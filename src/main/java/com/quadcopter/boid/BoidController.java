package com.quadcopter.boid;

import com.quadcopter.VRepTargetController;
import org.springframework.beans.factory.annotation.Autowired;
import com.quadcopter.utils.Tuple;
import com.quadcopter.utils.Vector;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

/**
 * Created by solovyevt on 14.11.15 14:10.
 */
public class BoidController{

//    @Autowired
    public VRepTargetController vRepTargetController;

    private float maxVelocity;

    private int numberOfBoids = 64;
    private int numberOfPredators = 8;

    private ArrayList<BoidModel> boidModels = new ArrayList<>(numberOfBoids);

    public BoidController(VRepTargetController vRepTargetController, Vector center, float maxRadius, float maxVelocity, int numberOfBoids, int numberOfPredators){
        this.vRepTargetController = vRepTargetController;
        this.center = center;
        this.maxRadius = maxRadius;
        this.criticalRadius = maxRadius * 1.5f;
        this.maxVelocity = maxVelocity;
        this.numberOfBoids = numberOfBoids;
        this.numberOfPredators = numberOfPredators;

        initializeBoids();

        vRepTargetController.initializeQuadcopters(boidModels);

        while(true){
            calculateNewPositions(0.0f);
        }
    }

    void calculateNewPositions(float dt){
        ArrayList<CompletableFuture> futures = new ArrayList<>();
        for(int i = 0; i < numberOfBoids; i++){
            int finalI = i;
            futures.add(CompletableFuture.runAsync(() -> {
                boidModels.get(finalI).calculateNewPosition(boidPreset);}));
        }
//        boidModels.stream().forEach(x -> {
//            CompletableFuture.runAsync(() -> x.calculateNewPosition(boidPreset));
//        });
        for(int i = numberOfBoids; i < boidModels.size(); i++){
            int finalI = i;
            futures.add(CompletableFuture.runAsync(() -> {
                boidModels.get(finalI).calculateNewPosition(predatorPreset);}));
        }
        futures.stream().forEach(x -> x.join());
        vRepTargetController.synchronize(boidModels);
//        boidModels.stream().forEach(x -> BoidView.render(x));
    }

    void initializeBoids(){
        for(int i = 0; i < numberOfBoids; i++){
            boidModels.add(new BoidModel(this,
                    randomSpawnPoint(),
                    randomVelocity(),
                    Color.GREEN,
                    (byte) 0,
                    7));
        }
        for(int i = 0; i < numberOfPredators; i++){
            boidModels.add(new BoidModel(this,
                    randomSpawnPoint(),
                    randomVelocity(),
                    Color.RED,
                    (byte) 1,
                    4));
        }
    }

    private Vector randomVelocity(){
        Random random = new Random();
        float x = (random.nextFloat() * 2 * maxVelocity) - maxVelocity;
        float y = (random.nextFloat() * 2 * maxVelocity) - maxVelocity;
        float z = (random.nextFloat() * 2 * maxVelocity) - maxVelocity;
        Vector result = new Vector(x, y, z);
        return Vector.mul(result.normalize(), maxVelocity * random.nextFloat());
    }

    private Vector randomSpawnPoint(){
        Random random = new Random();
        float x = (random.nextFloat() * 2 * maxRadius) - maxRadius;
        float y = (random.nextFloat() * 2 * maxRadius) - maxRadius;
        float z = (random.nextFloat() * 2 * maxRadius) - maxRadius;
        Vector result = new Vector(x, y, z);
        return Vector.mul(result.normalize(), maxRadius * random.nextFloat());
    }

    ArrayList<BoidModel> getBoidModels(){
        return boidModels;
    }

    public void render(float dt){
        calculateNewPositions(dt);
        /*for(BoidModel b: boidModels){
            b.render(dt);
        }*/
    }

    private static final Tuple[] boidPreset = {
//            moveToLocalCenter
            new Tuple<>(4f, 0.2f),
//            keepDistance
            new Tuple<>(1f, 0.2f),
//            keepVelocity
            new Tuple<>(3f, 0.3f),
//            limitRadius
            new Tuple<>(3f, 2f),
//            dodgeNeighbors
            new Tuple<>(1f, 0.3f),
//            dodgePredators
            new Tuple<>(4f, 1f),
//            chasePrey
            new Tuple<>(10f, 0.1f),
//            cohesion
            new Tuple<>(5f, 0.3f)
    };

    private static final Tuple[] predatorPreset = {
            new Tuple<>(0f, 0.000001f),
            new Tuple<>(1f, 0.1f),
            new Tuple<>(1f, 0.05f),
            new Tuple<>(3f, 1.5f),
            new Tuple<>(4f, 0.05f),
            new Tuple<>(4f, 0.05f),
            new Tuple<>(8f, 1f),
//            cohesion
            new Tuple<>(1f, 0.0f)
    };

    public Vector getCenter() {
        return center;
    }

    private Vector center;

    public float getMaxRadius() {
        return maxRadius;
    }

    private float maxRadius;

    public float getCriticalRadius() {
        return criticalRadius;
    }

    private float criticalRadius;

    public float getMaxVelocity() {
        return maxVelocity;
    }

    //Это перейдет к актору
    /*@Override
    public void onReceive(Object message) throws Exception {
        for(int i = 0; i < numberOfBoids; i++){
            boidModels.get(i).;
        }
        for(int i = numberOfBoids; i < boidModels.size(); i++){
            boidModels.get(i).calculateNewPosition(predatorPreset);
        }
    }*/
}
