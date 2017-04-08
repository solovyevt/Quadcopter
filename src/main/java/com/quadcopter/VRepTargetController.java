package com.quadcopter;

import com.quadcopter.utils.Vector;
import coppelia.FloatWA;
import coppelia.IntW;
import coppelia.IntWA;
import coppelia.remoteApi;
import com.quadcopter.boid.BoidModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


/**
 * Created by solovyevt on 05.04.17.
 */
//@Controller
public class VRepTargetController {

    private static final String TEMPLATE_QUADCOPTER = "Quadricopter#";
    private static final String TEMPLATE_TARGET = "Quadricopter_target#";
    private IntW templateQuadcopterHandle = new IntW(0);

    final Logger logger = LogManager.getLogger(VRepTargetController.class);
    int clientID = -1;

    remoteApi vrep;

    Map<BoidModel, IntW> boid2QuadcopterHandle = new HashMap<>();
    Map<BoidModel, IntW> boid2TargetHandle = new HashMap<>();

    public VRepTargetController() {
        vrep = new remoteApi();
        vrep.simxFinish(-1);
        clientID = vrep.simxStart("127.0.0.1",19997,true,true,5000,5);
        if(clientID != -1) logger.info("VrepTargetController initialized");
        else logger.error("VrepTargetController encountered problems");
        int status = vrep.simxGetObjectHandle(clientID, TEMPLATE_QUADCOPTER, templateQuadcopterHandle, remoteApi.simx_opmode_blocking);
        if(status == 0) logger.info("Quadcopter template found"); else logger.error("Quadcopter template not found!");
    }

//    @PostConstruct
//    private void init(){
//
//    }

    public List<IntW> collectTargets(){
        int status = 0;
        List<IntW> result = new ArrayList<>();
        for (int i = 0; status == 0; i++) {
            IntW handle = new IntW(0);
            status = vrep.simxGetObjectHandle(clientID, "Quadricopter_target#" + i, handle, remoteApi.simx_opmode_blocking);
            if(status == 0) result.add(handle); else break;
        }
        return result;
    }

    private void paintTarget(List<BoidModel> boidModels){
    }

    public void initializeQuadcopters(List<BoidModel> boidModels){
        IntWA templateHandleArray = new IntWA(1);
        List<IntW> duplicatedCoptersHandles = new ArrayList<>();
        List<IntW> duplicatedTargetsHandles = new ArrayList<>();
        templateHandleArray.getArray()[0] = templateQuadcopterHandle.getValue();
        for (int i = 0; i < boidModels.size(); i++) {
            int status = vrep.simxSetIntegerSignal(clientID, "changeColor_Quadricopter_target#" + i, ((Byte) boidModels.get(i).RANK).intValue(), remoteApi.simx_opmode_oneshot);
            IntWA duplicatedCoptersHandle = new IntWA(1);
            status = vrep.simxCopyPasteObjects(clientID, templateHandleArray, duplicatedCoptersHandle, remoteApi.simx_opmode_blocking);
            vrep.simxGetObjectPosition(clientID, duplicatedCoptersHandle.getArray()[0], -1, new FloatWA(3), remoteApi.simx_opmode_streaming);
            IntW duplicatedTargetHandle = new IntW(0);
            status = vrep.simxGetObjectHandle(clientID, "Quadricopter_target#" + i, duplicatedTargetHandle, remoteApi.simx_opmode_blocking);

            duplicatedCoptersHandles.add(new IntW(duplicatedCoptersHandle.getArray()[0]));
            duplicatedTargetsHandles.add(duplicatedTargetHandle);
        }

        for (int i = 0; i < duplicatedCoptersHandles.size(); i++) {
            boid2QuadcopterHandle.put(boidModels.get(i), duplicatedCoptersHandles.get(i));
            boid2TargetHandle.put(boidModels.get(i), duplicatedTargetsHandles.get(i));
        }

        boidModels.stream().forEach(x -> {
            FloatWA targetPos = new FloatWA(3);
            for (int i = 0; i < targetPos.getLength(); i++) {
                targetPos.getArray()[i] = x.getCurrentPosition().toArray()[i];
            }
            vrep.simxSetObjectPosition(clientID,
                    boid2QuadcopterHandle.get(x).getValue(),
                    -1,
                    targetPos,
                    remoteApi.simx_opmode_oneshot
            );

        });
        logger.info("Quadcopters duplicated: " + boidModels.size());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void synchronize(List<BoidModel> boidModels){
        List<CompletableFuture> futures = new ArrayList<>();
        boidModels.stream().forEach(x -> {
                    FloatWA quadcopterPos = new FloatWA(3);
                    vrep.simxGetObjectPosition(clientID, boid2QuadcopterHandle.get(x).getValue(), -1, quadcopterPos, remoteApi.simx_opmode_streaming);
                    Vector newPosition = new Vector(
                            quadcopterPos.getArray()[0],
                            quadcopterPos.getArray()[1],
                            quadcopterPos.getArray()[2]);
                    x.setCurrentPosition(newPosition);
                }
        );
        futures.stream().forEach(x -> x.join());

        boidModels.stream().forEach(x -> {
            FloatWA targetPos = new FloatWA(3);
            for (int i = 0; i < targetPos.getLength(); i++) {
                targetPos.getArray()[i] = x.getCurrentPosition().toArray()[i] + x.getCurrentVelocity().toArray()[i];
            }

            vrep.simxSetObjectPosition(clientID,
                    boid2TargetHandle.get(x).getValue(),
                    -1,
                    targetPos,
                    remoteApi.simx_opmode_oneshot
            );
        });
    }
}
