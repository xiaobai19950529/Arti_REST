package com.raysmond.artirest;

import org.springframework.beans.factory.InitializingBean;

public class TestInitializingBean implements InitializingBean{

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("Initializing");
    }

}
