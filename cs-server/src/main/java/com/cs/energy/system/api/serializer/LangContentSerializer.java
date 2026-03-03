package com.cs.energy.system.api.serializer;

import cn.hutool.core.util.ReflectUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.cs.energy.system.api.enums.NoticeTypeEnum;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fiona
 * @date 2024/9/11 03:55
 */
public class LangContentSerializer extends StdSerializer<String> {
    public static final Map<String, String> MAP = new HashMap<String, String>() {{
        put("zh-TW", "ZhTw");
        put("zh-CN", "ZhTw");
        put("en-US", "EnUs");
        put("ko-KR", "KoKr");
        put("vi-VN", "ViVn");
        put("ar-SA", "ArSa");
    }};

    public LangContentSerializer() {
        this(String.class);
    }

    public LangContentSerializer(Class<String> t) {
        super(t);
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        Object noticeTypeObj = ReflectUtil.invoke(gen.getCurrentValue(), "getNoticeType");
        // 为空或不是快讯，都不显示内容
        if(noticeTypeObj == null) {
            gen.writeNull();
            return;
        }
        // 新闻和公告不显示内容
        if(NoticeTypeEnum.NEWS.eq(noticeTypeObj.toString()) || NoticeTypeEnum.ANNOUNCE.eq(noticeTypeObj.toString())) {
            gen.writeNull();
            return;
        }

        String lang = MAP.get(LocaleContextHolder.getLocale().toLanguageTag());
        System.out.println("lang = " + lang);
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
