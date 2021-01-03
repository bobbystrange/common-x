package org.dreamcat.common.x.asm;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * Create by tuke on 2018/10/14
 */
@Slf4j
public final class MethodParameterUtil {

    private MethodParameterUtil(){
    }

    /**
     * require <strong>javac -g:vars<strong/>
     * to Generates Local variable debugging information
     *
     * @param method method
     * @return {@code null} if IO Error
     */
    public static String[] getName(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        int len = parameterTypes.length;
        if (len == 0) {
            return new String[0];
        }

        Class<?> clazz = method.getDeclaringClass();
        String methodName = method.getName();
        Type[] types = new Type[len];
        for (int i = 0; i < len; i++) {
            types[i] = Type.getType(parameterTypes[i]);
        }
        String[] parameterNames = new String[len];

        String className = clazz.getName();
        int lastDotIndex = className.lastIndexOf(".");
        className = className.substring(lastDotIndex + 1) + ".class";

        try (InputStream inputStream = clazz.getResourceAsStream(className)) {
            ClassReader classReader = new ClassReader(inputStream);
            classReader.accept(new ClassVisitor(Opcodes.ASM5) {
                @Override
                public MethodVisitor visitMethod(
                        int access, String name,
                        String desc, String signature, String[] exceptions) {
                    Type[] argumentTypes = Type.getArgumentTypes(desc);
                    // ignore if visited method is not the specified method

                    if (!methodName.equals(name)
                            || !Arrays.equals(argumentTypes, types)) {
                        return super.visitMethod(access, name, desc, signature,
                                exceptions);
                    }
                    return new MethodVisitor(Opcodes.ASM5) {
                        @Override
                        public void visitLocalVariable(
                                String name, String desc, String signature,
                                Label start, Label end, int index) {
                            // first param of no-static method is this
                            if (Modifier.isStatic(method.getModifiers())) {
                                parameterNames[index] = name;
                            } else if (index > 0) {
                                parameterNames[index - 1] = name;
                            }
                        }
                    };
                }
            }, 0);
            return parameterNames;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * require <strong>javac -g:vars<strong/>
     * to Generates Local variable debugging information
     *
     * @param method method
     * @return {@code null} if IO Error, or lack of Local variable
     */
    public static String[] getNameByJavassist(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        int len = parameterTypes.length;
        if (len == 0) {
            return new String[0];
        }

        Class<?> clazz = method.getDeclaringClass();
        String methodName = method.getName();

        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass;
        try {
            ctClass = pool.get(clazz.getName());
        } catch (NotFoundException e) {
            log.error(e.getMessage(), e);
            return null;
        }

        CtClass[] params = new CtClass[len];
        for (int i = 0; i < len; i++) {
            try {
                params[i] = pool.get(parameterTypes[i].getName());
            } catch (NotFoundException e) {
                log.error(e.getMessage(), e);
                return null;
            }
        }

        CtMethod ctMethod;
        try {
            ctMethod = ctClass.getDeclaredMethod(methodName, params);
        } catch (NotFoundException e) {
            log.error(e.getMessage(), e);
            return null;
        }

        MethodInfo methodInfo = ctMethod.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        LocalVariableAttribute attribute = (LocalVariableAttribute) codeAttribute
                .getAttribute(LocalVariableAttribute.tag);
        if (attribute == null) return null;

        String[] parameterNames = new String[len];
        int pos = Modifier.isStatic(ctMethod.getModifiers()) ? 0 : 1;
        for (int i = 0; i < len; i++) {
            parameterNames[i] = attribute.variableName(i + pos);
        }
        return parameterNames;
    }

}
