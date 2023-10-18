package com.aixu.controller;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.TYPE})
@Documented
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface GenController {
    String pkgName();

    String sourcePath() default "src/main/java";

    boolean overrideSource() default false;

}
