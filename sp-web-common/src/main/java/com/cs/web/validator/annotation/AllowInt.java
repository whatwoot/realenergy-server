package com.cs.web.validator.annotation;

/**
 * @author sb
 * @date 2024/10/8 14:43
 */

import com.cs.web.validator.AllowIntegerValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
// 需要自定义一个校验器AllowConstraintValidator
@Constraint(validatedBy = {AllowIntegerValidator.class})
public @interface AllowInt {
    // 检验失败的提示信息
    String message() default "{javax.validation.constraints.error.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    // 注解的属性
    int[] vals() default {};
}
