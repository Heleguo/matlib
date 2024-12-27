package me.matl114.matlib.Utils.Config;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.matl114.matlib.Utils.Debug;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class Config {
    //for load when startup

    @Getter
    @Setter
    private boolean updateDataWhenModified = true;
    @Getter
    @Setter
    private boolean saveDataWhenModified = false;
    HashMap<String,Object> data;
//    @Getter
//    Logger logger;
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
    protected void updateRawData(){
        if(!this.isUpdateDataWhenModified()){
            this.data= dumpToMap(this.root);
        }
    }
    private HashMap<String,Object> dumpToMap(InnerNode node){
        Collection<String> keys=node.getKeys();
        HashMap<String,Object> value=new LinkedHashMap<>();
        for(String key:keys){
            Node childe=node.getChild(key);
            if(childe instanceof InnerNode inner){
                value.put(key, dumpToMap(inner));
            }else if(childe instanceof LeafNode inner){
                value.put(key,castWhenDump( inner.getData().get()));
            }
        }
        return value;
    }
    //for save
    protected final HashMap<String,Object> getRawData(){
        return data;
    }
    public Map<String,Object> getData(){
        updateRawData();
        return Collections.unmodifiableMap(this.data);
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

        /**
         * no copy
         * @return
         */
        public Collection<String> getKeys(){
            return  children.keySet();
        }
        public Collection<String> getKeysCopy(){
            return  new HashSet<>(getKeys());
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
                    LeafNode<?> node=new LeafNode<>(Config.this.castWhenLoad( entry.getValue()));
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
                        parentNode.put(path[path.length-1],castWhenDump( leaf.getData().get()));
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
                                parentNode.put(left, dumpToMap(leaf));
                            }else if(child instanceof LeafNode<?> leaf){
                                parentNode.put(left,castWhenDump( leaf.getData().get()));
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
    public ConfigReference<Integer> getInt(String... path){
        return getWithIdentifier(Integer.class,path);
    }
    public ConfigReference<String> getString(String... path){
        return getWithIdentifier(String.class,path);
    }
    public ConfigReference<Boolean> getBool(String... path){
        return getWithIdentifier(Boolean.class,path);
    }
    public ConfigReference<Double> getDouble(String... path ){
        return getWithIdentifier(Double.class,path);
    }
    public ConfigReference<Float> getFloat(String... path){
        return getWithIdentifier(Float.class);
    }


    /**
     * use value.getClass check the recorded value
     * @param value
     * @param path
     * @return
     * @param <T>
     */
    public  <T extends Object> ConfigReference<T> getOrDefault(@Nullable T value, String... path){
        InnerNode parentNode= getOrCreateParentNode(path);
        if(parentNode.getChild(path[path.length-1]) instanceof LeafNode<?> leaf){
            var re=leaf.getData().get();
            if(value==null|| value.getClass().isInstance(re)){
                return (ConfigReference<T>) leaf.getData();
            }
        }
        LeafNode<T> newNodeToReplace=new LeafNode<>(value);
        parentNode.addChild(path[path.length-1],newNodeToReplace);
        return newNodeToReplace.getData();
    }
    /**
     * use identfier check the recorded value and mark the record' identifier as this identifier
     * @param
     * @param path
     * @return
     * @param <T>
     */
    public  <T extends Object> ConfigReference<T> getWithIdentifier(@Nonnull Class<T> identifier,String... path){
        return getWithIdentifier(null,identifier,path);
    }
    /**
     * use identfier check the recorded value and mark the record' identifier as this identifier
     * @param
     * @param path
     * @return
     * @param <T>
     */
    public  <T extends Object> ConfigReference<T> getWithIdentifier(@Nullable T value,@Nonnull Class<T> identifier,String... path){
        InnerNode parentNode= getOrCreateParentNode(path);
        if(parentNode.getChild(path[path.length-1]) instanceof LeafNode<?> leaf){
            var re=leaf.getData().get();
            if(identifier.isInstance(re)){
                var returned=(ConfigReference<T>) leaf.getData();
                returned.setIdentifier(identifier);
                return returned;
            }
        }
        LeafNode<T> newNodeToReplace=new LeafNode<>(value);
        parentNode.addChild(path[path.length-1],newNodeToReplace);
        var returned=newNodeToReplace.getData();
        returned.setIdentifier(identifier);
        return returned;
    }

    /**
     * set the config's default value(null->value)
     * @param defaultVal
     * @param path
     * @return
     * @param <T>
     * @param <W>
     */
    public <T extends Object,W extends Config> W defaultValue(@Nullable T defaultVal,String... path){
        var re= getOrDefault(defaultVal,path);
        if(re.get()==null){
            re.set(defaultVal);
        }
        return (W)this;
    }
    /**
     * set the config's default value(null->value) (id->identifier)
     * @param defaultVal
     * @param path
     * @return
     * @param <T>
     * @param <W>
     */
    public <T extends Object,W extends Config> W defaultValue(@Nullable T defaultVal,Class<T> identifier,String... path){
        var re= getWithIdentifier(defaultVal,identifier,path);
        if(re.get()==null){
            re.set(defaultVal);
        }
        return (W)this;
    }
    public <T extends Object> void setValue(T value,String... path){
        InnerNode parentNode= getOrCreateParentNode(path);
        if(parentNode.getChild(path[path.length-1]) instanceof LeafNode leaf){
            Class clazz=leaf.getData().getIdentifier();
            if(clazz.isInstance(value)){
                leaf.getData().set(value);
                return;
            }
        }
        parentNode.addChild(path[path.length-1],new LeafNode<>(value));
    }
    public final void createLeaf(Object value,String... path){
        getOrCreateParentNode(path).addChild(path[path.length-1],new LeafNode<>(value));
    }
    public final void clear(){
        this.root.clearChild();
    }
    private final void reloadNode(InnerNode parentNode,HashMap<String,Object> data){
        Collection<String> oldKeys=parentNode.getKeysCopy();
        for(Map.Entry<String,Object> entry:data.entrySet()){
            String key=entry.getKey();
            Object value=entry.getValue();
            Node sonNode=parentNode.getChild(key);
            oldKeys.remove(key);
            if(value instanceof HashMap map){
                InnerNode next;
                if((sonNode instanceof InnerNode in)){
                    next=in;
                }
                else{
                    next=new InnerNode();
                    parentNode.addChild(key,next);
                }
                reloadNode(next,(HashMap<String, Object>) map);
            }else{
                if(sonNode instanceof LeafNode lea){
                    lea.getData().set(value);
                }else {
                    LeafNode leaf=new LeafNode(value);
                    parentNode.addChild(key,leaf);
                }
            }
        }
        oldKeys.forEach(parentNode::removeChild);

    }
    protected final void reloadInternal(HashMap<String,Object> data){
        //todo laod new value into olds
        reloadNode(this.root,data);
//        this.root.setValid(false);
//        this.root=null;
//        this.data=data;
//        this.root=new RootNode();
    }
    public final  <T extends Config> T save(){
        if(this.getFile()!=null){
            save(this.getFile());
        }
        return (T)this;
    }
    public abstract File getFile();
    public abstract void save(File file);

    /**
     * cast from hashmap to internal config tree: will cast non-map value to leaf-node value
     * @param val
     * @return
     */
    public abstract Object castWhenLoad(Object val);

    /**
     * cast from internal config tree to hashmap: will cast leaf node value to this
     * @param val
     * @return
     */
    public abstract Object castWhenDump(Object val);
    //todo reload logic

    public abstract void reload();

    public boolean contains(@Nonnull String... path) {
        return get(path)!=null;
    }
    public static boolean createFile(File file) {
        try {
            return file.createNewFile();
        } catch (IOException var2) {
            IOException e = var2;
            Debug.severe("Exception while creating a Config file",e.getMessage());
            //this.logger.log(Level.SEVERE, "Exception while creating a Config file", e);
            return false;
        }
    }
    public final Collection<String> getKeys() {
        return this.root.getKeys();
    }
    @Nonnull
    public final Collection<String> getKeys(@Nonnull String... path) {
        return getOrCreateNode(path).getKeys();
    }
    public final HashSet<String> getPaths(){
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
