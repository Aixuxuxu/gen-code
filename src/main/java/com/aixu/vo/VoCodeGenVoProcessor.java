package com.aixu.vo;

import com.aixu.BaseCodeGenProcessor;
import com.aixu.CodeGenProcessor;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Set;

@AutoService(value = CodeGenProcessor.class)
public class VoCodeGenVoProcessor extends BaseCodeGenProcessor {

    public static final String SUFFIX = "VO";

    @Override
    protected void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        // 获取类过滤后的属性值
        Set<VariableElement> fields = findFields(typeElement,
                ve -> Objects.isNull(ve.getAnnotation(IgnoreVo.class)));


        // 设置类名
        String className =typeElement.getSimpleName() + SUFFIX;
        className = className.substring(4);

        //设置类结构
        TypeSpec.Builder builder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)  // 添加修饰符
                .addAnnotation(Data.class)      // 添加注解
                .addAnnotation(Schema.class);// 添加注解
//                .addAnnotation(ApiModel.class)

//        addSetterAndGetterMethod(builder,fields);
        for (VariableElement ve : fields){
            // 属性类型
            TypeName typeName = TypeName.get(ve.asType());
            FieldSpec.Builder field = FieldSpec
                    .builder(typeName, ve.getSimpleName().toString(), Modifier.PRIVATE)
                    .addAnnotation(AnnotationSpec.builder(Schema.class)
                            .addMember("title", "$S", getFieldDefaultName(ve))
                            .build()
                    );
            builder.addField(field.build());
        }
//        builder.addMethod(MethodSpec.constructorBuilder()
//                .addModifiers(Modifier.PROTECTED)
//                .build());
//        String packageName = generatePackage(typeElement);
//        genJavaFile(packageName, builder);
        genJavaSourceFile(generatePackage(typeElement),typeElement.getAnnotation(genVo.class).sourcePath(),builder);

        // 不生成source目录下的文件
//        genJavaFile(packageName, getSourceTypeWithConstruct(typeElement,sourceClassName, packageName, className));
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return genVo.class;
    }

    @Override
    public String generatePackage(TypeElement typeElement) {
        // 通过什么方式获取到 genVo 注解下的属性值呢？
        genVo annotation = typeElement.getAnnotation(genVo.class);
        return annotation.pkgName();
    }
}
