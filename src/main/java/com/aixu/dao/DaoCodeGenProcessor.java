package com.aixu.dao;


import cn.hutool.core.util.StrUtil;
import com.aixu.BaseCodeGenProcessor;
import com.aixu.CodeGenProcessor;
import com.aixu.DefaultNameContext;
import com.aixu.entity.GenJPAEntity;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Parameterizable;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;

@AutoService(CodeGenProcessor.class)
public class DaoCodeGenProcessor extends BaseCodeGenProcessor{

    public static final String SUFFIX = "Repository";


    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenDAO.class;
    }

    @Override
    public String generatePackage(TypeElement typeElement) {
        // 获取注解下的所有属性
        GenDAO genDAO = typeElement.getAnnotation(GenDAO.class);
        return genDAO.pkgName();
    }

    @Override
    protected void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {

        // 获取上下文对象
        DefaultNameContext nameContext = getNameContext(typeElement);

        // 1、生成类名 ,去掉前面的Base
        String className = typeElement.getSimpleName().toString().substring(4);

        // 2、生成类结构
        if (StrUtil.containsBlank(nameContext.getJpaEntityPackageName())) {
            return;
        }
        // 为DAO添加JpaRepository包含泛型
        ParameterizedTypeName jpaRepository = ParameterizedTypeName.get(
                ClassName.get(JpaRepository.class),
                ClassName.get(nameContext.getJpaEntityPackageName(),className),
                ClassName.get(Long.class)
        );
        //
        ParameterizedTypeName querydslPredicateExecutor = ParameterizedTypeName.get(
                ClassName.get(QuerydslPredicateExecutor.class),
                ClassName.get(nameContext.getJpaEntityPackageName(),className)
        );




        TypeSpec.Builder builder = TypeSpec.interfaceBuilder(className + SUFFIX)
                .addModifiers(javax.lang.model.element.Modifier.PUBLIC)
                .addSuperinterface(jpaRepository)
                .addSuperinterface(querydslPredicateExecutor)
                .addAnnotation(org.springframework.stereotype.Repository.class);


        // 3、生成文件
        genJavaSourceFile(generatePackage(typeElement),typeElement.getAnnotation(GenJPAEntity.class).sourcePath(),builder);

    }
}
