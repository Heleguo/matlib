package me.matl114.matlib.utils.reflect.internel;

import me.matl114.matlib.utils.reflect.ByteCodeUtils;
import org.bukkit.Bukkit;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SimpleObfManagerImpl implements SimpleObfManager {
    final Class<?> obfClass ;
    final ClassMapperHelper classMapperHelper ;
    final Map<String, ?> mappingsByObfName;
    final Map<String, ?> mappingsByMojangName;
    static final String originCraftbukkitPackageName = "org.bukkit.craftbukkit";
    final String craftbukkitPackageName;
    //map from higher version to lower version
    //we all use high version mojang name here
    //
    final Map<String,String> mojangVersionedPath = Map.of(
        "net.minecraft.world.level.chunk.status.ChunkStatus",
        "net.minecraft.world.level.chunk.ChunkStatus"
    );
    final Map<String, String> mojangVersionedPathMapper;
    final Map<String, String> mojangVersionedPathMapperInverse;
    SimpleObfManagerImpl(){
        String[] path= Bukkit.getServer().getClass().getPackage().getName().split("\\.");
        if(path.length >= 4){
            craftbukkitPackageName = Bukkit.getServer().getClass().getPackage().getName();
        }else {
            //upper than 1_20_v4, paper no longer relocation craftbukkit package , at least through reflection
            craftbukkitPackageName = originCraftbukkitPackageName;
        }
        try{
            obfClass = Class.forName("io.papermc.paper.util.ObfHelper");
            classMapperHelper = new ClassMapperHelperImpl();
            Object obfIns = obfClass.getEnumConstants()[0];
            var f1 = obfClass.getDeclaredField("mappingsByObfName");
            f1.setAccessible(true);
            mappingsByObfName = (Map<String, ?>) f1.get(obfIns);
            var f2 = obfClass.getDeclaredField("mappingsByMojangName");
            f2.setAccessible(true);
            mappingsByMojangName = (Map<String, ?>) f2.get(obfIns);
            ClassMapperHelperImpl classMapperHelper1 = (ClassMapperHelperImpl) classMapperHelper;
        }catch (Throwable e){
            throw new RuntimeException(e);
        }
        Map<String,String> pathValidation = new HashMap<>();
        b(pathValidation);
        mojangVersionedPathMapper = Collections.unmodifiableMap(pathValidation);
        mojangVersionedPathMapperInverse = Collections.unmodifiableMap(
            pathValidation.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(entry-> entry.getValue(), entry->entry.getKey(), (oldvalue, newValue)->oldvalue))
        );
    }
    private void b(Map<String,String> map){
        for (var pathPair :mojangVersionedPath.entrySet()){
            Object reobfName = this.mappingsByMojangName.get(pathPair.getValue());
            String realPath = reobfName == null ? pathPair.getValue() : classMapperHelper.obfNameGetter(reobfName);
            try{
                Class.forName(realPath);
            }catch (Throwable e){
                continue;
            }
            //valid versioned path
            map.put(pathPair.getKey(), pathPair.getValue());
        }
    }

    public String demapCraftBukkitAndMojangVersionedPath(String currentName){
        if(currentName.startsWith(craftbukkitPackageName)){
            return currentName.replaceFirst(craftbukkitPackageName, originCraftbukkitPackageName);
        }
        return mojangVersionedPathMapperInverse.getOrDefault(currentName, currentName);
    }
    public String remapCraftBukkitAndMojangVersionedPath(String currentName){
        if(currentName.startsWith(originCraftbukkitPackageName)){
            return currentName.replaceFirst(originCraftbukkitPackageName, craftbukkitPackageName);
        }

        return mojangVersionedPathMapper.getOrDefault(currentName, currentName);
    }

    @Override
    public String deobfClassName(String currentName) {
        if (this.mappingsByObfName == null) {
            return demapCraftBukkitAndMojangVersionedPath(currentName);
        }

        final Object map = this.mappingsByObfName.get(currentName);
        if (map == null) {
            return demapCraftBukkitAndMojangVersionedPath(currentName);
        }

        return demapCraftBukkitAndMojangVersionedPath( classMapperHelper.mojangNameGetter(map));
    }

    @Override
    public String reobfClassName(String mojangName) {
        if (this.mappingsByMojangName == null) {
            return remapCraftBukkitAndMojangVersionedPath(mojangName);
        }

        final Object map = this.mappingsByMojangName.get(mojangName);
        if (map == null) {
            return remapCraftBukkitAndMojangVersionedPath(mojangName);
        }

        return remapCraftBukkitAndMojangVersionedPath( classMapperHelper.obfNameGetter(map));
    }

    @Override
    public String deobfMethodInClass(String reobfClassName, String methodDescriptor) {
        String methodName = ByteCodeUtils.parseMethodNameFromDescriptor(methodDescriptor);
        if (this.mappingsByMojangName == null) {
            return methodName;
        }
        //using versioned path of mojang name
        final Object map = this.mappingsByMojangName.get(remapCraftBukkitAndMojangVersionedPath( reobfClassName) );
        if(map == null){
            //no obf,
            return methodName;
        }
        final Map<String,String> methodMapping = classMapperHelper.methodsByObf(map);
        return methodMapping.getOrDefault(methodDescriptor, methodName);
    }

}
