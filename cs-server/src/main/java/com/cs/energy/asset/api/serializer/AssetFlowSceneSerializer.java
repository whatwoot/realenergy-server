package com.cs.energy.asset.api.serializer;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.cs.energy.asset.api.enums.AssetSceneEnum;
import com.cs.web.spring.config.i18n.I18nHelper;

import java.io.IOException;

/**
 * @authro fun
 * @date 2025/4/28 17:56
 */
public class AssetFlowSceneSerializer extends StdSerializer<String> {

    public AssetFlowSceneSerializer() {
        this(String.class);
    }

    protected AssetFlowSceneSerializer(Class<String> t) {
        super(t);
    }

    @Override
    public void serialize(String s, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        I18nHelper i18n = SpringUtil.getBean(I18nHelper.class);

        Object sceneObj = ReflectUtil.invoke(gen.getCurrentValue(), "getScene");
        AssetSceneEnum sceneEnum = AssetSceneEnum.of(sceneObj.toString());
        Object memoObj;
        String memo = "";
        switch (sceneEnum) {
            //理论上不会出现，因为流水分离了，只会出现在商家里
            case WITHDRAW:
            case RECHARGE:
                memoObj = ReflectUtil.invoke(gen.getCurrentValue(), "getMemo");
                if (memoObj != null) {
                    memo = memoObj.toString();
                }
            default:
                break;
        }

        gen.writeString(i18n.getMsg(String.format("msg.withdraw.scene.%s", sceneEnum.getCode()), memo));
    }
}
