package com.cmsr.onebase.framework.common.tools.core.annotation;

import com.cmsr.onebase.framework.common.tools.core.collection.CollUtil;

import java.lang.annotation.*;
import java.lang.reflect.AnnotatedElement;
import java.util.Set;

/**
 * 注解工具类<br>
 * 快速获取注解对象、注解值等工具封装
 *
 * @author looly
 * @since 4.0.9
 */
public class AnnotationUtil {

    /**
     * 元注解
     */
    static final Set<Class<? extends Annotation>> META_ANNOTATIONS = CollUtil.newHashSet(Target.class, //
            Retention.class, //
            Inherited.class, //
            Documented.class, //
            SuppressWarnings.class, //
            Override.class, //
            Deprecated.class//
    );

    /**
     * 检查是否包含指定注解指定注解
     *
     * @param annotationEle  {@link AnnotatedElement}，可以是Class、Method、Field、Constructor、ReflectPermission
     * @param annotationType 注解类型
     * @return 是否包含指定注解
     * @since 5.4.2
     */
    public static boolean hasAnnotation(AnnotatedElement annotationEle, Class<? extends Annotation> annotationType) {
        return null != getAnnotation(annotationEle, annotationType);
    }

    /**
     * 获取指定注解
     *
     * @param <A>            注解类型
     * @param annotationEle  {@link AnnotatedElement}，可以是Class、Method、Field、Constructor、ReflectPermission
     * @param annotationType 注解类型
     * @return 注解对象
     */
    public static <A extends Annotation> A getAnnotation(AnnotatedElement annotationEle, Class<A> annotationType) {
        return (null == annotationEle) ? null : toCombination(annotationEle).getAnnotation(annotationType);
    }

    /**
     * 将指定的被注解的元素转换为组合注解元素
     *
     * @param annotationEle 注解元素
     * @return 组合注解元素
     */
    public static CombinationAnnotationElement toCombination(AnnotatedElement annotationEle) {
        if (annotationEle instanceof CombinationAnnotationElement) {
            return (CombinationAnnotationElement) annotationEle;
        }
        return new CombinationAnnotationElement(annotationEle);
    }

    /**
     * 是否不为Jdk自带的元注解。<br>
     * 包括：
     * <ul>
     *     <li>{@link Target}</li>
     *     <li>{@link Retention}</li>
     *     <li>{@link Inherited}</li>
     *     <li>{@link Documented}</li>
     *     <li>{@link SuppressWarnings}</li>
     *     <li>{@link Override}</li>
     *     <li>{@link Deprecated}</li>
     * </ul>
     *
     * @param annotationType 注解类型
     * @return 是否为Jdk自带的元注解
     */
    public static boolean isNotJdkMateAnnotation(Class<? extends Annotation> annotationType) {
        return false == isJdkMetaAnnotation(annotationType);
    }

    /**
     * 是否为Jdk自带的元注解。<br>
     * 包括：
     * <ul>
     *     <li>{@link Target}</li>
     *     <li>{@link Retention}</li>
     *     <li>{@link Inherited}</li>
     *     <li>{@link Documented}</li>
     *     <li>{@link SuppressWarnings}</li>
     *     <li>{@link Override}</li>
     *     <li>{@link Deprecated}</li>
     * </ul>
     *
     * @param annotationType 注解类型
     * @return 是否为Jdk自带的元注解
     */
    public static boolean isJdkMetaAnnotation(Class<? extends Annotation> annotationType) {
        return META_ANNOTATIONS.contains(annotationType);
    }

    /**
     * 检查是否包含指定注解<br>
     * 注解类传入全名，通过{@link Class#forName(String)}加载，避免不存在的注解导致的ClassNotFoundException
     *
     * @param annotationEle  {@link AnnotatedElement}，可以是Class、Method、Field、Constructor、ReflectPermission
     * @param annotationTypeName 注解类型完整类名
     * @return 是否包含指定注解
     * @since 5.8.37
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static boolean hasAnnotation(final AnnotatedElement annotationEle, final String annotationTypeName) {
        Class aClass = null;
        try {
            // issue#IB0JP5，Android可能无这个类
            aClass = Class.forName(annotationTypeName);
        } catch (final ClassNotFoundException e) {
            // ignore
        }
        if(null != aClass){
            return hasAnnotation(annotationEle, aClass);
        }
        return false;
    }

}