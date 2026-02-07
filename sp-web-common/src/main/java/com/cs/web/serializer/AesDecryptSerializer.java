package com.cs.web.serializer;

import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.cs.sp.constant.Constant;
import com.cs.web.spring.helper.aeshelper.AesHelper;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * @author sb
 * @date 2023/7/24 04:41
 */
public class AesDecryptSerializer extends StdSerializer<String> {

    public AesDecryptSerializer() {
        this(String.class);
    }

    protected AesDecryptSerializer(Class<String> t) {
        super(t);
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else if (StringUtils.hasText(value)) {
            try {
                gen.writeString(SpringUtil.getBean(AesHelper.class).decrypt(value));
            } catch (Throwable e) {
                gen.writeString(Constant.UNDECRYPT);
            }
        } else {
            gen.writeString(value);
        }
    }
}
