package com.cs.web.util;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * @author sb
 * @date 2023/9/27 17:06
 */
public class BeanCopior extends com.cs.sp.common.BeanCopior {

    public static <T> Page<T> onlyCopyPage(Page source) {
        Page<T> destinationPage = new Page(source.getCurrent(), source.getSize());
        destinationPage.setTotal(source.getTotal());
        return destinationPage;
    }

    public static <T> Page<T> mapPage(Page source, Class<T> destinationClass) {
        Page<T> destinationPage = new Page(source.getCurrent(), source.getSize());
        destinationPage.setTotal(source.getTotal());
        ArrayList<T> list = new ArrayList<>();
        listCopy(destinationClass, list, source.getRecords());
        destinationPage.setRecords(list);
        return destinationPage;
    }

    /**
     *
     * @param source 分页page对象
     * @param destinationClass 目标类型
     * @param afterCopy 复制后回调
     * @param <T> 泛型，基本类型拷贝
     * @return 新类型的page列表对象
     */
    public static <T> Page<T> mapPage(Page source, Class<T> destinationClass, Consumer<T> afterCopy) {
        Page<T> destinationPage = new Page(source.getCurrent(), source.getSize());
        destinationPage.setTotal(source.getTotal());
        ArrayList<T> list = new ArrayList<>();
        listCopy(destinationClass, list, source.getRecords(), afterCopy);
        destinationPage.setRecords(list);
        return destinationPage;
    }
}

