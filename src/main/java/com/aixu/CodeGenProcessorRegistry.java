package com.aixu;

import com.google.common.collect.Maps;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * @author gim 通过SPI 加载所有的CodeGenProcessor 识别要处理的annotation标记类
 */
public final class CodeGenProcessorRegistry {

  private static Map<String, ? extends CodeGenProcessor> PROCESSORS;


  private CodeGenProcessorRegistry() {
    throw new UnsupportedOperationException();
  }

  /**
   * 注解处理器要处理的注解集合
   *
   * @return
   */
  public static Set<String> getSupportedAnnotations() {
    return PROCESSORS.keySet();
  }

  /**
   *
   * @param annotationClassName 接口或类的全限定类名
   * @return 返回CodeGenProcessor实现类
   */
  public static CodeGenProcessor find(String annotationClassName) {
    return PROCESSORS.get(annotationClassName);
  }

  /**
   * spi 加载所有的processor
   *
   * @return
   */
  // 如何加载所有的Processor？
  //
  public static void initProcessors() {
    // 如何加载所有的注解处理器？
    final Map<String, CodeGenProcessor> map = Maps.newLinkedHashMap();

    ServiceLoader<CodeGenProcessor> processors = ServiceLoader.load(CodeGenProcessor.class,CodeGenProcessor.class.getClassLoader());
    Iterator<CodeGenProcessor> iterator = processors.iterator();
    while (iterator.hasNext()) {
      CodeGenProcessor next = iterator.next();
      Class<? extends Annotation> annotation = next.getAnnotation();
      map.put(annotation.getName(), next);
    }
    PROCESSORS = map;
  }

}
