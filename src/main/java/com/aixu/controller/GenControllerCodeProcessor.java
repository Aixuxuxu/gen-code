package com.aixu.controller;

import com.aixu.BaseCodeGenProcessor;
import com.aixu.CodeGenProcessor;
import com.aixu.DefaultNameContext;
import com.aixu.utils.StringUtils;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;

/**
 * 生成Controller代码
 */
@AutoService(value = CodeGenProcessor.class)
public class GenControllerCodeProcessor extends BaseCodeGenProcessor {

    public static final String CONTROLLER_SUFFIX = "Controller";

    @Override
    protected void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        // 获取上下文对象
        DefaultNameContext nameContext = getNameContext(typeElement);
        // 获取基类名
        String className = typeElement.getSimpleName().toString().substring(4) + CONTROLLER_SUFFIX;

        // 生成类基本结构
        TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Slf4j.class)
                .addAnnotation(AnnotationSpec.builder(RequestMapping.class).addMember("value","$S", StringUtils.camel(typeElement.getSimpleName().toString().substring(4)) + "/v1").build())
                .addAnnotation(org.springframework.web.bind.annotation.RestController.class);

        String serviceFieldName = StringUtils.camel(typeElement.getSimpleName().toString()).substring(4) + "Service";
        if(StringUtils.containsNull(nameContext.getServicePackageName())){
            return;
        }

        FieldSpec serviceField = FieldSpec
                .builder(ClassName.get(nameContext.getServicePackageName(), nameContext.getServiceClassName().substring(4)), serviceFieldName)
                .addAnnotation(Resource.class)
                .addModifiers(Modifier.PRIVATE)
                .build();

        typeSpecBuilder.addField(serviceField);

        genJavaSourceFile(generatePackage(typeElement),typeElement.getAnnotation(GenController.class).sourcePath(),typeSpecBuilder);

    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenController.class;
    }

    @Override
    public String generatePackage(TypeElement typeElement) {
        return typeElement.getAnnotation(GenController.class).pkgName();
    }
}
