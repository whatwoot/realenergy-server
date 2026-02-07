package com.cs.web.validator;

import com.cs.web.validator.annotation.AllowBigdecimal;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * @author sb
 * @date 2024/10/8 14:44
 */
public class AllowBigdecimalValidator implements ConstraintValidator<AllowBigdecimal, BigDecimal> {
    Set<BigDecimal> set = new HashSet<>();

    @Override
    public void initialize(AllowBigdecimal constraintAnnotation) {
        // 注解的属性
        String[] vals = constraintAnnotation.vals();
        for (String value : vals) {
            set.add(new BigDecimal(value));
        }
    }

    /**
     * 判断是否校验成功
     *
     * @param value 要校验的值
     */
    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        // 如果set中包含要校验的值，说明校验通过，否则校验失败
        return value == null || set.contains(value);
    }
}
