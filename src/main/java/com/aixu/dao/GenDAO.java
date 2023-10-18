package com.aixu.dao;


import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = {java.lang.annotation.ElementType.TYPE})
public @interface GenDAO {
    String pkgName();

    String sourcePath() default "src/main/java";

    boolean overrideSource() default false;
}
