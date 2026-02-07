package com.cs.sp.serializer.fastjson2;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.writer.ObjectWriter;

import java.lang.reflect.Type;
import java.math.BigDecimal;


public class DecimalFast2Serializer implements ObjectWriter<BigDecimal> {
    @Override
    public void write(JSONWriter jsonWriter, Object o, Object fileName, Type fieldType, long features) {
        if (o == null) {
            jsonWriter.writeNull();
        } else {
            BigDecimal b = (BigDecimal) o;
            jsonWriter.writeString(b.stripTrailingZeros().toPlainString());
        }
    }
}
