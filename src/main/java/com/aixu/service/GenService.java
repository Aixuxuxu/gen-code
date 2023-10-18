package com.aixu.service;


import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GenService {

    String pkgName();
    String sourcePath() default "src/main/java";

    boolean overrideSource() default false;

}
