package com.cs.energy.system.api.enums;

import lombok.Getter;

/**
 * @author fiona
 * @date 2025/2/25 00:32
 */
@Getter
public enum SpFileServiceName {
    /**
     *
     */
    MERCHANT_LICENSE("image.merchantLicense","商户营业执照"),
    MERCHANT_PAYMENT("image.payment","商户收款方式"),
    MERCHANT_PHOTO("image.merchantPhoto","门店照片"),
    FORM_APPLY("file.form","表单申请照片"),
    MERCHANT_PAY("file.pay","做单支付"),
    ;

    private String code;
    private String msg;

    SpFileServiceName(String code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public static SpFileServiceName of(String code){
        for(SpFileServiceName value: values()){
            if(value.eq(code)){
                return value;
            }
        }
        return null;
    }

    public boolean eq(String code){
        return this.getCode().equals(code);
    }
}
