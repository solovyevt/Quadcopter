package com.quadcopter.flocking;

import com.quadcopter.spring.VRepConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import com.quadcopter.boid.BoidView;

public class Simulator {

    public static void main(String [] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(VRepConfig.class);
        ((BoidView) context.getBean("boidView")).start();
    }
}