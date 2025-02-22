package me.matl114.matlib.Utils.Inventory.Inventorys;

import me.matl114.matlib.Common.Lang.Annotations.NotRecommended;
import me.matl114.matlib.Common.Lang.Annotations.Note;
import me.matl114.matlib.Common.Lang.Annotations.UnsafeOperation;
import me.matl114.matlib.Utils.Inventory.InventoryRecords.InventoryRecord;
import me.matl114.matlib.Utils.Inventory.InventoryRecords.SimpleInventoryRecord;
import me.matl114.matlib.Utils.NMSInventoryUtils;
import me.matl114.matlib.Utils.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
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

@Note(value = "note that this class just provides a view to manipulate block inventory in an async-safe way")
public abstract class AsyncInventoryWrapper implements Inventory {
    boolean triggerDelayUpdate =false;
    Inventory handle;
    InventoryRecord record = null;
    @NotRecommended("use Inventory Record instead")
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
                        if(blockInventory.getHolder() instanceof TileState tile) {
                            WorldUtils.tileEntitySetChange(tile);
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
            return record.hasInv()?null:new AsyncInventoryWrapper(record) {
                @Override
                public void delayChangeUpdateInternal() {
                    Bukkit.getScheduler().runTask(pl, record::setChange);
                }
            };
        }
    }
    public AsyncInventoryWrapper(Inventory inventory) {
        this.handle = inventory;
        this.record = SimpleInventoryRecord.fromInventory(inventory,false);
    }
    public AsyncInventoryWrapper(InventoryRecord record){
        this.handle = record.inventory();
        this.record = record;
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
    @UnsafeOperation
    public void setItem(int i, ItemStack itemStack) {
        NMSInventoryUtils.setTileInvItemNoUpdate(record, i, itemStack);
        delayChangeUpdate();
    }

    @Override
    @NotRecommended
    @Note(value = "Not recommended in async")
    public HashMap<Integer, ItemStack> addItem(ItemStack... itemStacks) throws IllegalArgumentException {
        try{
            return this.handle.addItem(itemStacks);
        }catch(Throwable e){
            delayChangeUpdate();
        }
        return new HashMap<>();

    }

    @Override
    @NotRecommended
    @Note(value = "Not recommended in async")
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
    @UnsafeOperation
    public void setContents(ItemStack[] itemStacks) throws IllegalArgumentException {
        NMSInventoryUtils.setTileInvContentsNoUpdate(this.record,itemStacks);
    }

    @Override
    public ItemStack[] getStorageContents() {
        return this.handle.getStorageContents();
    }

    @Override
    @UnsafeOperation
    public void setStorageContents(ItemStack[] itemStacks) throws IllegalArgumentException {
        NMSInventoryUtils.setTileInvContentsNoUpdate(this.record,itemStacks);
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
    @NotRecommended
    @Note(value = "Not recommended in async")
    public void remove(Material material) throws IllegalArgumentException {
        try{
            this.handle.remove(material);
        }
        catch (Throwable e){
            delayChangeUpdate();
        }
    }

    @Override
    @NotRecommended
    @Note(value = "Not recommended in async")
    public void remove(ItemStack itemStack) {
        try{
            this.handle.remove(itemStack);
        }catch (Throwable e){
            delayChangeUpdate();
        }
    }

    @Override
    @NotRecommended
    @Note(value = "Not recommended in async")
    public void clear(int i) {
        try{
            this.handle.clear(i);
        }catch (Throwable e){
            delayChangeUpdate();
        }
    }

    @Override
    @NotRecommended
    @Note(value = "Not recommended in async")
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
    @Note(value = "override of this method is available if multiple change update is required")
    public void delayChangeUpdate(){
        if(!triggerDelayUpdate){
            triggerDelayUpdate = true;
            delayChangeUpdateInternal();
        }
    }
    public abstract void delayChangeUpdateInternal();
}
