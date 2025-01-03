package cn.gyyx.aoplog.utils;

public class TraceIdUtil {

    private static SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker(0);


    public static String getTraceId(){
        return "traceId-"+snowflakeIdWorker.nextId();
    }
}
