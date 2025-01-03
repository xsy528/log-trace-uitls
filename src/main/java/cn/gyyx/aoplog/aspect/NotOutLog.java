package cn.gyyx.aoplog.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 邢少亚
 * @date 2024/3/12  9:51
 * @description 全局模式下不打印出参
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotOutLog {
}
