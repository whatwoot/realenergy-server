package com.cs.sp.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.cs.sp.enums.YesNoStrEnum;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * @author sb
 * @date 2023/7/24 04:41
 */
public class PwdSerializer extends StdSerializer<String> {

    public PwdSerializer() {
        this(String.class);
    }

    protected PwdSerializer(Class<String> t) {
        super(t);
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        if (StringUtils.hasText(value)) {
            gen.writeString(YesNoStrEnum.YES.getCode());
        } else {
            gen.writeString(YesNoStrEnum.NO.getCode());
        }
    }
}
