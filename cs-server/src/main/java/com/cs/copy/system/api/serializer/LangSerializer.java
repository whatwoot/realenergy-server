package com.cs.copy.system.api.serializer;

import cn.hutool.core.util.ReflectUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fiona
 * @date 2024/9/11 03:55
 */
public class LangSerializer extends StdSerializer<String> {
    public static final Map<String, String> MAP = new HashMap<String, String>() {{
        put("zh-CN", "ZhTw");
        put("zh-TW", "ZhTw");
        put("en-US", "EnUs");
        put("ko-KR", "KoKr");
        put("vi-VN", "ViVn");
        put("ar-SA", "ArSa");
    }};

    public LangSerializer() {
        this(String.class);
    }

    public LangSerializer(Class<String> t) {
        super(t);
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        String lang = MAP.get(LocaleContextHolder.getLocale().toLanguageTag());
        if (lang == null) {
            lang = "EnUs";
        }
        String filedName = gen.getOutputContext().getCurrentName();
        String method = "get" + filedName.substring(0, 1).toUpperCase() + filedName.substring(1) + lang;
        Object result = ReflectUtil.invoke(gen.getCurrentValue(), method);
        // 优先取语言版，取不到取默认
        if(result == null || !StringUtils.hasText(result.toString())) {
            if(value == null){
                gen.writeNull();
            }else{
                gen.writeString(value);
            }
        }else{
            gen.writeString(result.toString());
        }
    }
}
