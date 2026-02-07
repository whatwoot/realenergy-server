package com.cs.copy.asset.api.serializer;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.cs.sp.enums.YesNoStrEnum;
import com.cs.web.spring.config.i18n.I18nHelper;

import java.io.IOException;

/**
 * @authro fun
 * @date 2025/4/28 17:56
 */
public class RefundSerializer extends StdSerializer<String> {

    public RefundSerializer() {
        this(String.class);
    }

    protected RefundSerializer(Class<String> t) {
        super(t);
    }

    @Override
    public void serialize(String s, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        I18nHelper i18n = SpringUtil.getBean(I18nHelper.class);
        Object refundObj = ReflectUtil.invoke(gen.getCurrentValue(), "getRefunded");
        String refundMsg = "";
        if (refundObj != null && YesNoStrEnum.YES.eq(refundObj.toString())) {
            refundMsg = i18n.getMsg("msg.withdraw.refundMsg");
        }
        gen.writeString(refundMsg);
    }
}
