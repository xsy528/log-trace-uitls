package cn.gyyx.aoplog.webfilter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Service
public class RequestHeader {

    public final static String GYYXREFER = "GyyxReferer";

    public final static String REFER = "Referer";

    private final HttpServletRequest request;

    public RequestHeader(HttpServletRequest request) {
        this.request = request;
    }


    /**
     * 获取请求来源
     * @return 请求来源
     */
    public String getRefer(){
        String gyyxRefer = request.getHeader(GYYXREFER);
        if(StringUtils.isNotEmpty(gyyxRefer)){
            return gyyxRefer;
        }
        String refer = request.getHeader(REFER);
        if(StringUtils.isNotEmpty(refer)){
            return refer;
        }
        return "无";
    }
}
