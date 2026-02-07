package com.cs.sp.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * @author sb
 * @date 2023/7/24 04:41
 */
public class DecimalSerializer extends StdSerializer<BigDecimal> {

    public DecimalSerializer() {
        this(BigDecimal.class);
    }

    protected DecimalSerializer(Class<BigDecimal> t) {
        super(t);
    }

    @Override
    public void serialize(BigDecimal bigDecimal, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        if (bigDecimal == null) {
            gen.writeNull();
            return;
        }
        NumberFormat nf = new DecimalFormat("0.##################");
        nf.setRoundingMode(RoundingMode.DOWN);
        gen.writeString(nf.format(bigDecimal));
    }
}
