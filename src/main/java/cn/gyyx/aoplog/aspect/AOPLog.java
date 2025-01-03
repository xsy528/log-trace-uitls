package cn.gyyx.aoplog.aspect;

import cn.gyyx.aoplog.beans.LogLevel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by developer on 2021/12/9.
 */
//只有定义为 RetentionPolicy.RUNTIME（在运行时有效）时，我们才能通过反射获取到注解，然后根据注解的一系列值，变更不同的操作。
@Target(ElementType.METHOD)
// 指定生效至运行时
@Retention(RetentionPolicy.RUNTIME)
public @interface AOPLog {
    /**
     * 是否打印出参
     * true 打印出参, 默认true
     *
     * @return
     */
    boolean logReturn() default true;

    /**
     * 打印特定的入参 spel表达式
     * 示例：#param  #param.code  #param.code+'is'+#param.name
     * 如果配置了该配置，入参的格式将会替换为：[logEnterParams:spel表达式获取的值]
     * @return
     */
    String logEnterParams() default "";

    /**
     * 自定义打印信息
     * @return
     */
    String customInfo() default "";
    /**
     * 日志等级 1，极不重要 2，不重要 3，一般 4，重要  5，极其重要
     */
    LogLevel logLevel() default LogLevel.Normal;

    /**
     * 链路标识，由于当前版本未支持redis,所以该参数废弃
     */
    String traceName() default "";
}
