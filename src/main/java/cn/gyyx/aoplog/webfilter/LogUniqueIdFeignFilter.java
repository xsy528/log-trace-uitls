package cn.gyyx.aoplog.webfilter;

import cn.gyyx.aoplog.traceId.LogTraceIdFactory;
import cn.gyyx.aoplog.traceId.impl.LogUniqueIdGlobalByRequest;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class LogUniqueIdFeignFilter implements RequestInterceptor {

    private final LogTraceIdFactory factory;
    private final LogUniqueIdGlobalByRequest logUniqueIdGlobalByRequest;

    @Getter
    @Setter
    @Value("${POD_NAME:}")
    private String podName;

    public LogUniqueIdFeignFilter(LogTraceIdFactory factory, LogUniqueIdGlobalByRequest logUniqueIdGlobalByRequest) {
        this.factory = factory;
        this.logUniqueIdGlobalByRequest = logUniqueIdGlobalByRequest;
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header(logUniqueIdGlobalByRequest.TRACE_ID_HEADER, factory.getLogTraceId().getTraceId(null));
        requestTemplate.header(RequestHeader.GYYXREFER, getPodName());
    }

}
