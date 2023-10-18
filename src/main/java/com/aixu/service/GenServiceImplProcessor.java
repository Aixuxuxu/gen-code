package com.aixu.service;

import cn.hutool.core.util.StrUtil;
import com.aixu.BaseCodeGenProcessor;
import com.aixu.CodeGenProcessor;
import com.aixu.DefaultNameContext;
import com.google.auto.service.AutoService;
import com.google.common.base.CaseFormat;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.transaction.Transactional;
import java.lang.annotation.Annotation;


@AutoService(value = CodeGenProcessor.class)
public class GenServiceImplProcessor extends BaseCodeGenProcessor {
    public static final String IMPL_SUFFIX = "ServiceImpl";

    @Override
    protected void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        // 获取上下文对象
        DefaultNameContext nameContext = getNameContext(typeElement);
        // 获取类名
        String className = typeElement.getSimpleName().toString().substring(4) + IMPL_SUFFIX;


        // 生成类基本结构
        TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(className)
                .addSuperinterface(
                        ClassName.get(nameContext.getServicePackageName(), nameContext.getServiceClassName().substring(4)))
                .addAnnotation(Transactional.class)
                .addAnnotation(org.springframework.stereotype.Service.class)
                .addAnnotation(Slf4j.class)
//                .addAnnotation(RequiredArgsConstructor.class)
                .addModifiers(Modifier.PUBLIC);
        // 生成类的属性
        // 如果有Repository注解，则生成对应的属性
        if (StrUtil.containsBlank(nameContext.getRepositoryPackageName())) {
            return;
        }
        String repositoryFieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL,
                nameContext.getRepositoryClassName()).substring(4);

//        String classFieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL,
//                typeElement.getSimpleName().toString());
        FieldSpec repositoryField = FieldSpec
                .builder(ClassName.get(nameContext.getRepositoryPackageName(), // 属性类型
                        nameContext.getRepositoryClassName().substring(4)), repositoryFieldName)
                .addModifiers(Modifier.PRIVATE)
                .addAnnotation(javax.annotation.Resource.class)
                .build();

        typeSpecBuilder.addField(repositoryField);

        genJavaSourceFile(generatePackage(typeElement),typeElement.getAnnotation(GenServiceImpl.class).sourcePath(),typeSpecBuilder);

    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenServiceImpl.class;
    }

    @Override
    public String generatePackage(TypeElement typeElement) {
        return typeElement.getAnnotation(GenServiceImpl.class).pkgName();
    }
}
