package me.matl114.matlib.utils.reflect;

import com.google.common.base.Preconditions;
import me.matl114.matlib.utils.Debug;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import javax.annotation.Nullable;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Type.getInternalName;

public class ASMUtils {
    public static void insertDebug(MethodVisitor mv, String log){
        mv.visitLdcInsn(log);
        mv.visitMethodInsn(
            INVOKESTATIC,
            getInternalName(Debug.class),
            "logger",
            "(Ljava/lang/String;)V",
            false
        );
    }
    public static void generateEmptyInit(ClassWriter cw,@Nullable String parentCls){
        var methodVisitor = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        {
            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
            methodVisitor.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                parentCls == null?"java/lang/Object":parentCls.replace(".","/"),
                "<init>",
                "()V",
                false);
            methodVisitor.visitInsn(Opcodes.RETURN);
            methodVisitor.visitMaxs(0,0);
            methodVisitor.visitEnd();
        }
    }
    public static MethodVisitor createOverrideMethodImpl(ClassWriter cw, Method method){
        Class<?>[] exceptions = method.getExceptionTypes();
        String[] names = (exceptions == null || exceptions.length == 0) ? null : Arrays.stream(exceptions) .map(Type::getInternalName).toArray(String[]::new);
        int mod = method.getModifiers();
        //remove abstract flag
        mod = mod & (~ Opcodes.ACC_ABSTRACT);
        return cw.visitMethod(mod, method.getName(), Type.getMethodDescriptor(method), null, names);
    }
    public static void createSuitableLoad(MethodVisitor mv, String loadType, int loadIndex){
        switch (loadType){
            case "int":
            case "boolean":
            case "short":
            case "byte":
            case "char":
                mv.visitVarInsn(Opcodes.ILOAD, loadIndex);
                break;
            case "long":
                mv.visitVarInsn(Opcodes.LLOAD, loadIndex);
                break;
            case "double":
                mv.visitVarInsn(Opcodes.DLOAD, loadIndex);
                break;
            case "float":
                mv.visitVarInsn(Opcodes.FLOAD, loadIndex);
                break;
            case "void":
                throw new IllegalArgumentException("Can not load a void variable");
            default:
                mv.visitVarInsn(Opcodes.ALOAD, loadIndex);
                break;
        }
    }

    public static void createMethodLookupField(ClassWriter cw){
        var fv = cw.visitField(
            ACC_PUBLIC|ACC_FINAL|ACC_STATIC,
            "lookup",
            "Ljava/lang/invoke/MethodHandles$Lookup;",
            null,
            null
        );
        fv.visitEnd();
    }

    public static void createMethodLookupInit(MethodVisitor mv, String implPath){
        mv.visitLdcInsn(Type.getType(ByteCodeUtils.toJvmType(implPath)));
        mv.visitMethodInsn(
            INVOKESTATIC,
            getInternalName(MethodHandles.class),
            "lookup",
            "()Ljava/lang/invoke/MethodHandles$Lookup;",
            false
        );
        mv.visitFieldInsn(
            PUTSTATIC,
            implPath,
            "lookup",
            "Ljava/lang/invoke/MethodHandles$Lookup;"
        );
    }

    public static void createSuitableReturn(MethodVisitor mv, String returnType){
        switch (returnType){
            case "int":
            case "boolean":
            case "short":
            case "byte":
            case "char":
                mv.visitInsn(Opcodes.IRETURN);
                break;
            case "long":
                mv.visitInsn(Opcodes.LRETURN);
                break;
            case "double":
                mv.visitInsn(Opcodes.DRETURN);
                break;
            case "float":
                mv.visitInsn(Opcodes.FRETURN);
                break;
            case "void":
                mv.visitInsn(Opcodes.RETURN);
                break;
            default:
                mv.visitInsn(Opcodes.ARETURN);
                break;
        }
    }
    public static void createSuitableDefaultValueReturn(MethodVisitor mv, String returnType){
        switch (returnType){
            case "int":
            case "boolean":
            case "short":
            case "byte":
            case "char":
                mv.visitInsn(Opcodes.ICONST_0);
                mv.visitInsn(Opcodes.IRETURN);
                break;
            case "long":
                mv.visitInsn(Opcodes.LCONST_0);
                mv.visitInsn(Opcodes.LRETURN);
                break;
            case "double":
                mv.visitInsn(Opcodes.DCONST_0);
                mv.visitInsn(Opcodes.DRETURN);
                break;
            case "float":
                mv.visitInsn(Opcodes.FCONST_0);
                mv.visitInsn(Opcodes.FRETURN);
                break;
            case "void":
                mv.visitInsn(Opcodes.RETURN);
                break;
            default:
                mv.visitInsn(Opcodes.ACONST_NULL);
                mv.visitInsn(Opcodes.ARETURN);
                break;
        }
    }
    public static void castType(MethodVisitor mv, String originType, String targetType){
        if(!ReflectUtils.isPrimitiveType(originType) && ReflectUtils.isPrimitiveType(targetType)){
            //向基类转
            if(!ReflectUtils.isBoxedPrimitive(originType)){
                //原 不是基类包装类
                String boxedType = ReflectUtils.getBoxedClass(targetType);
                castType(mv, originType, boxedType);
                castToPrimitiveType(mv, targetType);
            }else {
                //考虑 基类是否可以转
                String unboxed = ReflectUtils.getUnboxedClass(originType);
                castToPrimitiveType(mv, unboxed);
                castInPrimitive(mv, unboxed, targetType);
            }
        } else if(ReflectUtils.isPrimitiveType(originType) && !ReflectUtils.isPrimitiveType(targetType)){

            if(ReflectUtils.isBoxedPrimitive(targetType)){

                String unbox = ReflectUtils.getUnboxedClass(targetType);
                castInPrimitive(mv, originType, unbox);
                castFromPrimitiveType(mv, unbox);
            }else {

                String boxedClass = ReflectUtils.getBoxedClass(originType);
                castFromPrimitiveType(mv, originType);
                if(!Objects.equals(targetType, boxedClass)){
                    //not boxClass belike cast to Object or sth
                    castRefInternel(mv, targetType);
                }
            }
//            Preconditions.checkArgument(Objects.equals(boxedClass, targetType), "Primitive type "+ originType+" can only cast to "+ boxedClass);

        } else if(ReflectUtils.isPrimitiveType(originType )&& ReflectUtils.isPrimitiveType(targetType)){

            castInPrimitive(mv, originType, targetType);
        }
        else {

            castRefInternel(mv, targetType);
        }
    }

    private static void castRefInternel(MethodVisitor mv, String to){
        if(!"java/lang/Object".equals(to)){
            mv.visitTypeInsn(Opcodes.CHECKCAST, to);
        }
    }
    public static void castToPrimitiveType(MethodVisitor mv, String primitive){
        switch (primitive) {
            case "int":
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
                break;
            case "boolean":
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
                break;
            case "long":
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
                break;
            case "double":
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
                break;
            case "float":
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false);
                break;
            case "short":
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S", false);
                break;
            case "byte":
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B", false);
                break;
            case "char":
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C", false);
                break;
            case "void":
                //wtf?
            default:
                throw new IllegalArgumentException("Unsupported primitive type: " + primitive);
        }
    }
    public static void castFromPrimitiveType(MethodVisitor mv, String primitive){
        String boxed  = ReflectUtils.getBoxedClass(primitive);
        Preconditions.checkArgument(!Objects.equals(primitive, "void"),"can not cast "+primitive +" to "+boxed);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, boxed, "valueOf", "("+  ByteCodeUtils.toJvmType(primitive) +")"+ ByteCodeUtils.toJvmType(boxed), false );
    }
    public static void castInPrimitive(MethodVisitor mv, String primFrom, String primTo){
        if(Objects.equals(primFrom, primTo)){
            return;
        }
        switch (primFrom + "->" + primTo) {
            // int -> 其他类型
            case "int->long":
                mv.visitInsn(Opcodes.I2L); // int 转 long
                break;
            case "int->float":
                mv.visitInsn(Opcodes.I2F); // int 转 float
                break;
            case "int->double":
                mv.visitInsn(Opcodes.I2D); // int 转 double
                break;
            case "int->short":
                mv.visitInsn(Opcodes.I2S); // int 转 short（截断）
                break;
            case "int->byte":
                mv.visitInsn(Opcodes.I2B); // int 转 byte（截断）
                break;
            case "int->char":
                mv.visitInsn(Opcodes.I2C); // int 转 char（无符号截断）
                break;

            // long -> 其他类型
            case "long->int":
                mv.visitInsn(Opcodes.L2I); // long 转 int（截断）
                break;
            case "long->float":
                mv.visitInsn(Opcodes.L2F); // long 转 float
                break;
            case "long->double":
                mv.visitInsn(Opcodes.L2D); // long 转 double
                break;

            // float -> 其他类型
            case "float->int":
                mv.visitInsn(Opcodes.F2I); // float 转 int（截断）
                break;
            case "float->long":
                mv.visitInsn(Opcodes.F2L); // float 转 long（截断）
                break;
            case "float->double":
                mv.visitInsn(Opcodes.F2D); // float 转 double
                break;

            // double -> 其他类型
            case "double->int":
                mv.visitInsn(Opcodes.D2I); // double 转 int（截断）
                break;
            case "double->long":
                mv.visitInsn(Opcodes.D2L); // double 转 long（截断）
                break;
            case "double->float":
                mv.visitInsn(Opcodes.D2F); // double 转 float
                break;

            // 其他类型（short/byte/char 通常先转 int）
            case "short->int":
            case "byte->int":
            case "char->int":
                // short/byte/char 在字节码中实际以 int 形式存储，无需转换
                break;
            default:
                throw new IllegalArgumentException("Unsupported cast: " + primFrom + " to " + primTo);
        }
    }
}
