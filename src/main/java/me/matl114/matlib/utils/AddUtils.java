package me.matl114.matlib.utils;

import com.google.common.base.Preconditions;
import me.matl114.matlib.algorithms.algorithm.Utils;
import me.matl114.matlib.utils.inventory.itemStacks.CleanItemStack;
import me.matl114.matlib.core.EnvironmentManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;


public class AddUtils {
//    public static void init(String id,String addonName,Plugin pl){
//        Debug.logger("Initializing Utils...");
//      //  ADDON_INSTANCE=pl;
//
//    }
//    public static String ADDON_NAME ;
//    public static String ADDON_ID;
//    public static Plugin ADDON_INSTANCE;
    public static final boolean USE_IDDECORATOR=true;
    private static final DecimalFormat FORMAT = new DecimalFormat("###,###,###,###,###,###.#");
    private static final Random random=new Random();
    private static final Enchantment GLOW_EFFECT=EnvironmentManager.getManager().getVersioned().getEnchantment("infinity");
    public static final ItemStack RESOLVE_FAILED=AddUtils.addGlow( new CleanItemStack(Material.BARRIER,"&c解析物品失败"));
    public static final String PLACEHOLDER = "†";
    public static String formatDouble(double s){
        return FORMAT.format(s);
    }

    public static final String C="§";
//    public static NamespacedKey getNameKey(String str) {
//        return new NamespacedKey(ADDON_INSTANCE,str);
//    }
    public static String desc(String str) {
        return "§7" + str;
    }
    public static final String[] COLOR_MAP=new String[]{"§0","§1","§2","§3","§4","§5","§6","§7","§8","§9","§A","§B","§C","§D","§E","§F"};
    public static String resolveRGB(int rgb){
        if(rgb>16777216){
            rgb=16777216;
        }
        else if (rgb<0){
            rgb=0;
        }
        StringBuilder builder=new StringBuilder("§x");
        for(int i=0;i<6;i++){
            int r=rgb%16;
            rgb=rgb/16;
            builder.append(COLOR_MAP[r]);
        }
        return builder.toString();
    }
    public static String codeColor(int c) {
        if (c < 10 && c >= 0) {
            return String.valueOf(c);
        }
        return switch (c) {
            case 10 -> "A";
            case 11 -> "B";
            case 12 -> "C";
            case 13 -> "D";
            case 14 -> "E";
            case 15 -> "F";
            default -> "0";
        };
    }
    public static String resolveColor(int c) {
        return C+codeColor(c);
    }

    public static String resolveRGB(String rgb) throws IllegalArgumentException {
        if(rgb.length()!=6){
            throw new IllegalArgumentException("Invalid RGB String");
        }

        StringBuilder builder=new StringBuilder("§x");
        for (int i=0;i<6;i++){
            builder.append("§").append(Character.toUpperCase( rgb.charAt(i)));
        }
        return builder.toString();
    }
    public static int decodeColor(char c){
        if(c>='0' && c<='9'){
            return c - '0';
        }
        switch (c) {
            case 'A' :
            case 'a' : return 10;
            case 'B' :
            case 'b' : return 11;
            case 'C' :
            case 'c' : return 12;
            case 'D' :
            case 'd' : return 13;
            case 'E' :
            case 'e' : return 14;
            case 'F' :
            case 'f' : return 15;
            default:
                return  0;
        }
    }
    public static int rgb2int(String rgb) throws IllegalArgumentException{
        if(rgb.length()!=6){
            throw new IllegalArgumentException("Invalid RGB String");
        }
        int value=0;
        for (int i=0;i<6;i++){
            value = value * 16 +decodeColor(rgb.charAt(i));
        }
        return value;
    }
    public static final int START_CODE=rgb2int("eb33eb");
    //15409899;
    public static final int END_CODE=rgb2int("970097");
    public static String color(String str){
        return resolveRGB(START_CODE)+str;
    }
    public static String colorful(String str) {
        int len=str.length()-1;
        if(len<=0){
            return resolveRGB(START_CODE)+str;
        }
        else{
            int start=START_CODE;
            int end=END_CODE;
            int[] rgbs=new int[9];
            for(int i=0;i<3;++i){
                rgbs[i]=start%256;
                rgbs[i+3]=end%256;
                rgbs[i+6]=rgbs[i+3]-rgbs[i];
                start=start/256;
                end=end/256;
            }
            String str2="";
            for(int i=0;i<=len;i++){
                str2=str2+resolveRGB(START_CODE+65536*((rgbs[8]*i)/len)+256*((rgbs[7]*i)/len)+((rgbs[6]*i)/len))+str.substring(i,i+1);
            }
            return str2;
        }
    }
    public static String colorString(@Nonnull String string0, @Nonnull List<Color> colorList) {
        StringBuilder stringBuilder = new StringBuilder();
        if (string0.length() == 0) {
            string0 += " ";
        }
        if (string0.length() == 1) {
            string0 += " ";
        }
        String string = string0.replaceAll("%s", PLACEHOLDER);
        for (int i = 0, length = string.length() - 1; i <= length; i++) {
            double p =( ((double) i) / length ) * (colorList.size() - 1);
            Color color1 = colorList.get((int) Math.floor(p));
            Color color2 = colorList.get((int) Math.ceil(p));
            int blue = (int) (color1.getBlue() * (1 - p + Math.floor(p)) + color2.getBlue() * (p - Math.floor(p)));
            int green = (int) (color1.getGreen() * (1 - p + Math.floor(p)) + color2.getGreen() * (p - Math.floor(p)));
            int red = (int) (color1.getRed() * (1 - p + Math.floor(p)) + color2.getRed() * (p - Math.floor(p)));
            stringBuilder.append("§x")
                    .append("§").append(codeColor(red / 16))
                    .append("§").append(codeColor(red % 16))
                    .append("§").append(codeColor(green / 16))
                    .append("§").append(codeColor(green % 16))
                    .append("§").append(codeColor(blue / 16))
                    .append("§").append(codeColor(blue % 16));
            stringBuilder.append(string.charAt(i));
        }
        String re = stringBuilder.toString();
        re = re.replaceAll(PLACEHOLDER, "%s");
        return re;
    }
    public static String colorRandomString(@Nonnull String string) {
        List<Color> colorList = new ArrayList<>();
        double r = 0;
        Random random = new Random(string.hashCode() / 2 +1919810);
        do {
            int red = (int) ((random.nextDouble() * 8 + 8) * 15 + random.nextDouble() * 12 + 4);
            int green = (int) ((random.nextDouble() * 8 + 8) * 15 + random.nextDouble() * 12 + 4);
            int blue = (int) ((random.nextDouble() * 8 + 8) * 15 + random.nextDouble() * 12 + 4);
            colorList.add(Color.fromRGB(red, green, blue));
            r++;
        }while (1 / r >= random.nextDouble() && r * r <= string.length()+1);

        return colorString(string, colorList);
    }
    public static String colorPseudorandomString(@Nonnull String string) {
        List<Color> colorList = new ArrayList<>();
        double r = 0;
        Random random = new Random(11451419);
        do {
            int red = (int) ((random.nextDouble() * 8 + 8) * 15 + random.nextDouble() * 12 + 4);
            int green = (int) ((random.nextDouble() * 8 + 8) * 15 + random.nextDouble() * 12 + 4);
            int blue = (int) ((random.nextDouble() * 8 + 8) * 15 + random.nextDouble() * 12 + 4);
            colorList.add(Color.fromRGB(red, green, blue));
            r++;
        }while (1 / r >= random.nextDouble() && r * r <= string.length()+1);

        return colorString(string, colorList);
    }
    private static final String DISPLAY_PATTERN="[%s,%.0f,%.0f,%.0f]";
    public static String locationToString(Location loc){
        if(loc==null){
            return "null";
        }else{
            return new StringBuilder().append(loc.getWorld().getName()).append(',')
                .append(loc.getX()).append(',').append(loc.getY()).append(',').append(loc.getZ()).toString();
        }
    }
    public static String blockLocationToString(Location loc){
        if(loc==null){
            return "null";
        }else{
            return new StringBuilder().append(loc.getWorld().getName()).append(',')
                    .append(loc.getBlockX()).append(',').append(loc.getBlockY()).append(',').append(loc.getBlockZ()).toString();
        }
    }
    public static Location locationFromString(String loc){
        try{
            if("null".equals(loc)){
                return null;
            }
            String[] list=loc.split(",");
            if(list.length!=4)return null;
            String world =list[0];
            double x = Double.parseDouble(list[1]);
            double y = Double.parseDouble(list[2]);
            double z = Double.parseDouble(list[3]);
            return new Location(Bukkit.getWorld(world), x, y, z);
        }catch (Throwable e){
        }
        return null;
    }
    public static Location blockLocationFromString(String loc){
        try{
            if("null".equals(loc)){
                return null;
            }
            String[] list=loc.split(",");
            if(list.length!=4)return null;
            String world =list[0];
            int x = Integer.parseInt(list[1]);
            int y = Integer.parseInt(list[2]);
            int z = Integer.parseInt(list[3]);
            return new Location(Bukkit.getWorld(world), x, y, z);
        }catch (Throwable e){
        }
        return null;
    }
    public static String locationToDisplayString(Location loc) {
        return loc!=null? DISPLAY_PATTERN.formatted(loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ()):"null";
    }

    private static final List<Function<ItemStack,ItemStack>> copyFunctions = new ArrayList<>();
    public static void registerItemCopy(Function<ItemStack,ItemStack> function){
        copyFunctions.add(function);
    }

    public static ItemStack getCopy(ItemStack stack){
        ItemStack result;
//        if(stack instanceof AbstractItemStack abs){
//            return abs.copy();
//        }else
        ItemStack copied = Utils.computeTilPresent(stack, (Function<ItemStack, ItemStack>[]) copyFunctions.toArray(new Function[copyFunctions.size()]));
        if(copied!=null){
            return copied;
        }
        return getCleaned(stack);



    }
    //make it return org.bukkit.inventory.ItemStack
    public static ItemStack getCleaned(ItemStack stack){
        return stack==null?new ItemStack(Material.AIR): CleanItemStack.ofBukkitClean(stack);
    }


    public static boolean copyItem(ItemStack from,ItemStack to){
        if(from==null||to==null)return false;
        to.setAmount(from.getAmount());
        to.setType(from.getType());
        to.setData(from.getData());
        return to.setItemMeta(from.getItemMeta());
    }

    public static ItemStack addLore(ItemStack item,String... lores){

        ItemStack item2=item.clone();

        ItemMeta meta=item2.getItemMeta();
        List<String> finallist=meta.hasLore() ? meta.getLore() : new ArrayList<>();
        for (String l:lores){
            finallist.add(resolveColor(l));
        }
        meta.setLore(finallist);
        item2.setItemMeta(meta);
        return item2;
    }
    public static ItemStack renameItem(ItemStack item,String name){
        ItemStack item2=item.clone();

        ItemMeta meta=item2.getItemMeta();
        meta.setDisplayName(resolveColor(name));
        item2.setItemMeta(meta);
        return item2;
    }
    public static String resolveColor(String s){
        return translateAlternateColorCodes('&', s);
    }
    public static String translateAlternateColorCodes(char altColorChar,  String textToTranslate) {
        Preconditions.checkArgument(textToTranslate != null, "Cannot translate null text");
        char[] b = textToTranslate.toCharArray();

        for(int i = 0; i < b.length - 1; ++i) {
            if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx".indexOf(b[i + 1]) > -1) {
                b[i] = 167;
            }
        }

        return new String(b);
    }

    public static String getPercentFormat(double b){
        DecimalFormat df = new DecimalFormat("#.##");
        NumberFormat nf = NumberFormat.getPercentInstance();
        return nf.format(Double.parseDouble(df.format(b)));
    }

    /**
     * return int values in [0,length)
     * @param length
     * @return
     */
    public static int random(int length){
        return random.nextInt(length);
    }
    //generate rand in (0,1)
    public static double standardRandom(){
        return random.nextDouble();
    }
    //we supposed that u have checked these shits


    public static void forceGive(Player p, ItemStack toGive, int amount) {
        ItemStack incoming;
        int maxSize=toGive.getMaxStackSize();
        while(amount>0) {
            incoming = new ItemStack(toGive);
            int amount2=Math.min(maxSize, amount);
            incoming.setAmount(amount2);
            amount-=amount2;
            Collection<ItemStack> leftover = p.getInventory().addItem(incoming).values();
            for (ItemStack itemStack : leftover) {
                p.getWorld().dropItemNaturally(p.getLocation(), itemStack);
            }
        }
    }

    /**
     * add glowing effect to itemstack
     * no clone in this method
     * @param stack
     */

    public static ItemStack addGlow(ItemStack stack){
        //stack.addEnchantment(Enchantment.ARROW_INFINITE, 1);
        ItemMeta meta=stack.getItemMeta();
        meta.addEnchant(GLOW_EFFECT, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        stack.setItemMeta(meta);
        return stack;
    }
    public static ItemStack hideAllFlags(ItemStack stack){
        ItemMeta meta=stack.getItemMeta();
        for (ItemFlag flag:ItemFlag.values()){
            meta.addItemFlags(flag);
        }
        stack.setItemMeta(meta);
        return stack;
    }
    public static ItemStack showAllFlags(ItemStack stack){
        ItemMeta meta=stack.getItemMeta();
        for (ItemFlag flag:ItemFlag.values()){
            meta.removeItemFlags(flag);
        }
        stack.setItemMeta(meta);
        return stack;
    }
    public static ItemStack setUnbreakable(ItemStack stack,boolean unbreakable){
        ItemMeta meta=stack.getItemMeta();
        meta.setUnbreakable(unbreakable);
        stack.setItemMeta(meta);
        return stack;
    }
    /**
     * get a info display item to present in SF machineRecipe display
     * @param title
     * @param name
     * @return
     */
    public static ItemStack getInfoShow(String title,String... name){
        return new CleanItemStack(Material.BOOK,title,name);
    }

    /**
     * set the specific lore line in stack ,will not clone
     * @param stack
     * @param index
     * @param str
     * @return
     */
    public static ItemStack setLore(ItemStack stack,int index,String str){
        ItemMeta meta=stack.getItemMeta();
        List<String> lore=meta.getLore();
        while(index>=lore.size()){
            lore.add("");
        }
        lore.set(index,resolveColor(str));
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }
    /**
     * set the total lore line in stack ,will not clone
     * @param stack
     * @param str
     * @return
     */
    public static ItemStack setLore(ItemStack stack,String... str){
        ItemMeta meta=stack.getItemMeta();
        List<String> lore=new ArrayList<>();
        int len=str.length;
        for (int i=0;i<len;++i) {
            lore.add(resolveColor(str[i]));
        }
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }

    public static void sendMessage(CommandSender p, String msg){
        p.sendMessage(resolveColor(msg));
    }
    public static void sendTitle(Player p,String title,String subtitle){
        p.sendTitle(resolveColor(title),resolveColor(subtitle),-1,-1,-1);
    }
    public static ItemStack[] formatInfoRecipe(ItemStack stack,String source){
        return new ItemStack[]{
                null,stack,null,
                null,getInfoShow("&f获取方式","&7在 %s 中获取".formatted(source)),null,
                null,null,null
        };
    }
    //    public static MachineRecipe formatInfoMachineRecipe(ItemStack[] stack,int tick,String... description){
//        return MachineRecipeUtils.From(tick,new ItemStack[]{
//                getInfoShow("&f获取方式",description)
//        },stack);
//    }
    public static @Nonnull Optional<Material> getPlanks(@Nonnull Material log) {
        String materialName = log.name().replace("STRIPPED_", "");
        int endIndex = materialName.lastIndexOf('_');

        if (endIndex > 0) {
            materialName = materialName.substring(0, endIndex) + "_PLANKS";
            return Optional.ofNullable(Material.getMaterial(materialName));
        } else {
            // Fixed #3651 - Do not panic because of one weird wood type.
            return Optional.empty();
        }
    }
    public static void displayCopyString(Player player,String display,String hover,String copy){
        final TextComponent link = new TextComponent(display);
        link.setColor(ChatColor.YELLOW);
        link.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hover)));
        link.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD,copy));
        player.spigot().sendMessage(link);
    }


    public static String getUUID(){
        return UUID.randomUUID().toString();
    }
    public static void broadCast(String string){
        Bukkit.getServer().broadcastMessage(resolveColor(string));
    }
    public static ItemStack setCount(ItemStack stack,int amount){
        return new CleanItemStack(stack,amount);
    }
    public static String concat(String... strs){
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<strs.length;++i){
            sb.append(strs[i]);
        }
        return sb.toString();
    }
    public static void damageGeneric(Damageable e, double f){
        e.setHealth(Math.min( Math.max( e.getHealth()-f,0.0),e.getMaxHealth()));
    }
    public static ItemMeta setName(String name,ItemMeta meta){
        meta.setDisplayName(AddUtils.resolveColor(name));
        return meta;
    }
    public static String getDateString(){
        return new SimpleDateFormat("yyyyMMdd").format(new Date());
    }
}
