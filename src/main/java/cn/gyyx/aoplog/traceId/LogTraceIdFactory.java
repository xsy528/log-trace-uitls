package cn.gyyx.aoplog.traceId;

import cn.gyyx.aoplog.traceId.impl.LogUniqueIdLocal;
import cn.gyyx.aoplog.config.LogConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 邢少亚
 * @date 2024/1/26  15:26
 * @description 根据配置选择获取链路id方法
 */
@Slf4j
@Component
@RefreshScope
public class LogTraceIdFactory implements ApplicationContextAware {

    public final static Map<String, LogUniqueId> EXECUTE_HANDLER = new HashMap<>(16);

    private final LogConfig logConfig;
    private final LogUniqueIdLocal logUniqueIdLocal;

    public LogTraceIdFactory(LogConfig logConfig,
                             LogUniqueIdLocal logUniqueIdLocal) {
        this.logConfig = logConfig;
        this.logUniqueIdLocal = logUniqueIdLocal;
    }

    public LogUniqueId getLogTraceId(){
        LogUniqueId logUniqueId = EXECUTE_HANDLER.get(logConfig.getLogTraceType());
        if(logUniqueId==null){
            return logUniqueIdLocal;
        }
        return logUniqueId;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, LogUniqueId> types2 = applicationContext.getBeansOfType(LogUniqueId.class);
        types2.values().forEach(e -> EXECUTE_HANDLER.putIfAbsent(e.getLogTraceType(), e));
    }
}
