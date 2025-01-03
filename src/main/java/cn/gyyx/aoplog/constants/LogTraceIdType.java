package cn.gyyx.aoplog.constants;


/**
 * @author 邢少亚
 * @date 2024/2/27  13:49
 * @description LogTraceId生成方式枚举，支持自定义
 */
public interface LogTraceIdType {

    String LOCAL = "local";

    String GLOBAL_FEIGN = "globalByFeign";

    String GLOBAL_REDIS = "globalByRedis";
}
