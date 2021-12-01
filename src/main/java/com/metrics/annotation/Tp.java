package com.metrics.annotation;

import java.lang.annotation.*;

/**
 * @author jeff
 * @date 2021/12/1
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Tp {

    String description() default "";
}