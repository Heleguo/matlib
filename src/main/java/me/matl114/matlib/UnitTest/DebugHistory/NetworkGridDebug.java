package me.matl114.matlib.UnitTest.DebugHistory;

import com.github.houbb.pinyin.constant.enums.PinyinStyleEnum;
import com.github.houbb.pinyin.util.PinyinHelper;
import com.google.common.base.Preconditions;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.sefiraat.networks.network.NodeDefinition;
import io.github.sefiraat.networks.slimefun.network.grid.AbstractGrid;
import io.github.sefiraat.networks.slimefun.network.grid.GridCache;
import io.github.sefiraat.networks.slimefun.network.grid.NetworkGrid;
import io.github.thebusybiscuit.slimefun4.api.items.ItemState;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import me.matl114.matlib.Utils.Debug;
import me.matl114.matlib.Utils.Reflect.FieldAccess;
import me.matl114.matlib.Utils.Reflect.MethodAccess;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class NetworkGridDebug {
    static BlockTicker ticker;
    public static final Comparator<Map.Entry<ItemStack, Long>> ALPHABETICAL_SORT = Comparator.comparing(
            itemStackIntegerEntry -> {
                ItemStack itemStack = itemStackIntegerEntry.getKey();
                SlimefunItem slimefunItem = SlimefunItem.getByItem(itemStack);
                if (slimefunItem != null) {
                    return ChatColor.stripColor(slimefunItem.getItemName());
                } else {
                    return ChatColor.stripColor(ItemStackHelper.getDisplayName(itemStack));
                }
            },
            Collator.getInstance(Locale.CHINA)::compare
    );
    public static void startDebug(){
        AbstractGrid item =(AbstractGrid) SlimefunItem.getById("NTW_GRID");
        ticker = item.getBlockTicker();
        FieldAccess.AccessWithObject<ItemState> access= FieldAccess.ofName(SlimefunItem.class,"state").ofAccess(item);
        ItemState state = access.getRaw();
        access.set(ItemState.UNREGISTERED);
        Debug.logger("Hello world");
        MethodAccess<Void> addMethod = MethodAccess.ofName(AbstractGrid.class,"addToRegistry").printError(true);
        MethodAccess<Void> tryAdd = MethodAccess.ofName(AbstractGrid.class,"tryAddItem").printError(true);;
        MethodAccess<Void> updateDisplay = MethodAccess.ofName(AbstractGrid.class,"updateDisplay").printError(true);;
        Map<Location, GridCache> gridCacheMap =(Map<Location, GridCache>) FieldAccess.ofName(NetworkGrid.class,"CACHE_MAP").printError(true).ofAccess(item).getRaw();
        final MethodAccess<NodeDefinition> definitionAccess = MethodAccess.ofName(NetworkStorage.class,"getNode",Location.class).initWithNull().printError(true);
        MethodAccess<List<Map.Entry<ItemStack, Long>>> getEntryAccess = MethodAccess.ofName(AbstractGrid.class,"getEntries", NetworkRoot.class,GridCache.class).init(item).printError(true);
        MethodAccess<Map<ItemStack,Long>> getaaaaaa = MethodAccess.ofName(NetworkRoot.class,"getAllNetworkItemsLongType").printError(true);
        Map<?, Comparator<? super Map.Entry<ItemStack, Long>>> SORT_MAP = (Map<?, Comparator<? super Map.Entry<ItemStack, Long>>>) FieldAccess.ofName(AbstractGrid.class,"SORT_MAP").initWithNull().ofAccess(null).getRaw();
        try {
            MethodAccess<String> itemStackShitHelper = MethodAccess.ofName(Class.forName("com.balugaq.netex.api.helpers.ItemStackHelper"),"getDisplayName",ItemStack.class).initWithNull();
            Debug.logger(itemStackShitHelper.invoke(null,new ItemStack(Material.SPLASH_POTION)));
        } catch (Throwable e) {
            e.printStackTrace();
        }
        item.addItemHandler(new BlockTicker() {
            private int tick = 1;

            public boolean isSynchronized() {
                return true;
            }

            public void tick(Block block, SlimefunItem sfitem, SlimefunBlockData data) {
                if (this.tick <= 1) {
                    BlockMenu blockMenu = data.getBlockMenu();
                    if(blockMenu == null) {
                        return;
                    }
                    try {
                        addMethod.invoke(item,block);
                    } catch (Throwable e) {
                        Debug.logger("Error in Method 1");
                        e.printStackTrace();
                        return;
                    }
                    try {
                        tryAdd.invoke(item,blockMenu);
                    } catch (Throwable e) {
                        Debug.logger("Error in Method 2");
                        e.printStackTrace();
                        return;
                    }
                    if(!blockMenu.hasViewer()){

                        return;
                    }
                    final NodeDefinition definition;
                    try {
                        definition = definitionAccess.invoke(null,blockMenu.getLocation());
                    } catch (Throwable e) {
                        Debug.logger("error? in 1");
                        Debug.logger(blockMenu.getLocation().toString());
                        e.printStackTrace();
                        return;
                    }

                    // No node located, weird
                    if (definition == null || definition.getNode() == null) {
                        Debug.logger("Not Found");
                        return;
                    }

                    // Update Screen
                    final NetworkRoot root = definition.getNode().getRoot();

                    final GridCache gridCache = gridCacheMap.get(blockMenu.getLocation().clone());
                    AtomicInteger counter = new AtomicInteger(0);
                    int size = 0;
                    try {
                        //final List<Map.Entry<ItemStack, Long>> entries = getEntryAccess.invoke(item,root, gridCache);
                        Debug.logger("start Method test");
                        Debug.logger("check data :");
                        var a= getaaaaaa.invoke(root);
                        for (Map.Entry<ItemStack, Long> entry : a.entrySet()) {
                            if( entry==null|| entry.getValue() == null || entry.getKey() == null) {
                                Debug.logger("Dirty Data:",entry);
                            }
                        }
                        size = a.size();
                        Debug.logger("Map size",size);
                        var b= getaaaaaa.invoke(root).entrySet().stream().filter((entry) -> {
                            counter.incrementAndGet();
                            if(entry==null){
                                Debug.logger("Catch null Entry?????");
                            }
                            if (gridCache.getFilter() == null) {
                                return true;
                            } else {
                                Debug.logger("Not Ignored");
                                ItemStack itemStack = (ItemStack)entry.getKey();
                                Preconditions.checkNotNull(itemStack);
                                String name = ChatColor.stripColor(ItemStackHelper.getDisplayName(itemStack).toLowerCase(Locale.ROOT));
                                Preconditions.checkNotNull(name);
                                String pinyinName = PinyinHelper.toPinyin(name, PinyinStyleEnum.INPUT, "");
                                Preconditions.checkNotNull(pinyinName);
                                String pinyinFirstLetter = PinyinHelper.toPinyin(name, PinyinStyleEnum.FIRST_LETTER, "");
                                Preconditions.checkNotNull(pinyinFirstLetter);
                                return name.contains(gridCache.getFilter()) || pinyinName.contains(gridCache.getFilter()) || pinyinFirstLetter.contains(gridCache.getFilter());
                            }
                        });
                        Debug.logger("point2");
                        Debug.logger("check grid cache",gridCache.getSortOrder());
                        var china = Collator.getInstance(Locale.CHINA);
                        Debug.logger("check Chinese comparator",china);
                        //
                        Comparator<Map.Entry<ItemStack, Long>> comparator = (Comparator<Map.Entry<ItemStack, Long>>) SORT_MAP.get(gridCache.getSortOrder());
                        Debug.logger("check comparator",comparator);
                        var c= b.sorted(new Comparator<Map.Entry<ItemStack, Long>>() {
                            @Override
                            public int compare(Map.Entry<ItemStack, Long> o1, Map.Entry<ItemStack, Long> o2) {
                                try{
                                    return comparator.compare(o1,o2);
                                }catch (Throwable e){
                                    Debug.logger("Error in invoking comparator",o1.getKey(),o1.getValue(),o2.getKey(),o2.getValue());
                                    Debug.logger(e);
                                    return 0;
                                }

                            }
                        });
                        Debug.logger("point3");
                        Debug.logger(c.isParallel());
                        var d = c.toList();
                        Debug.logger("point4");
                        Debug.logger("check ",d.size());
                        getEntryAccess.invoke(item,root,gridCache);
                        Debug.logger("origin test pass");
                    } catch (Throwable e) {
                        Debug.logger("Error in Method 3");
                        Debug.logger("Execute %d of %d filter".formatted(counter.get(),size));
                        e.printStackTrace();
                        return;
                    }
                    for (int i=0;i<100;++i){
                        try {
                            getEntryAccess.invoke(item,root,gridCache);
                        }catch (Throwable e){}
                    }
                }

            }

            public void uniqueTick() {
                this.tick = this.tick <= 1 ? (Integer) 1: this.tick - 1;
            }
        });
        access.set(ItemState.ENABLED);
    }
    public static void endDebug(){
        SlimefunItem item = SlimefunItem.getById("NTW_GRID");
        FieldAccess.AccessWithObject<ItemState> access= FieldAccess.ofName(SlimefunItem.class,"state").ofAccess(item);
        ItemState state = access.getRaw();
        access.set(ItemState.UNREGISTERED);
        item.addItemHandler(ticker);
        access.set(state);
    }
}
