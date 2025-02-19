package me.matl114.matlib.Utils;

import lombok.Getter;
import me.matl114.matlib.Algorithms.DataStructures.Frames.InitializeProvider;
import me.matl114.matlib.Algorithms.DataStructures.Frames.InitializeSafeProvider;
import me.matl114.matlib.Algorithms.DataStructures.Frames.InitializingTasks;
import me.matl114.matlib.Utils.ItemCache.ItemStackCache;
import me.matl114.matlib.Utils.Reflect.FieldAccess;
import me.matl114.matlib.core.EnvironmentManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;


import javax.annotation.Nonnull;
import java.lang.invoke.VarHandle;
import java.util.*;

public class CraftUtils {

    private static final EnumSet<Material> COMPLEX_MATERIALS = EnumSet.noneOf(Material.class);
    static{
        ItemMeta sampleMeta=new ItemStack(Material.STONE).getItemMeta();
        for(Material mat : Material.values()){
            if(mat.isItem()&&!mat.isAir()){
                ItemMeta testMeta=new ItemStack(mat).getItemMeta();
                if(testMeta!=null&& testMeta.getClass()!=sampleMeta.getClass()){
                    COMPLEX_MATERIALS.add(mat);
                }
            }
        }
    }
    private static final HashSet<Material> INDISTINGUISHABLE_MATERIALS = new HashSet<>() {{
        add(Material.BUNDLE);
    }};
    public static final ItemStack DEFAULT_ITEMSTACK=new ItemStack(Material.STONE);
    private static final InitializingTasks INIT_TASK = new InitializingTasks(()->{
        Debug.logger("Initializing CraftUtils...");
    });
    public static final ItemMeta NULL_META=(DEFAULT_ITEMSTACK.getItemMeta());
    public static final Class craftMetaItemClass =NULL_META.getClass();
    private static final ThreadLocal<Inventory> threadLocalInventory = ThreadLocal.withInitial(()->Bukkit.createInventory(new InventoryHolder() {
        Inventory inv;
        @Override
        public Inventory getInventory() {
            return inv;
        }
    }, InventoryType.CHEST));
   private static final ItemStack craftItemStack = new InitializeSafeProvider<>(ItemStack.class,()->{
       try{
           Inventory a = threadLocalInventory.get();
           a.setItem(0,DEFAULT_ITEMSTACK);
           return a.getItem(0);
       }catch (Throwable e){
           return null;
       }
   }).v();
    private static final Class craftItemStackClass =  new InitializeProvider<Class>(()->{
        return craftItemStack !=null ? craftItemStack.getClass() : null;
    }).v();
    @Getter
    private static final FieldAccess loreAccess = new InitializeSafeProvider<>(FieldAccess.class,()->{
        try{
            var CRAFTLORE= craftMetaItemClass.getDeclaredField("lore");
            CRAFTLORE.setAccessible(true);
            return FieldAccess.of(CRAFTLORE);
        }catch (Throwable e){
            Debug.logger("Meta reflection failed,please check the error");
            Debug.logger(e);
            return FieldAccess.ofFailure();
        }
    }).v();
    @Getter
    private static final FieldAccess displayNameAccess =  new InitializeSafeProvider<>(FieldAccess.class,()->{
        try{
            var CRAFTDISPLAYNAME= craftMetaItemClass.getDeclaredField("displayName");
            CRAFTDISPLAYNAME.setAccessible(true);
            return FieldAccess.of(CRAFTDISPLAYNAME);
        }catch (Throwable e){
            Debug.logger("Meta reflection failed,please check the error");
            Debug.logger(e);
            return FieldAccess.ofFailure();
        }
    }).v();
    @Getter
    private static final FieldAccess enchantmentAccess = new InitializeSafeProvider<>(FieldAccess.class,()->{
        try{
            var CRAFTDISPLAYNAME= craftMetaItemClass.getDeclaredField("enchantments");
            CRAFTDISPLAYNAME.setAccessible(true);
            return FieldAccess.of(CRAFTDISPLAYNAME);
        }catch (Throwable e){
            Debug.logger("Meta reflection failed,please check the error");
            Debug.logger(e);
            return FieldAccess.ofFailure();
        }
    }).v();
    @Getter
    private static final FieldAccess handledAccess = new InitializeSafeProvider<>(FieldAccess.class,()->{
        try{
            var CRAFTHANDLER= craftItemStackClass.getDeclaredField("handle");
            CRAFTHANDLER.setAccessible(true);
            return FieldAccess.of(CRAFTHANDLER);
        }catch (Throwable e){
            Debug.logger("Stack reflection failed,please check the error");
            Debug.logger(e);
            return FieldAccess.ofFailure();
        }
    }).v();
   // public static Field CRAFTHANDLER;
    private static final Class NMSITEMCLASS = new InitializeSafeProvider<>(Class.class,()->{
       try {
           return (Class) handledAccess.getValue(craftItemStack).getClass() ;
       } catch (Throwable e) {
           return null;
       }
    }).v();
    @Getter
    private static final VarHandle loreHandle = new InitializeSafeProvider<>(()->{
        return loreAccess.getVarHandleOrDefault(()->null);
    }).runNonnullAndNoError(()->Debug.logger("Successfully initialize CraftMetaItem.lore VarHandle")).v();
    @Getter
    private static final VarHandle displayNameHandle = new InitializeSafeProvider<>(()->{
        return displayNameAccess.getVarHandleOrDefault(()->null);
    }).runNonnullAndNoError(()->Debug.logger("Successfully initialize CraftMetaItem.displayName VarHandle")).v();
    @Getter
    private static final VarHandle enchantmentsHandle = new InitializeSafeProvider<>(()->{
        return enchantmentAccess.getVarHandleOrDefault(()->null);
    }).runNonnullAndNoError(()->Debug.logger("Successfully initialize CraftMetaItem.enchantments VarHandle")).v();
    @Getter
    private static final VarHandle handleHandle = new InitializeSafeProvider<>(()->{
        return handledAccess.getVarHandleOrDefault(()->null);
    }).runNonnullAndNoError(()->Debug.logger("Successfully initialize CraftItemStack.handle VarHandle")).v();

    private static final FieldAccess craftDelegateAccess = new InitializeSafeProvider<>(FieldAccess.class,()->{
        return null;
    }).runNonnullAndNoError(()->Debug.logger("Detected ItemStack.craftDelegate field")).v();

    //when running without slimefun:
    //don't use history record of crafting method
    //don't use parseSfId
    private static final InitializingTasks INIT_TASK_FINISH = new InitializingTasks(()->{
        Debug.logger("Successfully initialize CraftUtils...");
    });
    public static void setup(){

    }
    public static ItemStack getCraftCopy(ItemStack item,boolean throughInventorySafe){
        if(throughInventorySafe){
            Inventory inventory = threadLocalInventory.get();
            inventory.setItem(0,item);
            return inventory.getItem(0);
        }else{
            //todo not completed yet
            return getCraftCopy(item,true);
        }
    }
    public static boolean isCraftItemStack(ItemStack item){
        return craftItemStackClass.isInstance(item);
    }

    /**
     * if item a and item b both craftItemStack ,check if they have same handled NMSItemStack
     * @param a
     * @param b
     * @return
     */
    public static boolean sameCraftItem(ItemStack a, ItemStack b){
        if(craftItemStackClass.isInstance(a)&& craftItemStackClass.isInstance(b)){
            try{
                if (handleHandle!=null){
                    return handleHandle.get(a) == handleHandle.get(b);
                }
                return  handledAccess.getValue(a) == handledAccess.getValue(b);
            }catch (Throwable e){
                return false;
            }
        }else return false;
    }

    public static ItemStackCache getStackCache(ItemStack a){
        if(a==null)return null;
        //用于比较和
        return ItemStackCache.get(a);
    }

    public static boolean amountLargerThan(ItemStack thisItem,ItemStack thatItem){
        if(thisItem==null||thatItem==null){
            return thisItem==thatItem;
        }else{
            return thisItem.getAmount()>=thatItem.getAmount();
        }
    }
    public static void consumeThat(ItemStack thisItem,ItemStack thatItem){
        if(thisItem!=null&&thatItem!=null){
            thatItem.setAmount(thatItem.getAmount()-thisItem.getAmount());
        }
    }
    public static void consumeThat(int amount ,ItemStack thatItem){
        if(thatItem!=null){
            thatItem.setAmount(thatItem.getAmount()-amount);
        }
    }
    public static boolean matchItemCounter(ItemStackCache counter1, ItemStackCache counter2, boolean strictCheck){
        return matchItemCore(counter1,counter2,strictCheck);
    }
    //
    private static final List<ItemMatcher> registeredMatchers=new ArrayList<>();
    public static interface ItemMatcher{
        public Flags doMatch(ItemStack stack1,ItemStack stack2,boolean strictCheck);
    }
    public static interface CustomItemMatcher {
        public Optional<String> parseId(ItemMeta meta1);
        public Flags doMatch(String id,ItemMeta meta1,ItemMeta meta2);
    }
    public static void registerCustomMatcher(ItemMatcher runs){
        registeredMatchers.add(runs);
    }

    private static final List<CustomItemMatcher> registeredCustomMatchers=new ArrayList<>();
    public static void registerCustomItemIdHook(CustomItemMatcher matcher){
        registeredCustomMatchers.add(matcher);
    }
    public static boolean matchItemCore(ItemStackCache counter1, ItemStackCache counter2, boolean strictCheck) {

        ItemStack stack1=counter1.getItem();
        ItemStack stack2=counter2.getItem();
        if (stack1 == null || stack2 == null) {
            return stack1 == stack2;
        }
        Flags flag;
        for (var matcher:registeredMatchers){
            flag=matcher.doMatch(stack1,stack2,strictCheck);
            if(flag==Flags.ACCEPT){
                return true;
            }else if(flag==Flags.REJECT){
                return false;
            }
        }
        //match material
        if (stack1.getType() != stack2.getType()) {
            return false;
        }
        ItemMeta meta1= counter1.getMeta();
        ItemMeta meta2= counter2.getMeta();
        if(meta1==null||meta2==null ) {
            return meta2==meta1;
        }else if(meta1.getClass()!=meta2.getClass()){
            //class different ,probably do not match
            return false;
        }
        //if indistinguishable meta all return false
        if(INDISTINGUISHABLE_MATERIALS.contains(stack1.getType())){
            return false;
        }
        //match display name
        if(!matchDisplayNameField(meta1,meta2)) {
            return false;
        }
        //custommodeldata
        final boolean hasCustomOne = meta1.hasCustomModelData();
        final boolean hasCustomTwo = meta2.hasCustomModelData();
        if (hasCustomOne) {
            if (!hasCustomTwo || meta1.getCustomModelData() != meta2.getCustomModelData()) {
                return false;
            }
        } else if (hasCustomTwo) {
            return false;
        }


        //check special metas
        if(canQuickEscapeMetaVariant(meta1,meta2)){
            return false;
        }

        if(EnvironmentManager.getManager().getVersioned().differentSpecialMeta(meta1,meta2)){
            return false;
        }
        //check pdc
        if (!meta1.getPersistentDataContainer().equals(meta2.getPersistentDataContainer())) {
            return false;
        }

        //如果非严格并且是sfid物品比较
        Optional<String> stackId1 = Optional.empty() ; //= parseSfId(meta1);
        CustomItemMatcher matcher = null;
        for (CustomItemMatcher matcher1:registeredCustomMatchers){
            stackId1 = matcher1.parseId(meta1);
            if(stackId1.isPresent()){
                matcher = matcher1;
                break;
            }
        }
        //final String stackId2 = parseSfId(meta2);

        if (stackId1.isPresent()) {
            Flags distinctiveFlag = matcher.doMatch(stackId1.get(),meta1,meta2);// SlimefunUtils.checkDistinctive(stackId1,meta1,meta2);
            switch (distinctiveFlag) {
                case ACCEPT:return true;
                case REJECT:return false;
                default:
            }
            if(!strictCheck){
                return true;
            }
        }

        //粘液物品一般不可修改displayName和Lore
        //不然则全非sf物品
//        if(COMPLEX_MATERIALS.contains(stack1.getType())){
//            if(canQuickEscapeMaterialVariant(meta1,meta2)){
//                return false;
//            }
//        }
        //如果非严格且名字相同旧返回，反之则继续

//        if(!meta1.hasLore()||!meta2.hasLore()){
//            return meta1.hasLore()==meta2.hasLore();
//        }
        if ( !matchLoreField(meta1, meta2)) {
            return false;
            //对于普通物品 检查完lore就结束是正常的
        }else if(!strictCheck){
            return true;
        }
        //Strict check: Make sure enchantments match
        if (!matchEnchantmentsFields(meta1,meta2)) {
            return false;
        }

        final boolean hasAttributeOne = meta1.hasAttributeModifiers();
        final boolean hasAttributeTwo = meta2.hasAttributeModifiers();
        if (hasAttributeOne) {
            if (!hasAttributeTwo || !Objects.equals(meta1.getAttributeModifiers(),meta2.getAttributeModifiers())) {
                return false;
            }
        } else if (hasAttributeTwo) {
            return false;
        }
        return true;
    }
    public static boolean matchLoreField(@Nonnull ItemMeta meta1, @Nonnull ItemMeta meta2){
        if(loreHandle!=null){
            return Objects.equals(loreHandle.get(meta1),loreHandle.get(meta2));
        }else{
            return loreAccess.compareFieldOrDefault(meta1,meta2,()->Objects.equals(meta1.getLore(),meta2.getLore()));
        }


//        try{
//            Object lore1= (CRAFTLORE.get(meta1));
//            Object  lore2= (CRAFTLORE.get(meta2));
//
//            return  Objects.equals(lore1,lore2);
//        }catch (Throwable e){
//            return ;
//        }
    }

    //    public static ItemMeta getItemMeta(ItemStack it){
////        if(!INVOKE_STACK_SUCCESS)return it.getItemMeta();
////        if(craftItemStackClass.isInstance(it)){
////            return it.getItemMeta();
////        }
//        if(it.getClass()!=craftItemStackClass) {
//            try{
//            return (ItemMeta) ITEMSTACKMETA.get(it);
//            }catch (Throwable e){
//
//            }
//        }
//        return it.getItemMeta();
//
//    }
    public static boolean matchDisplayNameField(ItemMeta meta1, ItemMeta meta2){
        if(displayNameHandle!=null){
            return Objects.equals(displayNameHandle.get(meta1),displayNameHandle.get(meta2));
        }else{
            return displayNameAccess.compareFieldOrDefault(meta1,meta2,()->Objects.equals(meta1.getDisplayName(),meta2.getDisplayName()));
        }

//        try{
//            Object name1=(CRAFTDISPLAYNAME.get(meta1));
//            Object name2=(CRAFTDISPLAYNAME.get(meta2));
//            return name1.equals(name2);
//        }catch (Throwable e){
//            return meta1.getDisplayName().equals(meta2.getDisplayName());
//        }
    }

    public static boolean matchEnchantmentsFields(ItemMeta meta1,ItemMeta meta2){
        if(enchantmentsHandle!=null){
            return meta1.hasEnchants()? meta2.hasEnchants()&& Objects.equals(enchantmentsHandle.get(meta1),enchantmentsHandle.get(meta2)) : !meta2.hasEnchants();
        }else {
            return meta1.hasEnchants()? meta2.hasEnchants()&& displayNameAccess.compareFieldOrDefault(meta1,meta2,()->Objects.equals(meta1.getEnchants(),meta2.getEnchants())) : !meta2.hasEnchants();
        }
    }
//    private static Class CraftMetaBlockState;
//    private static Field blockEntityTag;
//    private static boolean hasFailed;


    public static boolean matchBlockStateMetaField(BlockStateMeta meta1, BlockStateMeta meta2){
        return EnvironmentManager.getManager().getVersioned().matchBlockStateMeta(meta1,meta2);
//        if(!hasFailed){
//            try{
//                if(CraftMetaBlockState==null){
//                    var field= ReflectUtils.getFieldsRecursively(meta1.getClass(),"blockEntityTag");
//                    CraftMetaBlockState=field.getB();
//                    blockEntityTag=field.getA();
//                    blockEntityTag.setAccessible(true);
//                    Debug.debug(CraftMetaBlockState);
//                    Debug.debug(blockEntityTag);
//                }
//                return Objects.equals(blockEntityTag.get(meta1),blockEntityTag.get(meta2));
//            }catch (Throwable e){
//                hasFailed=true;
//            }
//        }
//        return meta1.equals(meta2);
    }
    public static boolean matchItemStack(ItemStack stack1, ItemStack stack2,boolean strictCheck){
        if(stack1==null || stack2==null){
            return stack1 == stack2;
        }else {
            return matchItemCore(getStackCache(stack1), getStackCache(stack2),strictCheck);
        }
    }
    public static boolean matchItemStack(ItemStack counter1, ItemStackCache counter2, boolean strictCheck){
        if(counter1==null ){
            return counter2.getItem()==null;
        }else {
            return matchItemCore(getStackCache(counter1),counter2,strictCheck);
        }
    }
//    public static boolean matchLore(List<String> lore1,List<String> lore2,boolean strictMod){
//        if(strictMod){
//            if(lore1==null || lore2==null){
//                return lore1 == lore2;
//            }
//            if(lore1.size()!=lore2.size()){
//                return false;
//            }
//            int len=lore1.size();
//            String l1;
//            String l2;
//            for(int i=0;i<len;++i){
//                l1=lore1.get(i);
//                l2=lore2.get(i);
//                if(l1.length()!=l2.length()){
//                    return false;
//                }
//                if(!l1.equals(l2)){
//                    return false;
//                }
//            }
//            return true;
//        }else{
//            if(lore1==null || lore2==null){
//                return lore1 == lore2;
//            }
//            if(lore1.size()!=lore2.size()){
//                return false;
//            }
//            return lore1.hashCode()==lore2.hashCode();
//            /**
//             int len=lore1.size();
//             String l1;
//             String l2;
//             for(int i=0;i<len;++i){
//             l1=lore1.get(i);
//             l2=lore2.get(i);
//             if(l1.length()!=l2.length()){
//             return false;
//             }
//             if(l1.hashCode()!=l2.hashCode()){
//             return false;
//             }
//             }
//             return true;**/
//        }
//    }



    /**
     * pieces of shit copied from Network
     * @param metaOne
     * @param metaTwo
     * @return
     */
    public static boolean canQuickEscapeMetaVariant(@Nonnull ItemMeta metaOne, @Nonnull ItemMeta metaTwo) {
        if (metaOne instanceof Damageable instanceOne && metaTwo instanceof Damageable instanceTwo) {
            if (instanceOne.hasDamage() != instanceTwo.hasDamage()) {
                return true;
            }

            if (instanceOne.getDamage() != instanceTwo.getDamage()) {
                return true;
            }
        }
        if (metaOne instanceof Repairable instanceOne && metaTwo instanceof Repairable instanceTwo) {
            if (instanceOne.hasRepairCost() != instanceTwo.hasRepairCost()) {
                return true;
            }

            if (instanceOne.getRepairCost() != instanceTwo.getRepairCost()) {
                return true;
            }
        }
        if (metaOne instanceof BlockDataMeta instanceOne ) {
            if(metaTwo instanceof BlockDataMeta instanceTwo){
                if (instanceOne.hasBlockData() != instanceTwo.hasBlockData()) {
                    return true;
                }
            }else return true;
        }
        return false;
    }


}






























