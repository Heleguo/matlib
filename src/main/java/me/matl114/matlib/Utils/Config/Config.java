package me.matl114.matlib.Utils.Config;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.matl114.matlib.Utils.Debug;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Config {
    //for load when startup

    @Getter
    @Setter
    private boolean updateDataWhenModified = true;
    @Getter
    @Setter
    private boolean saveDataWhenModified = false;
    HashMap<String,Object> data;
    @Getter
    Logger logger;
    RootNode root;
    public Config(HashMap<String,Object> data) {
        this.data = data;
        this.root= new RootNode();

    }
    //get the HashMap s.t. map.get(path[path.length-1])=the needed Object
    private HashMap<String, Object> getOrCreateParentPathInRawData(String... targetPath){
        HashMap<String,Object> value=this.data;

        for(int i=0;i<targetPath.length-1;i++){
            value=(HashMap<String, Object>)(Object) value.compute(targetPath[i],(k, ob)->{
                if(! (ob instanceof HashMap<?,?>)){
                    return new LinkedHashMap<>();
                }
                return ob;
            });
        }

        return value;
      // value.put(path[path.length-1],val);
    }
    private void updateFromNode(){
        if(!this.isUpdateDataWhenModified()){
            this.data=buildMapFromNode(this.root);
        }
    }
    private HashMap<String,Object> buildMapFromNode(InnerNode node){
        Collection<String> keys=node.getKeys();
        HashMap<String,Object> value=new LinkedHashMap<>();
        for(String key:keys){
            Node childe=node.getChild(key);
            if(childe instanceof InnerNode inner){
                value.put(key,buildMapFromNode(inner));
            }else if(childe instanceof LeafNode inner){
                value.put(key,inner.getData().get());
            }
        }
        return value;
    }
    //for save
    protected final Map<String,Object> getRawData(){
        return data;
    }
    //配置文件节点
    protected abstract class Node{
        //abstract Collection<String> getKeys();
        @Getter
        @Setter(AccessLevel.PROTECTED)
        private Node parent;
        @Getter
        @Setter(AccessLevel.PROTECTED)
        private String pkey;
        @Getter
        @Setter(AccessLevel.PROTECTED)
        private boolean valid;
        public Node(){

        }
        public void markDirty(){
            if(this.isValid()){
                Config.this.makeDirtyInternal(this);
            }
        }
    }

    protected class InnerNode extends Node {
        public InnerNode(){
            super();
        }
        private final HashMap<String,Node> children = new HashMap<>();
        public Collection<String> getKeys(){
            return  children.keySet();
        }
        public Node getChild(String key) {
            return children.get(key);
        }
        public void addChild(String key, Node child) {
            child.setParent(this);
            child.setPkey(key);
            child.setValid(true);
            if(children.containsKey(key)){
                removeChild(key);
            }
            children.put(key, child);
            markDirty();
        }
        public Node removeChild(String key) {
            Node node= children.remove(key) ;
            if(node != null){
                node.setParent(null);
                node.setValid(false);
            }
            markDirty();
            return node;
        }
        public void clearChild(){
            this.children.clear();
            markDirty();
        }
    }
    protected final class RootNode extends InnerNode{
        public RootNode(){
            super();
            Preconditions.checkArgument(Config.this.root == null||!Config.this.root.isValid(),"Node Error in Config! this Config Tree already has a Root");
            Config.this.root = this;
            HashMap<String,Object> loaded= Config.this.data;
            loadInternal(this,loaded);
            //when not valid, markDirty method will not work for this Node
            setPkey(null);
            setParent(null);
            setValid(true);
        }
        private void loadInternal(InnerNode nowNode,HashMap<String,Object> data){
            for(Map.Entry<String,Object> entry:data.entrySet()){
                if(entry.getValue() instanceof HashMap map){
                    InnerNode node=new InnerNode();
                    loadInternal(node,(HashMap<String, Object>) map);
                    nowNode.addChild(entry.getKey(),node);
                }else {
                    LeafNode<?> node=new LeafNode<>(entry.getValue());
                    nowNode.addChild(entry.getKey(),node);
                }
            }
        }
    }
    protected class LeafNode<W> extends Node {
        @Getter
        private final ConfigReference<W> data;
        public LeafNode(W value) {
            super();
            this.data=ConfigReference.of(value,(w)->{
                this.markDirty();
            });
        }
    }
    private void makeDirtyInternal(Node node){
        if(isUpdateDataWhenModified()){
            Node modifiedNode=node;
            List<String> modifiedPath=new ArrayList<>();
            while(node.getPkey()!=null&&node.getParent()!=null){
                modifiedPath.add(node.getPkey());
                node=node.getParent();
            }
            if(node==root){
                if(root.isValid()){
                    //如果可以回溯到root 说明没断
                    String[] path=modifiedPath.toArray(String[]::new);
                    HashMap<String,Object> parentNode= getOrCreateParentPathInRawData(path);
                    if(modifiedNode instanceof LeafNode<?> leaf){
                        parentNode.put(path[path.length-1],leaf.getData().get());
                    }else if(modifiedNode instanceof InnerNode inner){
                        var iter=parentNode.keySet().iterator();
                        HashSet<String> newPaths=new HashSet<>( inner.getKeys());
                        //删除的key
                        while(iter.hasNext()){
                            String pkey=iter.next();
                            if(!newPaths.contains(pkey)){
                                iter.remove();
                            }else {
                                newPaths.remove(pkey);
                            }
                        }
                        //新出现的key
                        for(String left:newPaths){
                            Node child=inner.getChild(left);
                            if(child instanceof InnerNode leaf){
                                parentNode.put(left,buildMapFromNode(leaf));
                            }else if(child instanceof LeafNode<?> leaf){
                                parentNode.put(left,leaf.getData().get());
                            }
                        }
                    }
                }else{
                    return ;
                }
            }else{
                //并不是这个root下的节点,标记为已经废弃的节点,将不会继续markDirty
                modifiedNode.setValid(false);
                return;
            }
        }
        if(isSaveDataWhenModified()){
            save();
        }
    }
    private InnerNode getOrCreateNode(String[] path){
        InnerNode parent=this.root;
        for (int i=0;i<path.length;++i){
            Node nextNode=parent.getChild(path[i]);
            if(nextNode instanceof InnerNode innerNode){
                parent=innerNode;
            }else {
                var re=new InnerNode();
                parent.addChild(path[i],re);
                parent=re;
            }
        }
        return parent;
    }
    private InnerNode getOrCreateParentNode(String[] path){
        return getOrCreateNode(Arrays.copyOfRange(path,0,path.length-1));
    }
    public ConfigReference get(String... path){
        InnerNode parentNode= getOrCreateParentNode(path);
        if(parentNode.getChild(path[path.length-1]) instanceof LeafNode<?> leaf){
            return leaf.getData();
        }else {
            return null;
        }
    }
    public <T extends Object> ConfigReference<T> getOrDefault(T value,String... path){
        InnerNode parentNode= getOrCreateParentNode(path);
        if(parentNode.getChild(path[path.length-1]) instanceof LeafNode<?> leaf){
            var re=leaf.getData().get();
            if(value.getClass().isInstance(re)){
                return (ConfigReference<T>) leaf.getData();
            }
        }
        LeafNode<T> newNodeToReplace=new LeafNode<>(value);
        parentNode.addChild(path[path.length-1],newNodeToReplace);
        return newNodeToReplace.getData();
    }
    public void setValue(Object value,String... path){
        getOrCreateParentNode(path).addChild(path[path.length-1],new LeafNode<>(value));
    }
    public void clear(){
        this.root.clearChild();
    }
    protected void reloadInternal(HashMap<String,Object> data){
        this.root.setValid(false);
        this.root=null;
        this.data=data;
        this.root=new RootNode();
    }
    public <T extends Config> T save(){
        if(this.getFile()!=null){
            save(this.getFile());
        }
        return (T)this;
    }
    public abstract File getFile();
    public abstract void save(File file);
    //todo reload logic
    public abstract void reload();

    public boolean contains(@Nonnull String... path) {
        return get(path)!=null;
    }
    public boolean createFile() {
        try {
            return this.getFile().createNewFile();
        } catch (IOException var2) {
            IOException e = var2;
            Debug.severe("Exception while creating a Config file",e.getMessage());
            //this.logger.log(Level.SEVERE, "Exception while creating a Config file", e);
            return false;
        }
    }
    public Collection<String> getKeys() {
        return this.root.getKeys();
    }
    @Nonnull
    public Collection<String> getKeys(@Nonnull String... path) {
        return getOrCreateNode(path).getKeys();
    }
    public HashSet<String> getPaths(){
        HashSet<String> paths=new HashSet<>();
        for(String val:this.root.getKeys()){
            if(this.root.getChild(val) instanceof InnerNode map2){
                HashSet<String> p=getPaths(map2,val);
                paths.addAll(p);
            }else{
                paths.add(val);
            }
        }
        return paths;
    }
    private static HashSet<String> getPaths(InnerNode map,String parent){
        HashSet<String> paths=new HashSet<>();
        for(String val:map.getKeys()){
            if(map.getChild(val) instanceof InnerNode map2){
                HashSet<String> p=getPaths(map2,val);
                paths.addAll(p.stream().map(str->(String)parent+"."+str).collect(Collectors.toSet()));
            }else{
                paths.add(parent+"."+val);
            }
        }
        return paths;
    }
    public static String[] cutToPath(String rawPath){
        return rawPath.split("\\.");
    }
    public static Map<String,Object> deepCopyTree(Map<String,Object> origin){
        //todo
        return null;
    }
}
