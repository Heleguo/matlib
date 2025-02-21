package me.matl114.matlib.Utils.Inventory.Inventorys;

import me.matl114.matlib.Common.Lang.Annotations.Note;
import me.matl114.matlib.Utils.Inventory.InventoryRecords.InventoryRecord;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

@Note(note = "note that this class just provides a view to manipulate block inventory in an async-safe way")
public abstract class AsyncInventoryWrapper implements Inventory {
    boolean triggerDelayUpdate =false;
    Inventory handle;
    public static Inventory wrapOfCurrentThread(Plugin pl,@Nullable Inventory blockInventory) {
        if(blockInventory == null) {
            return null;
        }
        if(Bukkit.isPrimaryThread()){
            return blockInventory;
        }else {
            return new AsyncInventoryWrapper(blockInventory) {
                @Override
                public void delayChangeUpdateInternal() {
                    Bukkit.getScheduler().runTask(pl,()->{
                        Location loc = getLocation();
                        if(loc != null){
                            Block b= loc.getBlock();
                            b.setBlockData(b.getBlockData(),true);
                        }
                    });
                }
            };
        }
    }
    public static Inventory wrapOfCurrentThread(Plugin pl, InventoryRecord record) {
        if(!record.isVanillaInv()||Bukkit.isPrimaryThread()){
            return record.inventory();
        }else {
            return wrapOfCurrentThread(pl,record.inventory());
        }
    }
    public AsyncInventoryWrapper(Inventory inventory) {
        this.handle = inventory;
    }
    @Override
    public int getSize() {
        return this.handle.getSize();
    }

    @Override
    public int getMaxStackSize() {
        return this.handle.getMaxStackSize();
    }

    @Override
    public void setMaxStackSize(int i) {
        this.handle.setMaxStackSize(i);
    }

    @Override
    public ItemStack getItem(int i) {
        return this.handle.getItem(i);
    }

    @Override
    @Note(note = "this method may trigger block update and throw exceptions,but fortunately we catch it and schedule a delay block update")
    public void setItem(int i, ItemStack itemStack) {
        try{
            this.handle.setItem(i,itemStack);
        }catch (ArrayIndexOutOfBoundsException shit){
            //ignore out-of-bound setItem
            throw shit;
        }catch (Throwable e){
            delayChangeUpdate();
        }
    }

    @Override
    @Note(note = "Not recommended in async")
    public HashMap<Integer, ItemStack> addItem(ItemStack... itemStacks) throws IllegalArgumentException {
        try{
            return this.handle.addItem(itemStacks);
        }catch(Throwable e){
            delayChangeUpdate();

        }
        return new HashMap<>();

    }

    @Override
    @Note(note = "Not recommended in async")
    public HashMap<Integer, ItemStack> removeItem(ItemStack... itemStacks) throws IllegalArgumentException {
        try{
            return this.handle.removeItem(itemStacks);
        }catch(Throwable e){
            delayChangeUpdate();

        }
        return new HashMap<>();
    }

    @Override
    public ItemStack[] getContents() {
        return this.handle.getContents();
    }

    @Override
    @Note(note = "Not recommended in async")
    public void setContents(ItemStack[] itemStacks) throws IllegalArgumentException {
        try{
            this.handle.setContents(itemStacks);
        }catch (Throwable e){
            delayChangeUpdate();
        }
    }

    @Override
    public ItemStack[] getStorageContents() {

            return this.handle.getStorageContents();

    }

    @Override
    @Note(note = "Not recommended in async")
    public void setStorageContents(ItemStack[] itemStacks) throws IllegalArgumentException {
        try{
            this.handle.setStorageContents(itemStacks);
        }catch (Throwable e){
            delayChangeUpdate();
        }
    }

    @Override
    public boolean contains(Material material) throws IllegalArgumentException {
        return this.handle.contains(material);
    }

    @Override
    public boolean contains(ItemStack itemStack) {
        return this.handle.contains(itemStack);
    }

    @Override
    public boolean contains(Material material, int i) throws IllegalArgumentException {
        return this.handle.contains(material, i);
    }

    @Override
    public boolean contains(ItemStack itemStack, int i) {
        return this.handle.contains(itemStack, i);
    }

    @Override
    public boolean containsAtLeast(ItemStack itemStack, int i) {
        return this.handle.containsAtLeast(itemStack, i);
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(Material material) throws IllegalArgumentException {
        return this.handle.all(material);
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(ItemStack itemStack) {
        return this.handle.all(itemStack);
    }

    @Override
    public int first(Material material) throws IllegalArgumentException {
        return this.handle.first(material);
    }

    @Override
    public int first(ItemStack itemStack) {
        return this.handle.first(itemStack);
    }

    @Override
    public int firstEmpty() {
        return this.handle.firstEmpty();
    }

    @Override
    public boolean isEmpty() {
        return this.handle.isEmpty();
    }

    @Override
    @Note(note = "Not recommended in async")
    public void remove(Material material) throws IllegalArgumentException {
        try{
            this.handle.remove(material);
        }
        catch (Throwable e){
            delayChangeUpdate();
        }
    }

    @Override
    @Note(note = "Not recommended in async")
    public void remove(ItemStack itemStack) {
        try{
            this.handle.remove(itemStack);
        }catch (Throwable e){
            delayChangeUpdate();
        }
    }

    @Override
    @Note(note = "Not recommended in async")
    public void clear(int i) {
        try{
            this.handle.clear(i);
        }catch (Throwable e){
            delayChangeUpdate();
        }
    }

    @Override
    @Note(note = "Not recommended in async")
    public void clear() {
        try{
            this.handle.clear();
        }catch (Throwable e){
            delayChangeUpdate();
        }
    }

    @Override
    public List<HumanEntity> getViewers() {
        return this.handle.getViewers();
    }

    @Override
    public InventoryType getType() {
        return this.handle.getType();
    }

    @Override
    public InventoryHolder getHolder() {
        return this.handle.getHolder();
    }

    @Override
    public ListIterator<ItemStack> iterator() {
        return this.handle.iterator();
    }

    @Override
    public ListIterator<ItemStack> iterator(int i) {
        return this.handle.iterator(i);
    }

    @Override
    public Location getLocation() {
        return this.handle.getLocation();
    }
    @Note(note = "override of this method is available if multiple change update is required")
    public void delayChangeUpdate(){
        if(!triggerDelayUpdate){
            triggerDelayUpdate = true;
            delayChangeUpdateInternal();
        }
    }
    public abstract void delayChangeUpdateInternal();
}
