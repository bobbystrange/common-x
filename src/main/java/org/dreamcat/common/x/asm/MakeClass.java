package org.dreamcat.common.x.asm;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.ClassFile;
import org.dreamcat.common.util.ReflectUtil;
import org.dreamcat.common.util.StringUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Create by tuke on 2020/5/28
 */
public class MakeClass {

    private final ClassPool pool;
    private final CtClass cc;

    public MakeClass(String className) {
        this.pool = ClassPool.getDefault();
        this.cc = pool.makeClass(className);
    }

    public static MakeClass make(String className) {
        return new MakeClass(className);
    }

    public Class<?> toClass() throws CannotCompileException {
        return toCtClass().toClass();
    }

    public CtClass toCtClass() {
        return cc;
    }

    // ---- ---- ---- ----    ---- ---- ---- ----    ---- ---- ---- ----

    public MakeClass superClass(Class<?> superClass) throws NotFoundException, CannotCompileException {
        cc.setSuperclass(pool.get(superClass.getCanonicalName()));
        return this;
    }

    public MakeClass interfaces(Collection<Class<?>> interfaces) throws NotFoundException {
        for (Class<?> face : interfaces) {
            cc.addInterface(pool.get(face.getCanonicalName()));
        }
        return this;
    }

    public MakeClass interfaces(CtClass... interfaces) {
        for (CtClass face : interfaces) {
            cc.addInterface(face);
        }
        return this;
    }

    public MakeClass addInterface(Class<?> interfaceClass) throws NotFoundException {
        cc.addInterface(pool.get(interfaceClass.getCanonicalName()));
        return this;
    }

    public MakeClass annotations(Map<String, Map<String, Object>> annotations) {
        ClassFile classFile = cc.getClassFile();
        Set<Map.Entry<String, Map<String, Object>>> entrySet = annotations.entrySet();
        for (Map.Entry<String, Map<String, Object>> entry : entrySet) {
            String annotationClassName = entry.getKey();
            Map<String, Object> annotationMembers = entry.getValue();
            CtClassUtil.addAnnotation(classFile, annotationClassName, annotationMembers);
        }
        return this;
    }

    public MakeClass annotation(String annotationClassName, Map<String, Object> annotationMembers) {
        return annotations(Collections.singletonMap(annotationClassName, annotationMembers));
    }

    public MakeClass annotation(String annotationClassName, String name, String value) {
        return annotation(annotationClassName, Collections.singletonMap(name, value));
    }

    public MakeClass annotation(Class<?> annotationClass, String name, String value) {
        return annotation(annotationClass.getCanonicalName(), name, value);
    }

    public MakeClass constructors(Collection<CtConstructor> constructors) throws CannotCompileException {
        for (CtConstructor constructor : constructors) {
            cc.addConstructor(constructor);
        }
        return this;
    }

    public MakeClass constructor(CtClass[] parameters, String src) throws CannotCompileException {
        CtConstructor constructor = new CtConstructor(parameters, cc);
        constructor.setBody(src);
        cc.addConstructor(constructor);
        return this;
    }

    public MakeClass fields(Collection<CtField> fields) throws CannotCompileException {
        for (CtField field : fields) {
            cc.addField(field);
        }
        return this;
    }

    public MakeClass field(String src) throws CannotCompileException {
        CtField field = CtField.make(src, cc);
        cc.addField(field);
        return this;
    }

    public MakeClass methods(Collection<CtMethod> methods) throws CannotCompileException {
        for (CtMethod method : methods) {
            cc.addMethod(method);
        }
        return this;
    }

    public MakeClass method(String src) throws CannotCompileException {
        CtMethod method = CtMethod.make(src, cc);
        cc.addMethod(method);
        return this;
    }

    public MakeClass properties(Map<String, Class<?>> properties) throws CannotCompileException {
        Set<Map.Entry<String, Class<?>>> entrySet = properties.entrySet();
        for (Map.Entry<String, Class<?>> entry : entrySet) {
            String fieldName = entry.getKey();
            Class<?> fieldClass = entry.getValue();
            property(fieldName, fieldClass);
        }
        return this;
    }

    public MakeClass property(String name, Class<?> type) throws CannotCompileException {
        String literalType = ReflectUtil.getLiteralType(type);
        String src = String.format("private %s %s;", literalType, name);
        CtField field = CtField.make(src, cc);
        cc.addField(field);

        // add getter & setter
        String prefix = type.equals(boolean.class) ? "is" : "get";
        String capitalFieldName = StringUtil.toCapitalCase(name);
        String getter = String.format("public %s %s%s(){ return this.%s; }",
                literalType, prefix, capitalFieldName, name);
        String setter = String.format("public %s set%s(%s value){ this.%s = value; }",
                literalType, capitalFieldName, literalType, name);
        CtMethod getterMethod = CtMethod.make(getter, cc);
        CtMethod setterMethod = CtMethod.make(setter, cc);
        cc.addMethod(getterMethod);
        cc.addMethod(setterMethod);
        return this;
    }

}
