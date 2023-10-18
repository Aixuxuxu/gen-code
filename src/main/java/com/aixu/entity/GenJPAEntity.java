package com.aixu.entity;

import java.lang.annotation.*;

/**
 * 生成JPA对应的实体类对象
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GenJPAEntity {
    String pkgName();

    String sourcePath() default "src/main/java";

    boolean overrideSource() default false;

    boolean jpa() default true;
}
