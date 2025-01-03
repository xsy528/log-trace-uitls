package cn.gyyx.aoplog.traceId;

import org.slf4j.MDC;

import java.util.UUID;

/**
 * @author 邢少亚
 * @date 2024/1/26  15:06
 * @description 生成获取traceid
 */
public interface LogUniqueId {
    String getLogTraceType();
    String GUILD = "guild";

    String getTraceId(String traceName);

    default String getTraceId(){
        //接入redis实现全链路id
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    default void setXmlTraceId(String traceId){
        MDC.put(GUILD, traceId);
    }
}
