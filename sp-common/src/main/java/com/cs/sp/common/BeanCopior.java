package com.cs.sp.common;

import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.*;
import org.springframework.cglib.beans.BeanCopier;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Consumer;

import static com.cs.sp.common.WebAssert.isNotNull;

/**
 * 简单bean 拷贝实现类
 *
 * @author sb
 * @date 2018/7/19
 */
public class BeanCopior extends BeanUtils {

    private static final Logger log = LoggerFactory.getLogger(BeanCopior.class);

    public static <T> T map(Object source, Class<T> destinationClass){
        return map(source, destinationClass, null);
    }

    public static <T> T map(Object source, Class<T> destinationClass, Consumer<T> consumer) {
        if (source == null) {
            return null;
        }
        BeanCopier copier = BeanCopier.create(source.getClass(), destinationClass, false);
        T target = null;
        try {
            target = destinationClass.newInstance();
            copier.copy(source, target, null);
            if(consumer != null){
                consumer.accept(target);
            }
        } catch (InstantiationException | IllegalAccessException e) {
            log.error(e.getMessage(), e);
            WebAssert.throwBizException("sp.bean.map.fail");
        }
        return target;
    }

    public static <T> List<T> mapList(Collection sourceList, Class<T> destinationClass) {
        if(sourceList == null){
            return null;
        }
        ArrayList destinationList = new ArrayList();
        listCopy(destinationClass, destinationList, sourceList);
        return destinationList;
    }

    public static <T> List<T> mapList(Collection sourceList, Class<T> destinationClass, Consumer<T> afterCopy) {
        if(sourceList == null){
            return null;
        }
        ArrayList destinationList = new ArrayList();
        listCopy(destinationClass, destinationList, sourceList, afterCopy);
        return destinationList;
    }


    public static void copy(Object source, Object destinationObject) {
        copy(source, destinationObject, true);
    }

    public static void copy(Object source, Object destinationObject, boolean excludeNull) {
        if (excludeNull) {
            copyProperties(source, destinationObject, getNullPropertyNames(source));
        } else {
            copyProperties(source, destinationObject);
        }
    }


    /**
     * 带字段映射拷贝
     * 未映射的字段，则不转换，直接拷贝
     *
     * @param source 源对象
     * @param target 拷贝对象
     * @param map    {
     *               target.beanName : source.beanName
     *               }
     * @throws BeansException
     */
    public static void copy(Object source, Object target, Map<String, String> map) throws BeansException {

        isNotNull(source, "Source must not be null");
        isNotNull(target, "Target must not be null");

        Class<?> actualEditable = target.getClass();
        PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);

        for (PropertyDescriptor targetPd : targetPds) {
            Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod != null) {
                String mapedName = map.get(targetPd.getName());
                String readName = mapedName == null ? targetPd.getName() : mapedName;
                PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), readName);
                if (sourcePd != null) {
                    Method readMethod = sourcePd.getReadMethod();
                    if (readMethod != null &&
                            ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())) {
                        try {
                            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                                readMethod.setAccessible(true);
                            }
                            Object value = readMethod.invoke(source);
                            if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                writeMethod.setAccessible(true);
                            }
                            writeMethod.invoke(target, value);
                        } catch (Throwable ex) {
                            throw new FatalBeanException(
                                    "Could not copy property '" + targetPd.getName() + "' from source to target", ex);
                        }
                    }
                }
            }
        }
    }

    protected static <T> void listCopy(Class<T> destinationClass, ArrayList destinationList, Collection sourceList, Consumer<T> consumer) {
        BeanCopier copier = null;
        Iterator it = sourceList.iterator();
        while (it.hasNext()) {
            Object sourceObject = it.next();
            if (copier == null) {
                copier = BeanCopier.create(sourceObject.getClass(), destinationClass, false);
            }
            T destinationObject;
            try {
                destinationObject = destinationClass.newInstance();
                copier.copy(sourceObject, destinationObject, null);
                if(consumer != null){
                    consumer.accept(destinationObject);
                }
                destinationList.add(destinationObject);
            } catch (InstantiationException | IllegalAccessException e) {
                log.error(e.getMessage(), e);
                WebAssert.throwBizException("sp.bean.copy.fail");
            }
        }
    }

    protected static <T> void listCopy(Class<T> destinationClass, ArrayList destinationList, Collection sourceList) {
        listCopy(destinationClass, destinationList, sourceList, null);
    }

    protected static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }

        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
}
