package com.cs.copy.system.api.serializer;

import cn.hutool.extra.spring.SpringUtil;
import com.cs.web.spring.config.i18n.I18nHelper;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * @author fiona
 * @date 2024/9/11 03:55
 */
public class LangOutputSerializer extends StdSerializer<String> {

    public LangOutputSerializer() {
        this(String.class);
    }

    public LangOutputSerializer(Class<String> t) {
        super(t);
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else {
            gen.writeString(SpringUtil.getBean(I18nHelper.class).getMsg(value));
        }
    }
}
