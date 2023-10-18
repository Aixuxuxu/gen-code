package com.aixu.service;

import com.aixu.BaseCodeGenProcessor;
import com.aixu.CodeGenProcessor;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;


@AutoService(value = CodeGenProcessor.class)
public class GenServiceProcessor extends BaseCodeGenProcessor {

    // 后缀
    public static final String SERVICE_SUFFIX = "Service";

    @Override
    protected void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        String className = typeElement.getSimpleName() + SERVICE_SUFFIX;
        className = className.substring(4);

        TypeSpec.Builder builder = TypeSpec.interfaceBuilder(className)
                .addModifiers(Modifier.PUBLIC);

        genJavaSourceFile(generatePackage(typeElement),typeElement.getAnnotation(GenService.class).sourcePath(),builder);
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenService.class;
    }

    @Override
    public String generatePackage(TypeElement typeElement) {

        return typeElement.getAnnotation(GenService.class).pkgName();
    }
}
