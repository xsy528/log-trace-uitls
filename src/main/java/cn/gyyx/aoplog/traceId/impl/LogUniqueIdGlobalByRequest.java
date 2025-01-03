package cn.gyyx.aoplog.traceId.impl;

import cn.gyyx.aoplog.constants.LogTraceIdType;
import cn.gyyx.aoplog.traceId.LogUniqueId;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 邢少亚
 * @date 2024/1/29  11:31
 * @description feign时放入header中，实现全局上下文
 */
@Service
@Slf4j
public class LogUniqueIdGlobalByRequest  implements LogUniqueId {

    public final String TRACE_ID_HEADER = "LogTraceId";

    private final HttpServletRequest request;
    private final LogUniqueIdLocal logUniqueIdLocal;

    public LogUniqueIdGlobalByRequest(HttpServletRequest request, LogUniqueIdLocal logUniqueIdLocal) {
        this.request = request;
        this.logUniqueIdLocal = logUniqueIdLocal;
    }

    @Override
    public String getLogTraceType() {
        return LogTraceIdType.GLOBAL_FEIGN;
    }

    @Override
    public String getTraceId(String traceName) {
        String header;
        try{
            header = request.getHeader(TRACE_ID_HEADER);
            if(StringUtils.isEmpty(header)){
                return logUniqueIdLocal.getTraceId(traceName);
            }
            String traceId = logUniqueIdLocal.getTraceId(traceName);
            if(StringUtils.isEmpty(traceId)){
                setXmlTraceId(header);
            }

            return header;
        }catch (Exception e){
            return logUniqueIdLocal.getTraceId(traceName);
        }
    }
}
