package com.micr.crud.app.annotation;


import com.micr.crud.app.interceptor.RetryOnFailureInterceptor;
import io.micronaut.aop.Around;
import io.micronaut.context.annotation.Type;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Around
@Type(RetryOnFailureInterceptor.class)
public @interface RetryOnFailure {
    int maxAttempts() default 3;
    long delayMs() default 500;
}