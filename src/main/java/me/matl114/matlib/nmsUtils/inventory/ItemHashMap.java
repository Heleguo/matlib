package me.matl114.matlib.nmsUtils.inventory;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import me.matl114.matlib.algorithms.dataStructures.struct.Pair;
import me.matl114.matlib.nmsUtils.ItemUtils;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiFunction;

public class ItemHashMap <T> extends Object2ObjectOpenCustomHashMap<ItemStack,T> {
    public static final StrategyItemHash DEFAULT_ITEM_STRATEGY = new StrategyItemHash();
    public static final StrategyItemNoLoreHash NO_LORE_ITEM_STRATEGY = new StrategyItemNoLoreHash();
    public ItemHashMap(Strategy<ItemStack> customStrategy){
        super(customStrategy);
    }
    public ItemHashMap(int expected, Strategy<ItemStack> customStrategy){
        super(expected, customStrategy);
    }
    public ItemHashMap(boolean considerLore){
        super(considerLore? DEFAULT_ITEM_STRATEGY: NO_LORE_ITEM_STRATEGY);
    }
    public ItemHashMap(int expected, boolean considerLore) {
        super(expected, considerLore? DEFAULT_ITEM_STRATEGY: NO_LORE_ITEM_STRATEGY);
    }
    public ItemHashMap(Object2ObjectMap<ItemStack, T> map, boolean considerLore){
        super(map, considerLore? DEFAULT_ITEM_STRATEGY: NO_LORE_ITEM_STRATEGY);
    }
    public ItemHashMap(ItemStack[] key, T[] value, boolean considerLore){
        super(key, value, considerLore? DEFAULT_ITEM_STRATEGY: NO_LORE_ITEM_STRATEGY);
    }

    @Override
    public T put(ItemStack itemStack, T t) {
        return super.put(ItemUtils.cleanStack(itemStack), t);
    }

    @Override
    public boolean remove(Object k, Object v) {
        if(k instanceof ItemStack stack){
            return super.remove(ItemUtils.cleanStack(stack), v);
        }else {
            return false;
        }
    }
    @Override
    public T get(Object val){
        if(val instanceof ItemStack stack){
            return super.get(ItemUtils.cleanStack(stack));
        }
        return null;
    }

    @Override
    public boolean containsKey(Object k) {
        if(k instanceof ItemStack stack){
            return super.containsKey(ItemUtils.cleanStack(stack));
        }else {
            return false;
        }
    }

    @Override
    public boolean replace(ItemStack itemStack, T oldValue, T t) {
        return super.replace(ItemUtils.cleanStack(itemStack), oldValue, t);
    }

    @Override
    public T compute(ItemStack itemStack, BiFunction<? super ItemStack, ? super T, ? extends T> remappingFunction) {
        return super.compute(ItemUtils.cleanStack(itemStack), remappingFunction);
    }

    @Override
    public T computeIfPresent(ItemStack itemStack, BiFunction<? super ItemStack, ? super T, ? extends T> remappingFunction) {
        return super.computeIfPresent(ItemUtils.cleanStack(itemStack), remappingFunction);
    }

    @Override
    public T getOrDefault(Object k, T defaultValue) {
        if(k instanceof ItemStack stack){
            return super.getOrDefault(ItemUtils.cleanStack(stack), defaultValue);
        }else {
            return defaultValue;
        }
    }

    @Override
    public T putIfAbsent(ItemStack itemStack, T t) {
        return super.putIfAbsent(ItemUtils.cleanStack(itemStack), t);
    }

    public static class StrategyItemHash implements Strategy<ItemStack>{
        @Override
        public int hashCode(ItemStack itemStack) {
            return ItemUtils.itemStackHashCode(itemStack);
        }

        @Override
        public boolean equals(ItemStack itemStack, ItemStack k1) {
            return ItemUtils.matchItemStack(itemStack, k1, true);
        }
    }
    public static class StrategyItemNoLoreHash implements Strategy<ItemStack>{

        @Override
        public int hashCode(ItemStack itemStack) {
            return ItemUtils.itemStackHashCodeWithoutLore(itemStack);
        }

        @Override
        public boolean equals(ItemStack itemStack, ItemStack k1) {
            return ItemUtils.matchItemStack(itemStack, k1, false);
        }
    }
}
