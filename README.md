## log-tarce-uitls

**********************************

链路日志工具包
## 功能：
```
1，接口级自动入参出参日志打印，支持全局开启，白名单黑名单模式开启，支持spel表达式定制打印入参
2，打印接口处理耗时
3，链路日志打印，链路日志id，链路调用来源
4，统一的云log4j2配置，支持本地配置覆盖
5，多个重要级日志，支持动态修改日志等级，动态控制日志打印
6，采用雪花算法，提高uuid生成效率
7，支持除Controller层外的接口级入参出参打印
```


## 1、打包

```
mvn clean install
```

## 2、项目中引入

```xml
<dependency>
    <groupId>cn.xsy</groupId>
    <artifactId>cn-xsy-log</artifactId>
    <version>0.0.7</version>
</dependency>
```

## 3、配置

springboot项目配置文件application.properties：

```properties
server.port=8082

#开启全局controller层出参打印
log.controllerEnterLog=true
#开启全局controller层入参打印
log.controllerOutLog=true
#默认限制的打印日志等级
log.logLimitLevel=1
#默认链路日志工厂生成方案
log.logTraceType=globalByFeign

```

## 4、添加注解
需要开启入参出参的方法添加注解`@AOPLog`，不加则不会自动打印日志

```java
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController("/")
public class TestService {

    @AOPLog
    @GetMapping("/test")
    public String testJob(String param1, String param2) {
        return "test";
    }
    
    //只打印入参，出参不打印
    @AOPLog(logReturn=false)
    @GetMapping("/test1")
    public String testJob2(String param1, String param2) {
        return "test";
    }

    //设置日志等级，可以通过配置动态控制全局日志打印
    @AOPLog(logLevel=3)
    @GetMapping("/test2")
    public String testJob3(String param1, String param2) {
        return "test";
    }
}
```
