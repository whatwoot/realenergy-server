package com.cs.sp.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * @author sb
 * @date 2023/7/24 04:41
 */
public class Money2DecimalsSerializer extends StdSerializer<BigDecimal> {

    public Money2DecimalsSerializer() {
        this(BigDecimal.class);
    }

    protected Money2DecimalsSerializer(Class<BigDecimal> t) {
        super(t);
    }

    @Override
    public void serialize(BigDecimal bigDecimal, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        if (bigDecimal == null) {
            gen.writeNull();
            return;
        }
        gen.writeString(bigDecimal.setScale(2, BigDecimal.ROUND_FLOOR).stripTrailingZeros().toPlainString());
    }
}
