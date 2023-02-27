package com.tidus.me;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestDemo {
    public static final Logger logger = LoggerFactory.getLogger(TestDemo.class);

    @Test
    public void show1() {
        logger.error("test");
        System.out.println("test1");
    }
}
