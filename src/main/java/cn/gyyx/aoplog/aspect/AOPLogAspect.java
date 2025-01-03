package cn.gyyx.aoplog.aspect;

import cn.gyyx.aoplog.beans.LogLevel;
import cn.gyyx.aoplog.config.ExcludeClass;
import cn.gyyx.aoplog.traceId.LogTraceIdFactory;
import cn.gyyx.aoplog.config.LogConfig;
import cn.gyyx.aoplog.webfilter.RequestHeader;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author 邢少亚
 * @date 2024/1/23  17:17
 * @description 切面级日志
 */
@Slf4j
@Aspect
@Component
public class AOPLogAspect {

    private final LogTraceIdFactory factory;
    private final LogConfig logConfig;
    private final ExcludeClass excludeClass;
    private final RequestHeader requestHeader;

    private Boolean useCloudConfig = System.getProperties().getProperty("useCloudConfig")!=null;

    //通过ThreadLocal隔离不同线程的变量
    private final ThreadLocal<Long> timeRecord = new ThreadLocal<>();

    /**
     * 解析SpEL表达式的类
     */
    private final ExpressionParser parser = new SpelExpressionParser();
    /**
     * 字节码分析
     */
    private final LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();


    public AOPLogAspect(LogConfig logConfig, LogTraceIdFactory factory, ExcludeClass excludeClass, RequestHeader requestHeader) {
        this.logConfig = logConfig;
        this.factory = factory;
        this.excludeClass = excludeClass;
        this.requestHeader = requestHeader;
    }

    /**
     * 解析 spel 表达式
     *
     * @param method    方法
     * @param arguments 参数
     * @param spel      表达式
     * @return 执行spel表达式后的结果
     */
    private Object parseSpel(Method method, Object[] arguments, String spel) {
        String[] params = discoverer.getParameterNames(method);
        EvaluationContext context = new StandardEvaluationContext();
        for (int len = 0; len < params.length; len++) {
            context.setVariable(params[len], arguments[len]);
        }
        try {
            Expression expression = parser.parseExpression(spel);
            return expression.getValue(context, Object.class);
        } catch (Exception e) {
            log.error("方法：{}配置的AOPLog的logEnterParams参数配置的表达式错误，无法解析，{}",method.getName(),e);
            return null;
        }
    }

    @Pointcut("@annotation(cn.gyyx.aoplog.aspect.AOPLog)")
    public void jointPoint() {
    }

    @Pointcut("bean(*Controller)")
    public void controllerPoint() {
    }

    @Before("jointPoint()")
    public void doBefore(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获取方法上的注解，判断如果isDetail值为true，则打印结束日志
        Method method = signature.getMethod();
       methodBefore(joinPoint,true,method);
       timeRecord.set(System.currentTimeMillis());
    }

    @Before("controllerPoint()")
    public void controllerDoBefore(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获取方法上的注解，判断如果isDetail值为true，则打印结束日志
        Method method = signature.getMethod();
        if(logConfig.getControllerEnterLog()) {
            methodBefore(joinPoint,false, method);
        }
        if(logConfig.getControllerOutLog()) {
            //只有打印出参才加入计时
            NotOutLog notEnterLog = method.getAnnotation(NotOutLog.class);
            if(notEnterLog==null) {
                timeRecord.set(System.currentTimeMillis());
            }
        }
    }

    /**
     * 打印入参
     * @param joinPoint 切面信息
     * @param isAnnotation 是否是注解形式的日志
     */
    private void methodBefore(JoinPoint joinPoint,boolean isAnnotation,Method method){

        String uuid;
        String customInfo = "";
        //获取拼接类/方法名称
        String methodName = method.getName();

        // 获取入参
        Object[] param = joinPoint.getArgs();
        StringBuilder sb = new StringBuilder();

        if(isAnnotation) {
            AOPLog annotation = method.getAnnotation(AOPLog.class);
            LogLevel level = annotation.logLevel();
            if (level.getLogLevel() < logConfig.getLogLimitLevel()) {
                //低于目标日志等级，不打印日志
                return;
            }
            String traceName = annotation.traceName();
            uuid = factory.getLogTraceId().getTraceId(traceName);

            //打印入参日志，支持自定义信息
            customInfo = annotation.customInfo();

            //判断是否需要指定打印某个参数抑或是某个值
            String logParams = annotation.logEnterParams();
            if("".equals(logParams)){
                //默认打印所有入参，排除黑名单中的类
                convertRequest(param,sb,method);
            }else {
                //打印特定的类或参数，白名单模式
                Object object = parseSpel(method, param, logParams);
                if(object==null){
                    return;
                }
                sb.append(logParams);
                sb.append(":");
                sb.append(JSON.toJSONString(object));
            }
        }else {
            NotLog notLog = method.getAnnotation(NotLog.class);
            if(notLog!=null){
                return;
            }
            NotEnterLog notEnterLog = method.getAnnotation(NotEnterLog.class);
            if(notEnterLog!=null){
                return;
            }

            convertRequest(param,sb,method);
            uuid = factory.getLogTraceId().getTraceId("default");
        }

        String className = method.getDeclaringClass().getName();
        String controllerName = className.substring(className.lastIndexOf(".") + 1);
        String printName = controllerName + "." + methodName;
        String refer = requestHeader.getRefer();

        if ("".equals(customInfo)) {
            if(useCloudConfig){
                log.info("方法[{}]开始\n参数    :  [{}]\n来源    :  [{}]",printName, sb, refer);
            }else {
                log.info("方法[{}]开始\n参数    :  [{}]\n流程id  :  [{}]\n来源    :  [{}]",printName, sb, uuid, refer);
            }
        } else {
            if(useCloudConfig){
                log.info("方法[{}]开始\n参数    : [{}]\n来源    :  [{}]\n自定义信息  : [{}]"
                        ,printName, sb, refer, customInfo);
            }else {
                log.info("方法[{}]开始\n参数       : [{}]\n流程id     : [{}]\n来源    :  [{}]\n自定义信息  : [{}]"
                        ,printName, sb, uuid, refer, customInfo);
            }

        }
    }

    private void convertRequest(Object[] param, StringBuilder sb, Method method){
        //所有参数名称及顺序
        Parameter[] parameters = method.getParameters();
        for (int i=0;i<param.length;i++) {
            Object o = param[i];
            //过滤请求参数
            if(excludeClass.isExcluded(o)){
                continue;
            }
            String paramName = parameters[i].getName();
            sb.append(paramName).append(":").append(JSON.toJSONString(o)).append(";");
        }
    }

    /**
     * 注解打印日志，支持配置日志等级，自定义参数，链路id
     *
     * @param joinPoint
     */
    @AfterReturning(returning = "result", pointcut = "jointPoint()")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        methodAfter(joinPoint, result,true);
    }

    @AfterReturning(returning = "result", pointcut = "controllerPoint()")
    public void controllerAfterReturning(JoinPoint joinPoint, Object result) {
        if(logConfig.getControllerOutLog()) {
            methodAfter(joinPoint, result,false);
        }
    }

    /**
     * 打印出参信息
     * @param joinPoint 切面信息
     * @param result 返回结果
     * @param isAnnotation 是否是注解形式的日志
     */
    private void methodAfter(JoinPoint joinPoint, Object result,boolean isAnnotation){
        long beginTime = timeRecord.get();
        long consumeTime = System.currentTimeMillis() - beginTime;
        timeRecord.remove();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获取方法上的注解，判断如果isDetail值为true，则打印结束日志
        Method method = signature.getMethod();

        String customInfo = "";
        String uuid;
        boolean logReturn;
        if(isAnnotation){
            AOPLog annotation = method.getAnnotation(AOPLog.class);
            String traceName = annotation.traceName();
            uuid = factory.getLogTraceId().getTraceId(traceName);

            //打印入参日志，支持自定义信息
            customInfo = annotation.customInfo();
            //打印出参日志，支持开关
            logReturn = annotation.logReturn();
        }else {
            NotLog notLog = method.getAnnotation(NotLog.class);
            if(notLog!=null){
                return;
            }
            NotOutLog notOutLog = method.getAnnotation(NotOutLog.class);
            if(notOutLog!=null){
                return;
            }

            uuid = factory.getLogTraceId().getTraceId("default");
            logReturn = logConfig.getControllerOutLog();
        }

        if (logReturn) {
            //获取拼接类/方法名称
            String methodName = signature.getName();
            String className = method.getDeclaringClass().getName();
            String controllerName = className.substring(className.lastIndexOf(".") + 1);
            String printName = controllerName + "." + methodName;
            String resp = JSON.toJSONString(result, SerializerFeature.WriteMapNullValue);

            if ("".equals(customInfo)) {
                if(useCloudConfig){
                    log.info("方法[{}]执行结束\n耗时    :  [{}ms]\n返回值   : [{}]"
                            ,printName, consumeTime, resp);
                }else {
                    log.info("方法[{}]执行结束\n流程id  :  [{}]\n耗时    :  [{}ms]\n返回值  : [{}]"
                            ,printName, uuid, consumeTime, resp);
                }

            } else {
                if(useCloudConfig){
                    log.info("方法[{}]执行结束\n耗时     :  [{}ms]\n返回值    : [{}]\n自定义信息  : [{}]",
                            printName, consumeTime, resp, customInfo);
                }else {
                    log.info("方法[{}]执行结束\n流程id     :  [{}]\n耗时       :  [{}ms]\n返回值     : [{}]\n自定义信息  : [{}]",
                            printName,uuid, consumeTime, resp, customInfo);
                }
            }
        }
    }
}
