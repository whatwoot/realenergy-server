package com.cs.sp.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * @author sb
 * @date 2023/7/24 04:41
 */
public class ApikeySerializer extends StdSerializer<String> {

    public ApikeySerializer() {
        this(String.class);
    }

    protected ApikeySerializer(Class<String> t) {
        super(t);
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }
        if (value.length() < 3) {
            gen.writeString("**");
        }
        String start = value.substring(0, 2);
        String end = value.substring(value.length() - 2);
        gen.writeString(String.format("%s****%s", start, end));
    }
}
