package cn.gyyx.aoplog.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.w3c.dom.Document;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author 邢少亚
 * @date 2024/2/27  17:55
 * @description springboot参数加载时手动设置log4j2所需要的参数
 */
public class StarterInit implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        if (checkLocalHasConfig()) {
            //存在本地log4j2.xml
            return;
        }

        //使用云端log4j2-template
        String log4j2Template = "http://memcache.oa.gyyx.cn/log4j2-template.xml";
        try {
            URL url = new URL(log4j2Template);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();// 创建连接对象
            conn.setConnectTimeout(30000);
            conn.setDoOutput(true);
            conn.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            int index = 0;
            while ((line = reader.readLine()) != null) {
                if (StringUtils.isNotBlank(line)) {
                    index++;
                    //取第二个不为空的配置，判断是否云端配置是否可用
                    if (index == 2) {
                        boolean contains = line.contains("<configuration");
                        if (!contains) {
                            //404页面
                            loadDefaultConfig();
                            return;
                        } else {
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            loadDefaultConfig();
            System.out.println("http://memcache.oa.gyyx.cn/log4j2-template.xml无法访问，请检查");
            return;
        }

        //判断项目是否自定义了log4j2配置
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        String property = environment.getProperty("logging.file.path");
        if (property == null || "".equals(property)) {
            System.getProperties().setProperty("useCloudConfig", "true");
            System.getProperties().setProperty("logging.file.path", "/data/logs/${spring.cloud.kubernetes.config.sources[0].name}");
            System.getProperties().setProperty("logging.config", log4j2Template);
        }
    }

    /**
     * 检查是否存在本地配置
     * @return
     */
    private boolean checkLocalHasConfig() {
        InputStream inputStream = this.getClass().getResourceAsStream("/log4j2.xml");
        if (inputStream == null) {
            return false;
        }
        try {
            inputStream.close();
        } catch (IOException e) {

        }
        return true;
    }

    /**
     * 如果网络波动，导致无法读取云端配置，将默认读取jdk包中copy过来的log4j2.xml
     */
    private boolean loadDefaultConfig() {
        File file = new File("/data/conf/log4j2-template.xml");
        if (file.exists()) {
            System.getProperties().setProperty("logging.file.path", "/data/logs/${spring.cloud.kubernetes.config.sources[0].name}");
            System.getProperties().setProperty("logging.config", "/data/conf/log4j2-template.xml");
            return true;
        }
        return false;
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}
