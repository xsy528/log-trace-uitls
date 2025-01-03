package cn.gyyx.aoplog.traceId.impl;

import cn.gyyx.aoplog.constants.LogTraceIdType;
import cn.gyyx.aoplog.traceId.LogUniqueId;
import cn.gyyx.aoplog.utils.TraceIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.slf4j.MDC;

@Service
@Slf4j
public class LogUniqueIdLocal implements LogUniqueId {

    @Override
    public String getLogTraceType() {
        return LogTraceIdType.LOCAL;
    }

    @Override
    public String getTraceId(String traceName) {
        String traceId = MDC.get(GUILD);
        if (traceId == null) {
            //接入redis实现全链路id
            traceId = TraceIdUtil.getTraceId();
            setXmlTraceId(traceId);
        }

        return traceId;
    }
}