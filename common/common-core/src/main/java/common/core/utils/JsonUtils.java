package common.core.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * @author abing
 * @created 2025/5/7 15:33
 */
@Slf4j
public class JsonUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // 忽略 null 字段
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // 忽略未知字段
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")); // 设置时间格式
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 禁止时间戳
    }

    public static String toJson(Object obj) {
        if (obj == null) return null;
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Json序列化失败: {}", obj, e);
            return null;
        }
    }

    public static <T> T toBean(String json, Class<T> clazz) {
        if (json == null || clazz == null) return null;
        try {
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            log.error("Json反序列化失败: json={}, class={}", json, clazz, e);
            return null;
        }
    }

    public static <T> List<T> toList(String json, Class<T> clazz) {
        if (json == null || clazz == null) return null;
        JavaType javaType = mapper.getTypeFactory().constructCollectionType(List.class, clazz);
        try {
            return mapper.readValue(json, javaType);
        } catch (Exception e) {
            log.error("Json转List失败: json={}, class={}", json, clazz, e);
            return null;
        }
    }

    public static Map<String, Object> toMap(String json) {
        if (json == null) return null;
        try {
            return mapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.error("Json转Map失败: json={}", json, e);
            return null;
        }
    }

    public static <T> T parse(String json, TypeReference<T> typeReference) {
        if (json == null || typeReference == null) return null;
        try {
            return mapper.readValue(json, typeReference);
        } catch (Exception e) {
            log.error("Json泛型反序列化失败: json={}, type={}", json, typeReference.getType(), e);
            return null;
        }
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }
}
