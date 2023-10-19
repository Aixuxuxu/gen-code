## 代码生成
什么？现在都3202年了还在手动创建DTO和VO？还在手动创建JPA实体类？手动创建项目结构？
是时候引入代码生成来完成这些重复的工作了。

### 痛点
在项目开发的过程中需要建立多数的 DTO 和 VO ，这些类大多数是和实体类重复的，所以我引入了代码生成器来完成这些重复的工作。

## 解决方案
> 目前只适用于JPA项目，代码生成器是基于[这位大佬](https://gitee.com/only4playgroup)的二次开发，主要是为了适应自己的项目结构和代码风格。

### 展示
gif图，加载可能有点慢，如果看不到可以直接打开README文件修改为`代码视图`查看url
![demo](https://img.aixuxu.online/demo-gif.gif)
### 使用
1. 下载代码到项目中
```shell
git clone https://github.com/Aixuxuxu/gen-code.git
``` 
2. 将该模块打包到本地仓库
```shell
mvn clean install
```
3. 在被导入的模块中修改pom文件

```xml
 <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                    <encoding>UTF-8</encoding>

                    <annotationProcessorPaths>
                        <path>
                            <groupId>com.aixu</groupId>
                            <artifactId>gen-jar</artifactId>
                            <version>0.0.1-SNAPSHOT</version>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>

            <plugin>
                <groupId>com.mysema.maven</groupId>
                <artifactId>apt-maven-plugin</artifactId>
                <version>1.1.3</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>process</goal>
                        </goals>
                        <configuration>
                            <outputDirectory>target/generated-sources/java</outputDirectory>
                            <processor>com.querydsl.apt.jpa.JPAAnnotationProcessor</processor>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```
4. 在需要生成的模块中运行
```shell
mvn clean compile
```
### 后续功能
> 代码目前写的比较简单和潦草，后续会继续完善，欢迎大家提出建议和意见

> JPA已经很方便了，但是还是有很多重复的工作，比如说DTO和VO的转换，这些都是可以通过代码生成来完成的。
- [ ] 生成通过DTO和VO作为参数和返回值的CRUD接口
- [ ] 脱离使用BeanUtils的DTO和VO转换，通过生成MapStruct来作为转换工具