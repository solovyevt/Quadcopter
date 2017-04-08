package com.quadcopter.vrep;

import com.quadcopter.VRepTargetController;
import com.quadcopter.boid.BoidController;
import coppelia.IntW;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.quadcopter.spring.VRepConfig;

import java.util.List;

/**
 * Created by solovyevt on 06.04.17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = VRepConfig.class)
public class VRepTargetControllerTest {

    @Before
    public void setUp() throws Exception {
//        System.load("/home/solovyevt/V-REP_PRO_EDU_V3_4_0_Linux/programming/remoteApiBindings/java/lib/64Bit/libremoteApiJava.so");
    }

    @Autowired
    public VRepTargetController vRepTargetController;

    @Autowired
    public BoidController boidController;

    @Test
    public void collectTargetsTest() throws Exception {
        List<IntW> handles =  vRepTargetController.collectTargets();
        System.out.println(handles);
    }
}
