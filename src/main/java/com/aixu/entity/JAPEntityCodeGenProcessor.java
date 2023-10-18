package com.aixu.entity;

import com.aixu.BaseCodeGenProcessor;
import com.aixu.CodeGenProcessor;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.persistence.*;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * JPA实体类代码生成
 */
@AutoService(value = CodeGenProcessor.class)
public class JAPEntityCodeGenProcessor extends BaseCodeGenProcessor {


    @Override
    public Class<? extends Annotation> getAnnotation() {

        return GenJPAEntity.class;
    }

    @Override
    public String generatePackage(TypeElement typeElement) {
        // 获取注解下的所有属性
        GenJPAEntity genJPAEntity = typeElement.getAnnotation(GenJPAEntity.class);
        return genJPAEntity.pkgName();
    }

    @Override
    protected void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        // 1、生成类名 ,去掉前面的Base
        String className = typeElement.getSimpleName().toString().substring(4);


        /* 生成注解*/
        AnnotationSpec accessors = AnnotationSpec.builder(Accessors.class)
                .addMember("chain", "true")
                .build();

        AnnotationSpec entityListeners = AnnotationSpec.builder(EntityListeners.class)
                .addMember("value", "$T.class", AuditingEntityListener.class)
                .build();

        AnnotationSpec typeDef = AnnotationSpec.builder(TypeDef.class)
                .addMember("name", "$S", "json")
                .addMember("typeClass", "$T.class", JsonStringType.class)
                .build();

        TypeSpec.Builder build = TypeSpec.classBuilder(className)
                .addJavadoc("代码生成，小子！")
                .addModifiers(Modifier.PUBLIC)  // 添加修饰符
                .addAnnotation(Data.class)  // setter/getter
                .addAnnotation(accessors)   // 链式调用
                .addAnnotation(Entity.class)    // 实体类
                .addAnnotation(AllArgsConstructor.class)    // 有参构造
                .addAnnotation(NoArgsConstructor.class)    // 无参构造
                .addAnnotation(Table.class) // 设置表名
                .addAnnotation(DynamicInsert.class) //  // 想空值就不插入使用数据库默认值
                .addAnnotation(entityListeners) // 监听器
                .addAnnotation(typeDef); // json类型

        /* 生成属性 */
        Set<VariableElement> fields = findFields(typeElement, ve -> true);
        for (VariableElement ve : fields){
            // 属性类型
            TypeName typeName = TypeName.get(ve.asType());
            // 属性名
            String variable = ve.getSimpleName().toString();


            FieldSpec.Builder field = FieldSpec
                    .builder(typeName, variable, Modifier.PRIVATE)
                    .addJavadoc(variable)
                    .addAnnotation(AnnotationSpec.builder(Schema.class)
                            .addMember("title", "$S", ve)
                            .build()
                    );
            // 设置主键
            if ("id".equals(variable)) {
                // 设置主键注解
                field.addAnnotation(AnnotationSpec.builder(javax.persistence.GeneratedValue.class)   // 主键自增
                        .addMember("strategy", "$T.IDENTITY", javax.persistence.GenerationType.class)
                        .build()
                ).addAnnotation(Id.class);   // 主键标识
            }

            /*通过属性生成数据库类型*/
            if ("Object".equals(((ClassName) typeName).simpleName())) {
                // 设置json类型

                AnnotationSpec json = AnnotationSpec.builder(Column.class)
                        .addMember("name", "$S", variable)
                        .addMember("columnDefinition", "$S", "json")
                        .build();

                field.addAnnotation(json);
            }else if ("String".equals(((ClassName) typeName).simpleName())){
                // 设置String类型

                AnnotationSpec json = AnnotationSpec.builder(Column.class)
                        .addMember("name", "$S", variable)
                        .addMember("columnDefinition", "$S", "VARCHAR(255)")
                        .build();

                field.addAnnotation(json);
            }else if ("Long".equals(((ClassName) typeName).simpleName())){
                // 设置Long类型

                AnnotationSpec json = AnnotationSpec.builder(Column.class)
                        .addMember("name", "$S", variable)
                        .addMember("columnDefinition", "$S", "BIGINT")
                        .build();

                field.addAnnotation(json);
            }else if ("Boolean".equals(((ClassName) typeName).simpleName())){
                // 设置Boolean类型

                AnnotationSpec json = AnnotationSpec.builder(Column.class)
                        .addMember("name", "$S", variable)
                        .addMember("columnDefinition", "$S", "TINYINT(4)")
                        .build();

                field.addAnnotation(json);
            }else if ("Date".equals(((ClassName) typeName).simpleName())){
                // 设置Date类型

                AnnotationSpec json = AnnotationSpec.builder(Column.class)
                        .addMember("name", "$S", variable)
                        .addMember("columnDefinition", "$S", "DATETIME")
                        .build();

                field.addAnnotation(json);
            }else if ("Integer".equals(((ClassName) typeName).simpleName())){
                // 设置Integer类型

                AnnotationSpec json = AnnotationSpec.builder(Column.class)
                        .addMember("name", "$S", variable)
                        .addMember("columnDefinition", "$S", "INT(11)")
                        .build();

                field.addAnnotation(json);
            } else {
                // 设置列名
                field.addAnnotation(AnnotationSpec.builder(Column.class)
                        .addMember("name", "$S", variable)
                        .build()
                );
            }



            build.addField(field.build());



        }
        /* 生成java文件 */
        genJavaSourceFile(generatePackage(typeElement),typeElement.getAnnotation(GenJPAEntity.class).sourcePath(),build);

    }

}
