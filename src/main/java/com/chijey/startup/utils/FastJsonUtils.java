package com.chijey.startup.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public final class FastJsonUtils {

    public static String toJson(Object bean) {
        try {
            return null != bean ? JSON.toJSONString(bean) : null;
        } catch (Exception e) {
            log.error("[FastJsonUtils]toJson, bean={}, error={}", bean, e.getMessage());
        }

        return null;
    }

    public static String toJson(Object bean, SerializeFilter filter) {
        try {
            return null != bean ? JSON.toJSONString(bean, filter) : null;
        } catch (Exception e) {
            log.error("[FastJsonUtils]toJson, bean={}, error={}", bean, e.getMessage());
        }

        return null;
    }

    public static <T> T fromJson(String json, Class<T> clazz) {

        try {
            return StringUtils.isNotBlank(json) ? JSON.parseObject(json, clazz) : null;
        } catch (Exception e) {
            log.error("[FastJsonUtils]fromJson, json={}, clazz={}, error={}", json, clazz, e.getMessage());
        }

        return null;
    }

    public static <T> T fromJson(byte[] json, Class<T> clazz) {
        return fromJson(new String(json, StandardCharsets.UTF_8), clazz);
    }

    public static <T> List<T> fromJsonToList(String json, Class<T> clazz) {
        try {
            return StringUtils.isNotBlank(json) ? JSON.parseArray(json, clazz) : null;
        } catch (Exception e) {
            log.error("[FastJsonUtils]fromJson, json={}, clazz={}, error={}", json, clazz, e.getMessage());
        }

        return null;
    }

    public static <T> T fromJson(String json, Type type) {
        try {
            return StringUtils.isNotBlank(json) ? JSON.parseObject(json, type) : null;
        } catch (Exception e) {
            log.error("[FastJsonUtils]fromJson, json={}, type={}, error={}", json, type, e.getMessage());
        }

        return null;
    }

    public static <T> T fromJson(String json, TypeReference<T> type) {
        try {
            return StringUtils.isNotBlank(json)
                    ? JSON.parseObject(json, type.getType(), ParserConfig.global, JSON.DEFAULT_PARSER_FEATURE & ~Feature.UseBigDecimal.getMask())
                    : null;
        } catch (Exception e) {
            log.error("[FastJsonUtils]fromJson, json={}, typeReference={}, error={}", json, type, e.getMessage());
        }
        return null;
    }

    public static List<Map<String, Object>> parseMapArray(String json) {
        try {
            return StringUtils.isNotBlank(json)
                    ? JSON.parseObject(json, (new TypeReference<List<Map<String, Object>>>() {}).getType(), ParserConfig.global, JSON.DEFAULT_PARSER_FEATURE & ~Feature.UseBigDecimal.getMask())
                    : null;
        } catch (Exception e) {
            log.error("[FastJsonUtils]parseMapArray, json={}, error={}", json, e.getMessage());
        }
        return null;
    }

    public static Map<String, Object> parseMap(String json) {
        try {
            return StringUtils.isNotBlank(json)
                    ? JSON.parseObject(json, (new TypeReference<Map<String, Object>>() {}).getType(), ParserConfig.global, JSON.DEFAULT_PARSER_FEATURE & ~Feature.UseBigDecimal.getMask())
                    : null;
        } catch (Exception e) {
            log.error("[FastJsonUtils]parseMap, json={}, error={}", json, e.getMessage());
        }
        return null;
    }

    public static boolean mayJson(String json) {
        if (StringUtils.isBlank(json)) {
            return false;
        }

        try {
            Object parse = JSON.parse(json);

            if (parse instanceof JSONObject || parse instanceof JSONArray) {
                return true;
            }
        } catch (Exception e) {
            log.info("[FastJsonUtils]mayJson, json={}, error={}", json, e.getMessage());
        }

        return false;
    }

    public static boolean emptyJson(String json) {
        if (StringUtils.isBlank(json)) {
            return false;
        }

        try {
            Object parse = JSON.parse(json);
            if (parse instanceof JSONObject) {
                return ((JSONObject) parse).isEmpty();
            }
            if (parse instanceof JSONArray) {
                return ((JSONArray) parse).isEmpty();
            }
        } catch (Exception e) {
            log.info("[FastJsonUtils]emptyJson, json={}, error={}", json, e.getMessage());
        }

        return false;
    }

    private static void append(Map.Entry<String, String> entry, StringBuilder sb) {
        String key = entry.getKey(), value = entry.getValue();
        if (value == null) {
            value = StringUtils.EMPTY;
        }

        sb.append('"').append(key).append('"');
        sb.append(':');
        sb.append('"').append(value).append('"');
    }

    public static Map<String, Object> objectToMap(Object obj) {
        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = obj.getClass();

        try {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object value = field.get(obj);
                map.put(fieldName, value);
            }
        } catch (IllegalAccessException e) {
            log.error("[FastJsonUtils]objectToMap, obj={}, error={}", obj, e.getMessage());
        }

        return map;
    }


    public static Map<String, Object> objectToMapIgnoreEmptyValue(Object obj) {
        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = obj.getClass();
        try {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object value = field.get(obj);
                if (Objects.isNull(value)) {
                    continue;
                }
                if (value instanceof String && StringUtils.isBlank(((String) value))) {
                    continue;
                }
                map.put(fieldName, value);

            }
        } catch (IllegalAccessException e) {
            log.error("FastJsonUtils.objectToMap ex, obj=" + obj, e);
        }
        return map;
    }
}
