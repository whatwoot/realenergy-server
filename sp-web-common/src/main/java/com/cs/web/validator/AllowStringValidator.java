package com.cs.web.validator;

import com.cs.web.validator.annotation.AllowString;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * @author sb
 * @date 2024/10/8 14:44
 */
public class AllowStringValidator implements ConstraintValidator<AllowString, String> {
    Set<String> set = new HashSet<>();

    @Override
    public void initialize(AllowString constraintAnnotation) {
        // 注解的属性
        String[] vals = constraintAnnotation.vals();
        for (String value : vals) {
            set.add(value);
        }
    }

    /**
     * 判断是否校验成功
     *
     * @param value 要校验的值
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 如果set中包含要校验的值，说明校验通过，否则校验失败
        return value == null || set.contains(value);
    }
}
