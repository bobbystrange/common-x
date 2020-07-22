package org.dreamcat.common.x.asm;

import javassist.ByteArrayClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.ByteMemberValue;
import javassist.bytecode.annotation.CharMemberValue;
import javassist.bytecode.annotation.ClassMemberValue;
import javassist.bytecode.annotation.DoubleMemberValue;
import javassist.bytecode.annotation.EnumMemberValue;
import javassist.bytecode.annotation.FloatMemberValue;
import javassist.bytecode.annotation.IntegerMemberValue;
import javassist.bytecode.annotation.LongMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.ShortMemberValue;
import javassist.bytecode.annotation.StringMemberValue;
import org.dreamcat.common.util.ReflectUtil;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Create by tuke on 2020/5/28
 */
public class CtClassUtil {

    public static CtClass createCtClass(String className, byte[] classBytes) throws NotFoundException {
        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(new ByteArrayClassPath(className, classBytes));
        return pool.get(className);
    }

    public static void addAnnotation(
            ClassFile classFile, String annotationClassName,
            Map<String, Object> annotationMembers) {
        ConstPool constPool = classFile.getConstPool();
        AnnotationsAttribute attribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        Annotation annotation = new Annotation(annotationClassName, constPool);

        Set<Map.Entry<String, Object>> entrySet = annotationMembers.entrySet();
        for (Map.Entry<String, Object> entry : entrySet) {
            String name = entry.getKey();
            Object value = entry.getValue();
            annotation.addMemberValue(name, castToMemberValue(value, constPool));
        }
        attribute.addAnnotation(annotation);
        classFile.addAttribute(attribute);
    }

    public static MemberValue castToMemberValue(Object value, ConstPool constPool) {
        if (value instanceof String) {
            return new StringMemberValue((String) value, constPool);
        } else if (value instanceof Byte) {
            return new ByteMemberValue((Byte) value, constPool);
        } else if (value instanceof Short) {
            return new ShortMemberValue((Short) value, constPool);
        } else if (value instanceof Character) {
            return new CharMemberValue((Character) value, constPool);
        } else if (value instanceof Integer) {
            return new IntegerMemberValue(constPool, (Integer) value);
        } else if (value instanceof Long) {
            return new LongMemberValue((Long) value, constPool);
        } else if (value instanceof Float) {
            return new FloatMemberValue((Float) value, constPool);
        } else if (value instanceof Double) {
            return new DoubleMemberValue((Double) value, constPool);
        } else if (value instanceof Boolean) {
            return new BooleanMemberValue((Boolean) value, constPool);
        } else if (value instanceof Class) {
            return new ClassMemberValue(((Class<?>) value).getCanonicalName(), constPool);
        } else if (value instanceof Enum) {
            Enum<?> en = (Enum<?>) value;
            return new EnumMemberValue(en.ordinal(), en.ordinal(), constPool);
        } else {
            Class<?> valueClass = value.getClass();
            MemberValue[] memberValues;
            if (valueClass.isArray()) {
                Object[] a = ReflectUtil.castToArray(value);
                int len = a.length;
                memberValues = new MemberValue[len];
                for (int i = 0; i < len; i++) {
                    Object v = a[i];
                    memberValues[i] = castToMemberValue(v, constPool);
                }
            } else if (value instanceof Collection) {
                Collection<?> c = (Collection<?>) value;
                int len = c.size(), i = 0;
                memberValues = new MemberValue[len];
                for (Object v : c) {
                    memberValues[i++] = castToMemberValue(v, constPool);
                }
            } else {
                throw new IllegalArgumentException("unsupported class " + valueClass.getCanonicalName());
            }

            ArrayMemberValue memberValue = new ArrayMemberValue(constPool);
            memberValue.setValue(memberValues);
            return memberValue;
        }
    }

}
