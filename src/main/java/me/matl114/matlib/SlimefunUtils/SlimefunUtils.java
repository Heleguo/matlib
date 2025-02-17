package me.matl114.matlib.SlimefunUtils;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.chat.ChatInput;
import io.github.thebusybiscuit.slimefun4.utils.LoreBuilder;
import me.matl114.matlib.Utils.AddUtils;
import me.matl114.matlib.Utils.Algorithm.Pair;
import me.matl114.matlib.Utils.Debug;
import me.matl114.matlib.Utils.Flags;
import me.matl114.matlib.Utils.Inventory.ItemStacks.CleanItemStack;
import me.matl114.matlib.core.EnvironmentManager;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.matl114.matlib.Utils.AddUtils.getCopy;

public class SlimefunUtils {

    private static final double SF_TPS = 20.0 / (double) Slimefun.getTickerTask().getTickRate();
    public static String getItemId(ItemStack its){
        if(its==null)return null;
        SlimefunItem sfitem=SlimefunItem.getByItem(its);
        if(sfitem==null){
            return (its.getAmount()==1?"":String.valueOf(its.getAmount()))+its.getType().toString().toUpperCase(Locale.ROOT);
        }else {
            return (its.getAmount()==1?"":String.valueOf(its.getAmount()))+sfitem.getId();
        }
    }
    public static ItemStack resolveItem(Object a){
        if(a==null)return null;
        if(a instanceof ItemStack item){
            return  getCopy(item);
        }else if(a instanceof SlimefunItem){
            return getCopy(((SlimefunItem) a).getItem());
        }else if(a instanceof Material){
            return  new ItemStack((Material) a);
        }else if(a instanceof String){
            Pattern re=Pattern.compile("^([0-9]*)(.*)$");
            Matcher info= re.matcher((String)a);
            int cnt=-1;
            String id;
            if(info.find()){
                String amount=info.group(1);
                id=info.group(2);
                try{
                    cnt=Integer.parseInt(amount);
                }catch(NumberFormatException e){
                    cnt=-1;
                }
            }
            else{
                id=(String) a;
            }
            try{
                ItemStack b=getCopy(SlimefunItem.getById(id).getItem());
                if(cnt>0&&cnt!=b.getAmount()){
                    b.setAmount(cnt);
                }
                return b;
            }catch (Exception e){
                try{
                    ItemStack b=new ItemStack( EnvironmentManager.getManager().getVersioned().getMaterial(id));
                    if(cnt>0&&cnt!=b.getAmount()){
                        b=b.clone();
                        b.setAmount(cnt);
                    }
                    return b;
                }catch (Exception e2){
                    Debug.logger("WARNING: Object %s can not be solved ! Required Addon not installed ! Disabling relavent recipes...".formatted(a));
                    return AddUtils. RESOLVE_FAILED;
                }
            }
        } else {
            Debug.logger("WARNING: failed to solve Object "+a.toString());
            return AddUtils. RESOLVE_FAILED;
        }

    }
    public static Pair<ItemStack[], ItemStack[]> buildRecipes(Pair<Object[],Object[]> itemStacks){
        return buildRecipes(itemStacks.getA(),itemStacks.getB());
    }
    public static Pair<ItemStack[],ItemStack[]> buildRecipes (Object[] input,Object[] output){
        ItemStack[] a;
        ArrayList<ItemStack> a_=new ArrayList<>(){{

            Arrays.stream(input).forEach(
                    (obj)->{
                        ItemStack a__=resolveItem(obj);
                        this.add(a__);
                    }
            );
        }};
        a=a_.toArray(new ItemStack[a_.size()]);
        ItemStack[] b;
        ArrayList<ItemStack> b_=new ArrayList<>(){{

            Arrays.stream(output).forEach(
                    (obj)->{


                        ItemStack b__=resolveItem(obj);
                        this.add(b__);
                    }
            );

        }};
        b=b_.toArray(new ItemStack[b_.size()]);
        return new Pair<>(a,b);
    }
    public static MachineRecipe buildMachineRecipes(int time,Pair<Object[],Object[]> itemStacks){
        Pair<ItemStack[],ItemStack[]> b=buildRecipes(itemStacks);
        return new MachineRecipe(time,b.getA(),b.getB());
    }
    public static <T extends Object> List<Pair<Pair<ItemStack[],ItemStack[]>,Integer>> buildRecipeMap(List<Pair<T,Integer>> rawDataMap){
        if(rawDataMap==null)return new ArrayList<>();
        List<Pair<Pair<ItemStack[],ItemStack[]>,Integer>> map = new ArrayList<>();
        rawDataMap.forEach((p)->{
            var k=p.getA();
            var v=p.getB();
            if(k instanceof Object[]){

                map.add(new Pair<>(buildRecipes(
                        Arrays.copyOfRange((Object[])k,0,2),Arrays.copyOfRange((Object[])k,2,4)),v));
            }
            else if (k instanceof Pair){


                Object[] input=(Object[])((Pair)k).getA();
                if(input==null){
                    input=new Object[]{};
                }
                Object[] output=(Object[])((Pair)k).getB();
                if(output==null){
                    output=new Object[]{};
                }
                map.add(new Pair<>(buildRecipes(input,output),v));
            }
        });
        return map;
    }
    private static final DecimalFormat FORMAT = new DecimalFormat("###,###,###,###,###,###.#");
    public static String formatEnergy(int energy) {
        return FORMAT.format((double)energy * SF_TPS);
    }
    public static String energyPerSecond(int energy) {
        return "&8⇨ &e⚡ &7" + formatEnergy(energy) + " J/s";
    }
    public static String speedDisplay(int multiply){
        return "&8⇨ &e⚡ &7速度: &b"+ multiply + "x";
    }
    public static String energyPerTick(int energy){
        return "&8⇨ &e⚡ &7" + FORMAT.format((double)energy) + " J/t";
    }
    public static String energyPerCraft(int energy){
        return "&8⇨ &e⚡ &7" + FORMAT.format((double)energy ) + " J 每次合成";
    }
    public static  ItemStack workBenchInfoAdd(ItemStack item,int energyBuffer,int energyConsumption){
        return AddUtils.addLore(item, LoreBuilder.powerBuffer(energyBuffer), energyPerCraft(energyConsumption));
    }
    public static  String tickPerGen(int time){
        return "&8⇨ &7速度: &b每 " + Integer.toString(time) + " 粘液刻生成一次";
    }

    public static ItemStack capacitorInfoAdd(ItemStack item,int energyBuffer){
        return AddUtils.addLore(item, LoreBuilder.powerBuffer(energyBuffer));
    }
    public static  ItemStack machineInfoAdd(ItemStack item,int energyBuffer,int energyConsumption){
        return machineInfoAdd(item,energyBuffer,energyConsumption, Flags.USE_SEC_EXP);
    }
    public static ItemStack machineInfoAdd(ItemStack item, int energyBuffer, int energyConsumption, Flags type){
        if(type== Flags.USE_SEC_EXP) {
            return AddUtils.addLore(item, LoreBuilder.powerBuffer(energyBuffer), energyPerSecond(energyConsumption));
        }
        else if(type== Flags.USE_TICK_EXP) {
            return  AddUtils.addLore( item, LoreBuilder.powerBuffer(energyBuffer), energyPerTick(energyConsumption));
        }
        else return null;
    }
    public static ItemStack smgInfoAdd(ItemStack item, int time){
        return  AddUtils.addLore( item, tickPerGen(time));
    }
    public static ItemStack advancedMachineShow(ItemStack stack,int limit){
        return AddUtils.addLore(stack,"&7机器合成进程数: %s".formatted(limit));
    }
    public static void asyncWaitPlayerInput(Player player, Consumer<String> consumer){
        ChatInput.waitForPlayer(
                Slimefun.instance(),
                player,
                msg ->{
                    consumer.accept(msg);
                } );
    }
    public static ItemStack getGeneratorDisplay(boolean working,String type,int charge,int buffer){
        if(working){
            return new CleanItemStack(Material.GREEN_STAINED_GLASS_PANE,"&a发电中",
                    "&7类型:&6 %s".formatted(type),"&7&7电量: &6%s/%sJ".formatted(FORMAT.format((double)charge),FORMAT.format((double)buffer)));
        }else {
            return new CleanItemStack(Material.RED_STAINED_GLASS_PANE,"&a未发电",
                    "&7类型:&6 %s".formatted(type),"&7&7电量: &6%s/%sJ".formatted(FORMAT.format((double)charge),FORMAT.format((double)buffer)));
        }
    }

}
