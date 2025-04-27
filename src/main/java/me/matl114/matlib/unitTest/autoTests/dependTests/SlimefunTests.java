package me.matl114.matlib.unitTest.autoTests.dependTests;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.attributes.DistinctiveItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.matl114.matlib.implement.slimefun.manager.BlockDataCache;
import me.matl114.matlib.implement.bukkit.schedule.ScheduleManager;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.reflect.wrapper.FieldAccess;
import me.matl114.matlib.utils.reflect.wrapper.MethodAccess;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

public class SlimefunTests implements TestCase {
    @OnlineTest(name = "Slimefun blockData test")
    public void test_blockDataTest(){
        SlimefunItem testItem = SlimefunItem.getByItem( SlimefunItems.ELECTRIC_ORE_GRINDER_3 );
        World testWorld = Bukkit.getWorlds().get(0);
        Location location = new Location(testWorld,0,1,0);
        BlockDataCache.getManager().removeBlockData(location);
        SlimefunBlockData data = BlockDataCache.getManager().createBlockData(location,testItem);
        Debug.logger(data.isDataLoaded());
        BlockDataCache.getManager().setCustomString(location,"not","ok");
        Debug.logger(data.getData("not"));
        Config cfg = BlockStorage.getLocationInfo(location);
        Debug.logger(cfg.getClass());
        cfg.setValue("not",null);
        Debug.logger(data.getData("not"));
    }
    @OnlineTest(name = "Slimefun BlockStorage test")
    public void test_blockStorageTest(){
        SlimefunItem testItem = SlimefunItem.getByItem( SlimefunItems.ELECTRIC_ORE_GRINDER_3 );
        World testWorld = Bukkit.getWorlds().get(0);
        Location location = new Location(testWorld,3,7,2);
        BlockDataCache.getManager().removeBlockData(location);
        SlimefunBlockData data = BlockDataCache.getManager().createBlockData(location,testItem);
        Debug.logger(data.isDataLoaded());
        AtomicBoolean flag = new AtomicBoolean(false);
        ScheduleManager.getManager().launchScheduled(()->{
            boolean flag1 = flag.get();
            BlockStorage.addBlockInfo(location,"test",flag1?"ok":null);
            flag.set(!flag1);
        },10,false,1);
        Debug.logger("launched Machine-BlockStorage-behaviour Simulation Thread");
    }
    @OnlineTest(name = "sf block menu test")
    public void test_blockMenu(){
        BlockMenuPreset preset = new BlockMenuPreset("BYD","bydbyd") {
            @Override
            public void init() {
                this.addItem(36, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
            }

            @Override
            public boolean canOpen(@NotNull Block block, @NotNull Player player) {
                return true;
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow itemTransportFlow) {
                return IntStream.range(0,37).toArray();
            }
        };
        BlockMenu menu = new BlockMenu(preset, new Location(testWorld(),3,64,3));
        Debug.logger(menu.getInventory().getSize());
        menu.addMenuClickHandler(-1,ChestMenuUtils.getEmptyClickHandler());
        Debug.logger(menu.getInventory().getSize());
    }
    @OnlineTest(name = "distinctive test")
    public void test_distinctive(){
        for (SlimefunItem item: Slimefun.getRegistry().getAllSlimefunItems()){
            if(item instanceof DistinctiveItem && (item.getItem().getType() == Material.PLAYER_HEAD || item.getItem().getType() == Material.SUGAR)){
                Debug.logger(item);
            }
        }
    }

    protected static char[] GCE_GENE_DISPLAY_L=new char[]{'b','c','d','f','s','w'};
    protected static char[] GCE_GENE_DISPLAY_U=new char[]{'B','C','D','F','S','W'};
   // @OnlineTest(name = "Slimefun Gce test")
    public void test_GceTest() throws Throwable{
        HashMap<String,String> dna2Id = new HashMap<>();
        HashMap<String,String> dna2Name = new HashMap<>();
        Class utilClass =  Class.forName("net.guizhanss.gcereborn.utils.ChickenUtils");

        MethodAccess<ItemStack> access = MethodAccess.reflect("getResource", utilClass, ItemStack.class);//ofName(utilClass,"getResource",ItemStack.class).initWithNull().printError(true);
        for (SlimefunItem items : Slimefun.getRegistry().getAllSlimefunItems()){
            if(items.getId().startsWith("GCE_")&& items.getId().endsWith("_CHICKEN_ICON")){
                String materialId = items.getId().substring("GCE_".length(),items.getId().length()-"_CHICKEN_ICON".length());
                NamespacedKey key = new NamespacedKey("geneticchickengineering","gce_pocket_chicken_dna");
                ItemStack realChicken = items.getRecipe()[4];
                PersistentDataContainer container = realChicken.getItemMeta().getPersistentDataContainer();
                int[] dna = container.get(key, PersistentDataType.INTEGER_ARRAY);

                StringBuilder dnaSequence =  new StringBuilder();
                for (int i=0;i<6;i++){
                    if(dna[i] == 0 ){
                        dnaSequence.append(GCE_GENE_DISPLAY_L[i]).append(GCE_GENE_DISPLAY_L[i]);
                    }else {
                        dnaSequence.append(GCE_GENE_DISPLAY_U[i]).append(GCE_GENE_DISPLAY_U[i]);
                    }
                }
                String dnat = dnaSequence.toString();
                Debug.logger(dnat);
                ItemStack stack = access.invoke(null,realChicken);
                dna2Id.put(dnat,stack.getType().toString().toLowerCase(Locale.ROOT)+"|"+ Slimefun.getItemDataService().getItemData(stack).orElse("null"));
                dna2Name.put(dnat,materialId.toLowerCase(Locale.ROOT)+"_chicken");
            }
        }
        Debug.logger(dna2Id);
        Debug.logger(dna2Name);
    }
    private static String getItemFormat(ItemStack item){
        String mat = item.getType().toString();
        if(item.getType()==Material.PLAYER_HEAD && item.getItemMeta() instanceof SkullMeta meta1){
            URL t= meta1.getOwnerProfile().getTextures().getSkin();
            String path=t.getPath();
            String[] parts=path.split("/");
            mat+="$"+  parts[parts.length-1];
        }
        return mat;
    }
    //@OnlineTest(name = "Slimefun cultivation test")
    public void test_cultivation() throws Throwable {
//        HashMap<String, RandomizedSet<ItemStack>> id2Result = new HashMap<>();
//        for(SlimefunItem item: Slimefun.getRegistry().getAllSlimefunItems()){
//            if(item.getClass().getSimpleName().equals("HarvestableBush")){
//                id2Result.put(item.getId(),(RandomizedSet<ItemStack>) FieldAccess.ofName("harvestItems").noSnapShot().getValue(item)) ;
//            }
//        }
//        Map<String, List<Pair<String,String>>> id2ResultStackList = id2Result.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,(entry)->{
//            RandomizedSet<ItemStack> item = entry.getValue();
//            List<Pair<String,String>> lst = ((Set<WeightedNode<ItemStack>>) FieldAccess.ofName(RandomizedSet.class,"internalSet").ofAccess(item).getRaw()).stream().map(WeightedNode::getObject).map(i->{
//                return new Pair<>(getItemFormat(i),Slimefun.getItemDataService().getItemData(i).orElse("null"));
//            }).toList();
//            return lst;
//
//        }));
//        Debug.logger(id2ResultStackList);
//
        HashMap<String,List<Pair<String,String>>> tree2Product = new HashMap<>();
        for (SlimefunItem item: Slimefun.getRegistry().getAllSlimefunItems()){
            if(item.getClass().getSimpleName().equals("CultivationTree")){
                SlimefunItem product = (SlimefunItem) FieldAccess.ofName("produce").ofAccess(item).getRaw();
                tree2Product.put(item.getId(), List.of(new Pair<>(getItemFormat(product.getItem()),product.getId())));
            }
        }
        Debug.logger(tree2Product);
    }
   // @OnlineTest(name = "Slimefun id name test")
    public void test_slimefunitemid() throws  Throwable{
        HashMap<String, me.matl114.matlib.algorithms.dataStructures.struct.Pair<String, String>> map = new LinkedHashMap<>();
        for (SlimefunItem item: Slimefun.getRegistry().getAllSlimefunItems()){
            ItemStack item1 = item.getItem();
            var meta = item1.getItemMeta();
            if(meta != null && meta.getDisplayName() !=null){
                map.put(item.getId(), me.matl114.matlib.algorithms.dataStructures.struct.Pair.of(ChatColor.stripColor(meta.getDisplayName()), item.getAddon().getName()));
            }
        }
        Debug.logger(map);
    }
}
