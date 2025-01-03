package cn.gyyx.aoplog.config;


import cn.gyyx.aoplog.constants.LogTraceIdType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Data
@Slf4j
@RefreshScope
@Configuration
@ComponentScan(basePackages = "cn.gyyx.aoplog")
@ConfigurationProperties(prefix = "log")
public class LogConfig {

    /**
     * 全局controller层入参日志
     */
    private Boolean controllerEnterLog = false;
    /**
     * 全局controller层出参日志
     */
    private Boolean controllerOutLog = false;
    /**
     * 项目限制的打印日志等级
     */
    private Integer logLimitLevel = 1;

    /**
     * 生成traceId方式
     */
    private String logTraceType = LogTraceIdType.GLOBAL_FEIGN;

}
