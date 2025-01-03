package cn.gyyx.aoplog.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 邢少亚
 * @date 2024/3/12  9:50
 * @description 全局模式下不打印日志
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotLog {
}
