package com.cs.sp.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author sb
 * @date 2023/7/24 04:41
 */
public class TimestampSerizlizer extends StdSerializer<Long> {

    public TimestampSerizlizer() {
        this(Long.class);
    }

    protected TimestampSerizlizer(Class<Long> t) {
        super(t);
    }

    @Override
    public void serialize(Long time, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        if (time == null) {
            gen.writeNull();
            return;
        }
        gen.writeString(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(time)));
    }
}
