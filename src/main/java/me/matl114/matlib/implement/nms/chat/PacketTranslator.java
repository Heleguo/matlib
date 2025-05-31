package me.matl114.matlib.implement.nms.chat;

import com.google.common.base.Preconditions;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import me.matl114.matlib.algorithms.dataStructures.frames.collection.ListMapView;
import me.matl114.matlib.algorithms.dataStructures.frames.collection.PairList;
import me.matl114.matlib.algorithms.dataStructures.frames.mmap.ValueAccess;
import me.matl114.matlib.core.Manager;
import me.matl114.matlib.implement.nms.network.*;
import me.matl114.matlib.nmsMirror.impl.CraftBukkit;
import me.matl114.matlib.nmsMirror.impl.NMSCore;
import me.matl114.matlib.nmsMirror.impl.NMSEntity;
import me.matl114.matlib.nmsMirror.impl.NMSLevel;
import me.matl114.matlib.nmsMirror.network.NetworkEnum;
import me.matl114.matlib.nmsUtils.ChatUtils;
import me.matl114.matlib.nmsUtils.ItemUtils;
import me.matl114.matlib.nmsUtils.PlayerUtils;
import me.matl114.matlib.utils.ThreadUtils;
import me.matl114.matlib.utils.chat.lan.LanguageRegistry;
import net.kyori.adventure.translation.Translator;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static me.matl114.matlib.nmsMirror.impl.NMSChat.*;
import static me.matl114.matlib.nmsMirror.impl.NMSItem.*;
import static me.matl114.matlib.nmsUtils.network.GamePacket.*;
import static me.matl114.matlib.nmsMirror.impl.NMSNetwork.*;

public class PacketTranslator implements Manager, PacketListener {
    //it is not a singleton so there is no INSTANCE static field here
    Plugin plugin;
    boolean registered;

    LanguageRegistry languageRegistry;
    private final Map<String, Map<Locale, List<Iterable<?>>>> siblingsCache = new ConcurrentHashMap<>();
    @Override
    public PacketTranslator init(Plugin pl, String... path) {
        Preconditions.checkArgument(!registered ,"This manager has been initialized");
        Preconditions.checkNotNull(PacketEventManager.getManager(), "This manager depends on PacketEventManager to run functionality");
        this.registered = true;
        this.plugin = pl;
        this.languageRegistry = null;
        this.siblingsCache.clear();
        PacketEventManager.getManager().registerListener(this, plugin);
        return this;
    }

    public PacketTranslator setLanguageRegistry(LanguageRegistry registry){
        this.languageRegistry = registry;
        return this;
    }

    private boolean containsTranslateKey(String keu){
        return this.languageRegistry != null && this.languageRegistry.containsPath(keu);
    }

    private List<Iterable<?>> getSiblingsFromKey(Locale locale, String key){
        //Locale support = this.languageRegistry.getSupport(locale);
        if(this.languageRegistry != null && this.languageRegistry.containsPath(key)){
            return this.siblingsCache.computeIfAbsent(key,(i)->new ConcurrentHashMap<>()).computeIfAbsent(locale, (loc1)->{
                String value =  this.languageRegistry.getColored(loc1, key);
                return value == null ? null: CHATCOMPONENT.getSiblings( ChatUtils.deserializeLegacy(value));
            });
        }else return null;
    }

    private Iterable<?> processSingleLayer(Locale locale, Iterable<?> component){
        Object content = CHATCOMPONENT.getContents(component);
        if(COMP_CONTENT.isTranslatable(content)){
            String fallback = COMP_CONTENT.translatable$getFallback(content);
            if(fallback != null){
                return null;
            }
            String key =COMP_CONTENT.translatable$getKey(content);
            List<Iterable<?>> list = getSiblingsFromKey(locale, key);
            if (list == null){
                return null;
            }
            Object newContent = COMP_CONTENT.newTranslatable(key, "", COMP_CONTENT.translatable$getArgs(content));
            Iterable<?> newComp = CHATCOMPONENT.create(newContent);
            CHATCOMPONENT.setStyle(newComp, CHATCOMPONENT.getStyle(component));
            List<Iterable<?>> mutableList = CHATCOMPONENT.getSiblings(newComp);
            mutableList.addAll(list);
            mutableList.addAll(CHATCOMPONENT.getSiblings(component));
            return newComp;
        }
        return null;
    }
    private Iterable<?> processComponentS2C(Locale locale, Iterable<?> component){
        Iterable<?> optionalLayer0 = processSingleLayer(locale, component);
        if (optionalLayer0 != null)return null;
        //check layer 1
        boolean findAny = false;
        for (var subComp : CHATCOMPONENT.getSiblings(component)){
            Object content = CHATCOMPONENT.getContents(subComp);
            if(COMP_CONTENT.isTranslatable(content)){
//                Debug.logger("find optional translate, with argument",COMP_CONTENT.translatable$getFallback(content),COMP_CONTENT.translatable$getKey(content));
                if(COMP_CONTENT.translatable$getFallback(content) == null && containsTranslateKey(COMP_CONTENT.translatable$getKey(content))){
                    findAny = true;
                    break ;
                }
            }
        }
        if(!findAny){
            //find NONE
            //Debug.logger("find no translate, do not copy");
            return null;
        }
        //copy if found
        if(CHATCOMPONENT.isAdventure(component)){
            component = CHATCOMPONENT.deepConverted(component);
        }
        component = CHATCOMPONENT.copy(component);
        List<Iterable<?>> mutableChildList = CHATCOMPONENT.getSiblings(component);
        int size = mutableChildList.size();
        for(int i=0; i<size;++i){
            Iterable<?> child0 = mutableChildList.get(i);
            Iterable<?> childReplace = processSingleLayer(locale, child0);
            if(childReplace != null){
                mutableChildList.set(i, childReplace);
            }
        }
        return component;
    }
    private boolean processComponentC2S(Locale locale, Iterable<?> component){
        //modify this is okkkkkkkkk, because it just born from C2S packet byteflows
        boolean shouldWrite = false;
        PairList<Iterable<?>,Integer> tobeModifiedTranslate = null;
        for (var subs : component){
            Iterable<?> subComp = (Iterable<?>) subs;
            if(CHATCOMPONENT.isMutableComp(subComp)){
                Object content = CHATCOMPONENT.getContents(subComp);
                //if this nbt is from our translation key (we put "" into the faked chatComponent to mark ourselves and from being detected by client

                if(COMP_CONTENT.isTranslatable(content) && Objects.equals("", COMP_CONTENT.translatable$getFallback(content))){
                    String key = COMP_CONTENT.translatable$getKey(content);
                    //check our key here;
                    if(containsTranslateKey(key)){
                        //it is our key
                        var estimatedNbt = this.siblingsCache.computeIfAbsent(key, (i)->new ConcurrentHashMap<>()).get(locale);
                        int lengthToRemove = estimatedNbt == null ? -1: estimatedNbt.size();
                        if(tobeModifiedTranslate == null)tobeModifiedTranslate = new PairList<>();
                        tobeModifiedTranslate.put(subComp, lengthToRemove);
                    }
                }

            }
        }
        //modify after walk
        if(tobeModifiedTranslate != null){
            for (var entry: tobeModifiedTranslate){
                //nop
                shouldWrite = true;
                Iterable<?> mutable = entry.getA();
                List<Iterable<?>> sibs = CHATCOMPONENT.getSiblings(mutable);
                int index = entry.getB();
                if(!sibs.isEmpty()){
                    if(sibs instanceof ArrayList<Iterable<?>>){
                        if(index == -1 || index >= sibs.size()){
                            sibs.clear();
                        }else{
                            sibs.subList(0, index).clear();
                        }
                    }else {
                        int size = sibs.size();
                        if(index == -1 || index >= size){
                            CHATCOMPONENT.forceReplaceSiblings(mutable, List.of());
                        }else{
                            List<Iterable<?>> left = new ArrayList<>(sibs.subList(index, size));
                            CHATCOMPONENT.forceReplaceSiblings(mutable, left);
                        }
                    }
                }
                //set back to null
                COMP_CONTENT.translatable$setFallback(CHATCOMPONENT.getContents(mutable), null);
            }
        }
        return shouldWrite;
    }
    private boolean wrongState(){
        return !this.registered;
    }
    private boolean processItemStackS2C(Object stack, Locale locale){
        if(ITEMSTACK.isEmpty(stack))return false;
        boolean hasModify = false;
        if(ITEMSTACK.hasCustomHoverName(stack)){
            ValueAccess<Iterable<?>> customName = ITEMSTACK.getDisplayNameView(stack);
            Iterable<?> name = customName.get();
            //only the server has fucking adventure , 桀桀桀
            Iterable<?> shouldWrite = processComponentS2C(locale, name);
            if(shouldWrite != null){
                hasModify = true;
                customName.set(shouldWrite);
            }
        }
        if(ITEMSTACK.hasLore(stack)){
            ListMapView<?,Iterable<?>> loreAccess = ITEMSTACK.getLoreView(stack, false);
            if(loreAccess != null){
                int size = loreAccess.size();
                for (int i=0; i< size ;++i){
                    Iterable<?> loreLine = loreAccess.get(i);
                    if(CHATCOMPONENT.isAdventure(loreLine)){
                        loreLine = CHATCOMPONENT.deepConverted(loreLine);
                    }
                    Iterable<?> shouldWrite = processComponentS2C(locale, loreLine);
                    if(shouldWrite != null){
                        //Debug.logger("write lore");
                        hasModify = true;
                        loreAccess.set(i, shouldWrite);
                       // Debug.logger("after write", loreAccess);
                    }
                }
                loreAccess.batchWriteback();
            }
           // Debug.logger("check after change", ItemUtils.asBukkitCopy(stack));
        }
//        if(hasModify){
//            Debug.logger("translate one");
//        }
        return hasModify;
    }
    private void processItemStackC2S(Object stack, Locale locale){
        if(ITEMSTACK.hasCustomHoverName(stack)){
            ValueAccess<Iterable<?>> customName = ITEMSTACK.getDisplayNameView(stack);
            Iterable<?> name = customName.get();
            //fuck you adventure

            boolean shouldWrite = processComponentC2S(locale, name);
            if(shouldWrite){
                customName.set(name);
            }
        }
        if(ITEMSTACK.hasLore(stack)){
            ListMapView<?,Iterable<?>> loreAccess = ITEMSTACK.getLoreView(stack, false);
            if(loreAccess != null){
                int size = loreAccess.size();
                for (int i=0; i< size ;++i){
                    Iterable<?> loreLine = loreAccess.get(i);
                    boolean shouldWrite = processComponentC2S(locale, loreLine);
                    if(shouldWrite){
                        loreAccess.set(i, loreLine);
                    }
                }
                loreAccess.batchWriteback();
            }
        }
    }
    private Locale handleLocale(PacketEvent event){
        Object player = event.getClient().getPlayer();
        return NMSLevel.PLAYER.locale(player);
    }
    @PacketHandler(type = SERVERBOUND_SET_CREATIVE_MODE_SLOT)
    public void onPacket1(PacketEvent event){
        if(wrongState())return;
        Locale locale = handleLocale(event);
        Object packet = event.getPacket();
        Object item = PACKETS.serverboundSetCreativeModeSlotPacket$itemStack(packet);
        processItemStackC2S(item, locale);

    }

    @PacketHandler(type = CLIENTBOUND_SET_CURSOR_ITEM)
    public void onPacket2(PacketEvent event){
        if(wrongState())return;
        Locale locale = handleLocale(event);
        Object packet = event.getPacket();
        Object item = PACKETS.clientboundSetCursorItemPacket$cursor(packet);
        processItemStackS2C(item, locale);
        //debugClient(event);
    }
    @PacketHandler(type = SERVERBOUND_CONTAINER_CLICK)
    public void onPacket3(PacketEvent event){
        if(wrongState())return;
        Locale locale = handleLocale(event);
        Object packet = event.getPacket();
        Object cursorItem =PACKETS.serverboundContainerClickPacket$CarriedItem(packet);
        processItemStackC2S(cursorItem, locale);
        Int2ObjectMap<?> changedItems = PACKETS.serverboundContainerClickPacket$ChangedSlots(packet);
        for (var entry: changedItems.int2ObjectEntrySet()){
            processItemStackC2S(entry.getValue(), locale);
        }

    }

    @PacketHandler(type =CLIENTBOUND_SET_PLAYER_INVENTORY)
    public void onPacket4(PacketEvent event){
        if(wrongState())return;
        Locale locale = handleLocale(event);
        Object packet = event.getPacket();
        Object item = PACKETS.clientboundSetPlayerInventoryPacket$SlotContent(packet);
        processItemStackS2C(item, locale);
        //debugClient(event);

    }
//    private void debugClient(PacketEvent event){
//        Debug.logger("check after modification", NMSLevel.PLAYER.getBukkitEntity(event.getClient().getPlayer()).getInventory().getItemInMainHand());
//    }
    @PacketHandler(type =CLIENTBOUND_CONTAINER_SET_SLOT)
    public void onPacket5(PacketEvent event){
        if(wrongState())return;
        Locale locale = handleLocale(event);
        Object packet = event.getPacket();
        Object item = PACKETS.clientboundContainerSetSlotPacket$SlotItem(packet);
        processItemStackS2C(item, locale);
       // debugClient(event);
    }

    @PacketHandler(type =CLIENTBOUND_SET_EQUIPMENT)
    public void onPacket6(PacketEvent event){
        if(wrongState())return;
        Locale locale = handleLocale(event);
        Object packet = event.getPacket();
        List<?> slotWithItem = PACKETS.clientboundSetEquipmentPacket$EquipmentSlots(packet);
        for (var pair: slotWithItem){
            if(pair instanceof Pair<?,?> pp){
                Object item = pp.getSecond();
                processItemStackS2C(item, locale);
            }
        }
       // debugClient(event);
    }
    @PacketHandler(type =CLIENTBOUND_CONTAINER_SET_CONTENT)
    public void onPacket7(PacketEvent event){
        if(wrongState())return;
        Locale locale = handleLocale(event);
        Object packet = event.getPacket();
        List<?> items = PACKETS.clientboundContainerSetContentPacket$getItems(packet);
        for (var item: items){

                processItemStackS2C(item, locale);

        }
        processItemStackS2C(PACKETS.clientboundContainerSetContentPacket$getCursorItem(packet), locale);
      //  debugClient(event);
    }

    @PacketHandler(type = CLIENTBOUND_SET_ENTITY_DATA)
    public void onPacket8(PacketEvent event){
        if(wrongState())return;
        Locale locale = handleLocale(event);
        Object packet = event.getPacket();
        List<?> values = PACKETS.clientboundSetEntityDataPacket$packedValues(packet);
        for (var value: values){
            if(SYNCHER.entityDataValue$serializer(value) == NetworkEnum.ENTITYDATA_ITEMSTACK ){
                Object value0 = SYNCHER.entityDataValue$value(value);
                if(ITEMSTACK.isItemStack(value0)){
                    processItemStackS2C(value0, locale);
                }
            }
        }
    }

    @PacketHandler(type = CLIENTBOUND_MERCHANT_OFFERS)
    public void onPacket9(PacketEvent event){
        if(wrongState())return;
        Locale locale = handleLocale(event);
        Object packet = event.getPacket();
        List values = PACKETS.clientboundMerchantOffersPacket$Offstes(packet);
        int len = values.size();
        for (int i=0; i<len;++i){
            Object value = values.get(i);
            Object result = TRADE.getResult(value);
            processItemStackS2C(result, locale);
            //may copy a new one here
            Object result0 = processMerchantScreen(TRADE.asBukkit(value), locale);
            if(result0 != null){
                values.set(i, result0);
            }
        }
    }


    ConcurrentHashMap<ClientInformation, Long> localeRefreshCooldown = new ConcurrentHashMap<>();
    @PacketHandler(type = CLIENT_UNREGISTER)
    public void onClientDisconnect(PacketEvent event){
        localeRefreshCooldown.remove(event.getClient());
    }

    @PacketHandler(type = SERVERBOUND_CLIENT_INFORMATION)
    public void onClientInformationQuickUpdate(PacketEvent event){
        ClientInformation client = event.getClient();
        Object player = client.getPlayer();
        if(player != null){
            String language = PACKETS.serverboundClientInformationPacket$language(event.getPacket());
            Locale locale = Translator.parseLocale(language);
            Locale locale1 = NMSLevel.PLAYER.locale(player);
            if(!Objects.equals(locale, locale1)){
                NMSLevel.PLAYER.locale(player, locale == null ? (this.languageRegistry == null? Locale.CHINESE: this.languageRegistry.getDefaultLocale()): locale);
                long current =System.currentTimeMillis();
                if(localeRefreshCooldown.getOrDefault(client, 0L) < current){
                    localeRefreshCooldown.put(client, current + 5000L);
                    ThreadUtils.executeSyncSched(()->{
                        Player player1 = NMSLevel.PLAYER.getBukkitEntity(player);
                        player1.updateInventory();
                        Collection<Entity> viewableEntities = PlayerUtils.getViewableEntity(player1);
                        for (var entity: viewableEntities){
                            Object nms = CraftBukkit.ENTITY.getHandle(entity);
                            NMSEntity.ENTITY.refreshEntityData(nms, player);
                        }
                    });
                }

            }

        }
    }

    //because its serialization does not use item , but shit
    private Object processMerchantScreen(MerchantRecipe recipe, Locale locale){
        boolean shouldTransferNew = false;
        List<ItemStack> itemStacks = recipe.getIngredients();
        for (var item: itemStacks){
            Object itemHandle = ItemUtils.unwrapHandle(item);
            if(processItemStackS2C(itemHandle, locale)){
                shouldTransferNew = true;
            }
        }
        if(shouldTransferNew){
            recipe.setIngredients(itemStacks);
            Object newMerchant = TRADE.toMinecraft(recipe);
            return newMerchant;
        }
        return null;
    }

//    @PacketHandler(type = ALL_S2C_PLAY)
//    public void onPacketOut(PacketEvent event){
//        Debug.logger("detect event",event.getPacket().getClass().getSimpleName());
//    }



    //should add recipe item transform and ingredient transform

    @Override
    public PacketTranslator reload() {
        deconstruct();
        return init(plugin);
    }

    @Override
    public boolean isAutoDisable() {
        return false;
    }

    @Override
    public void deconstruct() {
        if(PacketEventManager.getManager() != null){
            PacketEventManager.getManager().unregisterAll(this, plugin);
        }
    }
}
