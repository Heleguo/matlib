package me.matl114.matlib.Utils;

import com.google.common.base.Preconditions;
import lombok.Getter;
import me.matl114.matlib.Algorithms.DataStructures.Frames.InitializeSafeProvider;
import me.matl114.matlib.Algorithms.DataStructures.Frames.InitializingTasks;
import me.matl114.matlib.Common.Lang.Annotations.NotRecommended;
import me.matl114.matlib.Common.Lang.Annotations.Note;
import me.matl114.matlib.Common.Lang.Annotations.UnsafeOperation;
import me.matl114.matlib.Utils.Inventory.InventoryRecords.InventoryRecord;
import me.matl114.matlib.Utils.Reflect.FieldAccess;
import me.matl114.matlib.Utils.Reflect.MethodAccess;
import me.matl114.matlib.Utils.Reflect.MethodInvoker;
import org.bukkit.Bukkit;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.List;

public class NMSInventoryUtils {
    private static final InitializingTasks INIT_TASK = new InitializingTasks(()->{
        Debug.logger("Initializing NMSInventoryUtils...");
    });
    @Getter
    @Note("class CraftInventory")
    private static final Class<?> craftInventoryClass =new InitializeSafeProvider<>(()->{
        Inventory inv = Bukkit.createInventory(new InventoryHolder() {
            Inventory thisInv;
            @Override
            public Inventory getInventory() {
                return thisInv;
            }
        },54);
        Class<?> craftCustomInv = inv.getClass();
        Class<?> superClass = craftCustomInv.getSuperclass();
        while(superClass != Object.class && superClass != null){
            craftCustomInv = superClass;
            superClass = craftCustomInv.getSuperclass();
        };
        Debug.debug(craftCustomInv);
        return craftCustomInv;
    }).v();
    @Getter
    private static final MethodAccess<?> getIInventoryAccess = MethodAccess.ofName(craftInventoryClass,"getInventory",new Class[0]).initWithNull();

    @Getter
    @Note("public IInventory getInventory()")
    private static final MethodInvoker<?> getIInventoryInvoker = new InitializeSafeProvider<>(getIInventoryAccess::getInvoker)
            .runNonnullAndNoError(()->Debug.logger("Successfully initialize CraftInventory.getInventory Method Invoker"))
            .v();

    @Getter
    @Note("interface IInventory")
    private static final Class<?> iInventoryClass = new InitializeSafeProvider<>(()->{
        Method rawMethod = getIInventoryAccess.getMethodOrDefault(()->null);
        Debug.debug(rawMethod);
        Debug.debug(rawMethod.getDeclaringClass(),rawMethod.getName(),rawMethod.getReturnType());
        return rawMethod.getReturnType();
    }).v();

    @Getter
    private static final MethodAccess<List<?>> getIIContentsAccess = MethodAccess.ofName(iInventoryClass,"getContents",new Class[0]).initWithNull();
    @Getter
    @Note("public List<net.minecraft.ItemStack> getContents()")
    private static final MethodInvoker<List<?>> getIIContentsInvoker = getIIContentsAccess.getInvoker();

    @UnsafeOperation
    @Note("record must be a vanilla Inv")
    public static void setTileInvItemNoUpdate(InventoryRecord record, int index, ItemStack item){
        Inventory bukkitInventory = record.inventory();
        if(record.isMultiBlockInv()){
            DoubleChestInventory first = (DoubleChestInventory)bukkitInventory;
            Inventory left = first.getLeftSide();
            int size = left.getSize();
            if(index < size){
                setInvInternal(left, index, item);
            }else {
                setInvInternal(first.getRightSide(), index - size, item);
            }
        }else{
            setInvInternal(bukkitInventory, index, item);
        }
    }
    @UnsafeOperation
    @NotRecommended
    @Note("inventory must be a block inventory, s.t. holder instanceof TileState or DoubleChest")
    public static void setTileInvItemNoUpdate(Inventory inventory, int index, ItemStack item){
        if(inventory instanceof DoubleChestInventory first){
            Inventory left = first.getLeftSide();
            int size = left.getSize();
            if(index < size){
                setInvInternal(left, index, item);
            }else {
                setInvInternal(first.getRightSide(), index - size, item);
            }
        }else {
            setInvInternal(inventory, index, item);
        }
    }
    @UnsafeOperation
    @NotRecommended
    @Note("inventory must be a CraftInventoryCustom, s.t. it is created through Bukkit.createInventory()")
    public static void setInvItem(Inventory inventory, int index, ItemStack item){
        setInvInternal(inventory, index, item);
    }
    @UnsafeOperation
    @NotRecommended
    @Note("inventory must be a CraftInventoryCustom, s.t. it is created through Bukkit.createInventory()")
    public static void setInvItemNoCopy(Inventory inventory, int index, ItemStack item){
        if(CraftUtils.isCraftItemStack(item)){
            setInvNoCopy(inventory, index, item);
        }else {
            setInvInternal(inventory, index, null);
        }
    }
    private static void setInvInternal(Inventory inventory, int index,@Nullable ItemStack item){
        try{
            Object iInventory = getIInventoryInvoker.invokeNoArg(inventory);
            List itemContents = getIIContentsInvoker.invokeNoArg(iInventory);
            itemContents.set(index, CraftUtils.getNMSCopy(item));
        }catch(ArrayIndexOutOfBoundsException SHIT){
            throw SHIT;
        }catch(Throwable e){
            Debug.logger("Error while doing nms Inventory modification");
            Debug.logger(e);
        }
    }
    private static void setInvNoCopy(Inventory inventory, int index,@Nonnull ItemStack item){
        try{
            Object iInventory = getIInventoryInvoker.invokeNoArg(inventory);
            List itemContents = getIIContentsInvoker.invokeNoArg(iInventory);
            itemContents.set(index, CraftUtils.getHandled(item));
        }catch (ArrayIndexOutOfBoundsException SHIT){
            throw SHIT;
        }catch (Throwable e){
            if(CraftUtils.isCraftItemStack(item)){
                throw new RuntimeException("Invalid Argument passed, can not access nms handle from "+(item.getClass()));
            }
            Debug.logger("Error while doing nms Inventory modification");
            Debug.logger(e);
        }
    }

    @UnsafeOperation
    public static void setTileInvContentsNoUpdate(InventoryRecord inventory, ItemStack... contents){
        Inventory bukkitInventory = inventory.inventory();
        int size = bukkitInventory.getSize();
        Preconditions.checkArgument(contents.length <= size, "Invalid inventory size (%s); expected %s or less", contents.length, size);
        if(inventory.isMultiBlockInv()){
            DoubleChestInventory first = (DoubleChestInventory)bukkitInventory;
            Inventory left = first.getLeftSide();
            int sizeA = left.getSize();
            if(contents.length < sizeA){
                setInvContentInternal(left,contents,0,contents.length);
            }else {
                setInvContentInternal(left,contents,0,sizeA);
                setInvContentInternal(first.getRightSide(),contents,sizeA,contents.length);
            }
        }else{
            setInvContentInternal(bukkitInventory,contents,0,contents.length);
        }
    }
    private static void setInvContentInternal(Inventory inventory, ItemStack[] item,int start,int end){
        try{
            Object iInventory = getIInventoryInvoker.invokeNoArg(inventory);
            List itemContents = getIIContentsInvoker.invokeNoArg(iInventory);
            for (int i=start; i<end; i++){
                itemContents.set(i - start,CraftUtils.getNMSCopy(item[i]));
            }
        }catch(ArrayIndexOutOfBoundsException SHIT){
            throw SHIT;
        }catch(Throwable e){
            Debug.logger("Error while doing no-update blockInventory modification");
            Debug.logger(e);
        }
    }

//
//    @Note("InventoryLargeChest.class")
//    private static Class<?> doubleChestInvClass;
//
//
//    @Note("public final IInventory container1")
//    private static FieldAccess doubleChestInv1;
//
//
//    @Note("public final IInventory container2")
//    private static FieldAccess doubleChestInv2;
//
//    public void initDoubleChest(DoubleChestInventory inv){
//
//    }






}
