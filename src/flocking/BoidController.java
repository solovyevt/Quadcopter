package flocking;

import utils.Tuple;
import utils.Vector;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by solovyevt on 14.11.15 14:10.
 */
public class BoidController {

    private float maxVelocity;

    private int numberOfBoids = 64;
    private int numberOfPredators = 8;

    private ArrayList<Boid> boids = new ArrayList<>(numberOfBoids);
    private ArrayList<Boid> predators = new ArrayList<>(numberOfPredators);

    public BoidController(Vector center, float maxRadius, float maxVelocity, int numberOfBoids, int numberOfPredators){
        this.center = center;
        this.maxRadius = maxRadius;
        this.criticalRadius = maxRadius * 1.5f;
        this.maxVelocity = maxVelocity;
        this.numberOfBoids = numberOfBoids;
        this.numberOfPredators = numberOfPredators;

        initializeBoids();
        initializePredators();
    }

    void calculateNewPositions(){
        for(Boid b: boids){
            b.calculateNewPosition(boidPreset);
        }
        for(Boid p: predators){
            p.calculateNewPosition(predatorPreset);
        }
    }

    void initializeBoids(){
        for(int i = 0; i < numberOfBoids; i++){
            boids.add(new Boid(this, randomSpawnPoint(), randomVelocity(), Color.GREEN, 0));
        }
    }

    void initializePredators(){
        for(int i = 0; i < numberOfPredators; i++){
            predators.add(new Boid(this, randomSpawnPoint(), randomVelocity(), Color.RED, 1));
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

    ArrayList<Boid> getBoids(){
        ArrayList<Boid> result = new ArrayList<>(boids);
        result.addAll(predators);
        return result;
    }

    public void render(float dt){
        calculateNewPositions();
        for(Boid b: boids){
            b.render(dt);
        }
        for(Boid p: predators){
            p.render(dt);
        }

    }

    private final Tuple[] boidPreset = {
            new Tuple<>(5f, 0.01f),
            new Tuple<>(2f, 1f),
            new Tuple<>(4f, 0.1f),
            new Tuple<>(3f, 1f),
            new Tuple<>(2f, 0.01f),
            new Tuple<>(4f, 0.01f),
            new Tuple<>(4f, 0.01f),
    };
    private final Tuple[] predatorPreset = {
            new Tuple<>(2f, 0.01f),
            new Tuple<>(1f, 1f),
            new Tuple<>(1f, 0.1f),
            new Tuple<>(3f, 1f),
            new Tuple<>(1f, 0.1f),
            new Tuple<>(5f, 0.01f),
            new Tuple<>(6f, 0.01f),
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
}
