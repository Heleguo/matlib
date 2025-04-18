package me.matl114.matlib.utils.reflect.proxy.methodMap;

import me.matl114.matlib.common.lang.annotations.Note;

import java.lang.reflect.Method;

public record MethodIndex(@Note("target class method") Method target, @Note("interface method signature") MethodSignature signature,@Note("internal index") int index) {
}
