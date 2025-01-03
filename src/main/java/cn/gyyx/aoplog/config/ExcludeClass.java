package cn.gyyx.aoplog.config;

import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 邢少亚
 * @date 2024/2/29  17:10
 * @description 入参不打印得类集合
 */
public interface ExcludeClass {

    /**
     * 判断入参是否是需要排除的类，自定义判断方法请实现该类后加入@Primy
     * @param o
     * @return
     */
    boolean isExcluded(Object o);
}
