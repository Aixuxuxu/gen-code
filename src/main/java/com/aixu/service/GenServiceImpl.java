package com.aixu.service;

import java.lang.annotation.*;
import java.lang.reflect.Type;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface GenServiceImpl {

    String pkgName();

    String sourcePath() default "src/main/java";
}
