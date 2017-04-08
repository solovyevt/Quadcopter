package com.quadcopter.spring;

import com.quadcopter.boid.BoidController;
import com.quadcopter.boid.BoidView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import com.quadcopter.utils.Vector;
import com.quadcopter.VRepTargetController;

/**
 * Created by solovyevt on 05.04.17.
 */

@Configuration
@ComponentScan(basePackages = {
        "com.quadcopter.*"
})
public class VRepConfig {

    @Bean
    public BoidController boidController() {
        return new BoidController(vRepTargetController(), new Vector(0), 9f, 5f, 64, 10);
    }

//    @Bean
//    public BoidView boidView(){
//        return new BoidView(boidController());
//    }

    @Bean VRepTargetController vRepTargetController() {
        return new VRepTargetController();
    }

//    @Bean
//    public VRepTargetController vRepTargetController() {
//        return new VRepTargetController();
//    }
}
