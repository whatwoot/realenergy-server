package com.cs.energy.system.api.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cs.energy.system.api.entity.Config;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author gpthk
 * @since 2023-11-13
 */
public interface ConfigService extends IService<Config> {

    <T> T getGlobalConfig(Class<T> clazz);

    List<Config> getOriByCategory(String category);
    List<Config> getByCategory(String category, boolean force);
    List<Config> getByCategory(String category);

    JSONObject getJsonByCategory(String category);
    JSONObject getShowJsonByCategory(String category);
    <T> T getObjByCategory(String category, Class<T> clazz);

    List<String> getListByCategory(String category);
    List<Integer> getIntListByCategory(String category);
    List<Long> getLongListByCategory(String category);
    List<BigDecimal> getDecimalListByCategory(String category);
    <T> List<T> getObjListByCategory(String category, Class<T> clazz);

    Config getOriByCategoryAndKey(String category, String key);
    Config getByCategoryAndKey(String category, String key, boolean force);
    Config getByCategoryAndKey(String category, String key);
    String getValueByCategoryAndKey(String category, String key);

    Config getOriByKey(String key);
    Config getByKey(String key, boolean force);
    Config getByKey(String key);
    String getValueByKey(String key);
    Integer getIntByKey(String key);
    BigDecimal getBigDecimalByKey(String key);
    Long getLongByKey(String key);
    JSONObject getJsonByKey(String key);
    <T> T getObjByKey(String key, Class<T> clazz);
}
