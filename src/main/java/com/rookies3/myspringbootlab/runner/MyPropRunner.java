package com.rookies3.myspringbootlab.runner;


import com.rookies3.myspringbootlab.config.MyEnvironment;
import com.rookies3.myspringbootlab.properties.MyPropProperties;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class MyPropRunner implements ApplicationRunner {
    @Value("${myprop.userName}")
    private String name;

    @Value("${myprop.port}")
    private int port;

    @Autowired
    private MyEnvironment myEnvironment;

    @Autowired
    private Environment environment;
    @Autowired
    private MyPropProperties myPropProperties;

    private Logger logger = LoggerFactory.getLogger(MyPropRunner.class);


    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.debug("Logger 구현객체명 = {}", logger.getClass().getName());
        logger.info("현재 활성화된 MyEnvironment Bean = {}",environment);
    }
}
