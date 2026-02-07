package com.cs.sp.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.cs.sp.util.StringUtil;

import java.io.IOException;

/**
 * @author sb
 * @date 2023/7/24 04:41
 */
public class NameSenseSerializer extends StdSerializer<String> {

    public NameSenseSerializer() {
        this(String.class);
    }

    protected NameSenseSerializer(Class<String> t) {
        super(t);
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }
        gen.writeString(StringUtil.senseName(value));
    }
}
