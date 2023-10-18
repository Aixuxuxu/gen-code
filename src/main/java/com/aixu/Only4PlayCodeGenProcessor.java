package com.aixu;


import com.aixu.context.ProcessingEnvironmentHolder;
import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic.Kind;
import java.util.Set;

/**
 * @author gim
 */
@AutoService(Processor.class)
// 该项目的代码生成类
public class Only4PlayCodeGenProcessor extends AbstractProcessor {

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    annotations.stream().forEach(an -> {
      // 获取注解标注的所有元素
      Set<? extends Element> typeElements = roundEnv.getElementsAnnotatedWith(an);
      // 筛选出该注解标注的所有类和接口
      Set<TypeElement> types = ElementFilter.typesIn(typeElements);

      // 循环所有的类和接口
      for (TypeElement typeElement : types){

//        返回CodeGenProcessor实现类
        CodeGenProcessor codeGenProcessor = CodeGenProcessorRegistry.find(
            an.getQualifiedName().toString());
        try {
          // 代码生成逻辑
          codeGenProcessor.generate(typeElement,roundEnv);
        } catch (Exception e) {
          // 生成错误信息
          ProcessingEnvironmentHolder.getEnvironment().getMessager().printMessage(Kind.ERROR,"代码生成异常:" + e.getMessage());
        }
      }

    });
    // 为毛要写出false
    return false;
  }

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    ProcessingEnvironmentHolder.setEnvironment(processingEnv);
    CodeGenProcessorRegistry.initProcessors();
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return CodeGenProcessorRegistry.getSupportedAnnotations();
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }
}
