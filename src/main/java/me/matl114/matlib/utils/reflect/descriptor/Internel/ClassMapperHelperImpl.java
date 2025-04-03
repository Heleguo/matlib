package me.matl114.matlib.utils.reflect.descriptor.Internel;

import com.esotericsoftware.reflectasm.MethodAccess;
import lombok.Getter;
import me.matl114.matlib.common.lang.annotations.Internal;
import me.matl114.matlib.common.lang.annotations.Note;

import java.util.Map;

@Note("this is a HAND-MADE impl for ObfHelper because we need to get the ObfSource before anything")
@Internal
class ClassMapperHelperImpl implements ClassMapperHelper {
    private final Class<?> clazz0;
    @Getter
    private final MethodAccess classMappingAccess;
    private final int method1;
    private final int method2;
    private final int method3;
    //private final Constructor<?> init0;
    public ClassMapperHelperImpl() throws Throwable{
        clazz0 = Class.forName("io.papermc.paper.util.ObfHelper$ClassMapping");
        classMappingAccess = me.matl114.matlib.utils.reflect.MethodAccess.getOrCreateAccess(clazz0);// MethodAccess.get(clazz0);
        method1 = classMappingAccess.getIndex("obfName",0);
        method2 = classMappingAccess.getIndex("mojangName",0);
        method3 = classMappingAccess.getIndex("methodsByObf",0);
        //init0 = clazz0.getConstructor(String.class, String.class, Map.class);


    }

//    @Override
//    public Object newInstance0(String obfName, String mojangName, Map mapping) {
//        try{
//            return this.init0.newInstance(obfName, mojangName, mapping);
//        }catch (Throwable e){
//            throw new DescriptorException(e);
//        }
//    }

    @Override
    public String obfNameGetter(Object obj) {
        return (String) classMappingAccess.invoke(obj, method1);
    }

    @Override
    public String mojangNameGetter(Object obj) {
        return (String) classMappingAccess.invoke(obj, method2);
    }

    @Override
    public Map methodsByObf(Object self) {
        return (Map) classMappingAccess.invoke(self, method3);
    }

    @Override
    public Class<?> getTargetClass() {
        return clazz0;
    }
}
