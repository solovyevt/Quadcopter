package com.quadcopter.boid;

import org.lwjgl.opengl.GL11;
import com.quadcopter.utils.SlidingAverage;
import com.quadcopter.utils.Tuple;
import com.quadcopter.utils.Vector;
import com.quadcopter.view.scene.Primitives;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by solovyevt on 14.11.15 14:11.
 */

/*
REMEMBAH: может быть несколько групп боидов, и каждая группа может иметь свои правила поведения
 @TODO Вынести все вычисления в акторы, BoidControllerActor и BoidActor взаимодействуют только между собой и только при помощи сообщений
 @TODO Добавить физику
 @TODO Вынести все, что связано с рендерингом, в com.quadcopter.view
 @TODO Покурить мануалы по методу Верле
 @TODO Обнаружен странный баг: один из боидов часто начинает крутиться по спирали в центре, как только его подхватывает другой боид - все ок.
 */
public class BoidModel {
    public final byte RANK;
    private float defaultVelocity = 0.8f;
    private BoidController controller;
    private Vector currentPosition;
    private SlidingAverage slidingAverage;

    public Vector getCurrentVelocity() {
        return currentVelocity;
    }

    Vector currentVelocity;

    public Color getColor() {
        return color;
    }

    Color color;
    float m;

    BoidModel(BoidController controller, Vector initPosition, Vector initVelocity, Color color, byte rank, int slidingAverageCapacity){
//        this.addObserver(Simulator.view);
        this.controller = controller;
        this.currentPosition = initPosition;
        this.currentVelocity = initVelocity;
        this.color = color;
        this.RANK = rank;
        this.slidingAverage = new SlidingAverage(slidingAverageCapacity);
    }

    ArrayList<BoidModel> getNeighbors(double r){
        ArrayList<BoidModel> neighbors = new ArrayList<>();
        for(BoidModel b: controller.getBoidModels()){
            if(Vector.manhattanDistance(b.getCurrentPosition(), this.currentPosition) < r){
                neighbors.add(b);
            }
        }
        return neighbors;
    }


    ArrayList<BoidModel> getMates(double r){
        ArrayList<BoidModel> neighbors = new ArrayList<>();
        for(BoidModel b: controller.getBoidModels()){
            if(Vector.manhattanDistance(b.getCurrentPosition(), this.currentPosition) < r && b.RANK == this.RANK && !b.currentPosition.equals(this.currentPosition)){
                neighbors.add(b);
            }
        }
        return neighbors;
    }

    ArrayList<BoidModel> getPredators(double r){
        ArrayList<BoidModel> predators = new ArrayList<>();
        for(BoidModel b: controller.getBoidModels()){
            if(Vector.manhattanDistance(b.getCurrentPosition(), this.currentPosition) < r && b.RANK > this.RANK){
                predators.add(b);
            }
        }
        return predators;
    }

    ArrayList<BoidModel> getPrey(double r){
        ArrayList<BoidModel> prey = new ArrayList<>();
        for(BoidModel b: controller.getBoidModels()){
            if(Vector.manhattanDistance(b.getCurrentPosition(), this.currentPosition) < r && b.RANK < this.RANK){
                prey.add(b);
            }
        }
        return prey;
    }

    public Vector getCurrentPosition() {
        return currentPosition;
    }

    @Deprecated
    public void render(float dt){
        GL11.glPushMatrix();
        GL11.glTranslatef(currentPosition.x, currentPosition.y, currentPosition.z);
        Primitives.setColor(this.color);
        Primitives.drawSphere(16, 0.3f);
        Vector normalizedVelocity = currentVelocity.normalize();
        Primitives.drawLine(Vector.ZERO, normalizedVelocity);
        GL11.glPopMatrix();
    }

    private void limitVelocity(){
        if(this.currentVelocity.length() > controller.getMaxVelocity()){
            this.currentVelocity = Vector.mul(currentVelocity.normalize(), controller.getMaxVelocity());
        }
    }

    private Vector limitRadius() {
        Vector result = new Vector(0);
        if (this.currentPosition.length() > controller.getMaxRadius()) {
            /*if(boid.currentPosition.x > (center.x + maxRadius)){
                result.x = - maxVelocity * (boid.currentPosition.x - (center.x + maxRadius) / criticalRadius);
            }
            else {
                result.x = maxVelocity * ((boid.currentPosition.x + (center.x + maxRadius)) / criticalRadius);
            }

            if(boid.currentPosition.y > (center.y + maxRadius)){
                result.y = - maxVelocity * ((boid.currentPosition.y - (center.y + maxRadius)) / criticalRadius);
            }
            else {
                result.y = maxVelocity * ((boid.currentPosition.y + (center.y + maxRadius)) / criticalRadius);
            }

            if(boid.currentPosition.z > (center.z + maxRadius)){
                result.z = - maxVelocity * ((boid.currentPosition.z - (center.z + maxRadius)) / criticalRadius);
            }
            else {
                result.z = maxVelocity * ((boid.currentPosition.z + (center.z + maxRadius)) / criticalRadius);
            }*/
            result = Vector.mul(Vector.sub(controller.getCenter(), this.currentPosition), (this.currentPosition.length() - controller.getMaxRadius()) / controller.getCriticalRadius());
        }
        return result;
    }

    @SafeVarargs
    //@TODO Пересмотреть механизм применения правил
    final void calculateNewPosition(Tuple<Float, Float>... c){
        Vector r1, r2, r3, r4, r5, r6, r7, r8;
        //@TODO проблема с движением боидов  к центру масс, при использовании боиды просто исчезают :/
        r1 = Vector.mul(moveToLocalCenter(c[0].x), c[0].y);
        //@TODO Допилить уклонение от объектов сцены, пока не работает
        r2 = Vector.mul(keepDistance(c[1].x), c[1].y);
        r3 = Vector.mul(keepVelocity(c[2].x), c[2].y);
        r4 = Vector.mul(limitRadius(), c[3].y);
        r5 = Vector.mul(dodgeNeighbors(c[4].x), c[4].y);
        r6 = Vector.mul(dodgePredators(c[5].x), c[5].y);
        r7 = Vector.mul(chasePrey(c[6].x), c[6].y);
        r8 = Vector.mul(cohesion(c[7].x), c[7].y);
        Vector direction = Vector.add(r1, r2, r3, r4, r5, r6, r7, r8);
        currentVelocity = Vector.add(Vector.mul(currentVelocity, defaultVelocity / currentVelocity.length()), slidingAverage.getNewValue(direction));
        currentPosition = Vector.add(currentPosition, currentVelocity);
        limitVelocity();
//        this.setChanged();
//        notifyObservers(this);
    }

    //Правило определяющее движение боида к центру масс окружающих его боидов

    Vector moveToLocalCenter(float r){
        Vector center = new Vector(0);
        ArrayList<BoidModel> neighbors = this.getMates(r);
        for(BoidModel b: neighbors){
            center = Vector.add(center, b.getCurrentPosition());
        }
        center = neighbors.size() == 0 ? center : Vector.div(center, neighbors.size());
        return Vector.sub(center, this.currentPosition);
    }

    //@TODO Правило, сохраняющее дистанцию до препятствий на пути
    //@TODO Это плохой способ избегать коллизий. Нужно поддерживать расстояние каким-то иным способом.
    Vector keepDistance(float r) {
        Vector dodgeDirection = new Vector(0);
        ArrayList<BoidModel> neighbors = this.getNeighbors(r);
        for (BoidModel b : neighbors) {
            //dodgeDirection = Vector.sub(dodgeDirection, Vector.mul(Vector.sub(b.currentPosition, this.currentPosition), 0.1f / Vector.manhattanDistance(b.currentPosition, this.currentPosition)));
            dodgeDirection = Vector.sub(dodgeDirection, Vector.sub(b.currentPosition, this.currentPosition));
        }
        //dodgeDirection = Vector.div(dodgeDirection, neighbors.size());
        return (neighbors.size() == 0) ? dodgeDirection : Vector.div(dodgeDirection, neighbors.size());
    }


    //Правило выравнивающее скорость с соседними боидами
    Vector keepVelocity(float r){
        Vector acceleration = new Vector(0);
        ArrayList<BoidModel> mates = this.getMates(r);
        for (BoidModel b : mates) {
            acceleration = Vector.add(acceleration, b.currentVelocity);
        }
        //acceleration = Vector.div(acceleration, neighbors.size());
        return (mates.size() == 0) ? acceleration : Vector.div(acceleration, mates.size());
    }

    //Уклонение от соседей
    Vector dodgeNeighbors(float r){
        Vector dodgeDirection = new Vector(0);
        ArrayList<BoidModel> neighbors = this.getMates(r);
        for (BoidModel b : neighbors) {
            dodgeDirection = Vector.sub(dodgeDirection, Vector.mul(Vector.sub(b.currentPosition, this.currentPosition), 1 / Vector.manhattanDistance(b.currentPosition, this.currentPosition)));
        }
        //dodgeDirection = Vector.div(dodgeDirection, neighbors.size());
        return (neighbors.size() == 0) ? dodgeDirection : Vector.div(dodgeDirection, neighbors.size());
    }

    //Уклонение от хищников
    Vector dodgePredators(float r){
        Vector dodgeDirection = new Vector(0);
        ArrayList<BoidModel> predators = this.getPredators(r);
        for (BoidModel p : predators) {
            dodgeDirection = Vector.sub(dodgeDirection, Vector.sub(p.currentPosition, this.currentPosition));
        }
        //dodgeDirection = Vector.div(dodgeDirection, predators.size());
        return (predators.size() == 0) ? dodgeDirection : Vector.div(dodgeDirection, predators.size());
    }

    //Погоня за жертвой
    Vector chasePrey(float r){
        Vector chaseDirection = new Vector(0);
        ArrayList<BoidModel> prey = this.getPrey(r);
        for (BoidModel p : prey) {
            chaseDirection = Vector.add(chaseDirection, Vector.mul(Vector.sub(p.currentPosition, this.currentPosition), 1 / Vector.manhattanDistance(p.currentPosition, this.currentPosition)));
        }
        //chaseDirection = Vector.div(chaseDirection, prey.size());
        return (prey.size() == 0) ? chaseDirection : Vector.div(chaseDirection, prey.size());
    }

    Vector cohesion(float r) {
        Vector cohesionVector = new Vector(0);
        ArrayList<BoidModel> mates = this.getMates(r);
        for(BoidModel mate : mates){
            cohesionVector = Vector.add(cohesionVector, Vector.mul(mate.getCurrentVelocity().normalize(), 1 / Vector.manhattanDistance(mate.currentPosition, this.currentPosition)));
        }
        return cohesionVector.normalize();
    }


    ArrayList getObstacles(float r){
        return new ArrayList();
    }


    //@TODO Возвращение вектора состояния боида. В дальнейшем стоит расширить и заменить этим методом все прямые обращения к элементам боида. Наверное?
    ArrayList<Vector> getCurrentState(){
        ArrayList<Vector> state = new ArrayList<>();
        state.add(currentPosition);
        state.add(currentVelocity);
        return state;
    }

    public void setCurrentPosition(Vector currentPosition) {
        this.currentPosition = currentPosition;
    }

    public void setCurrentVelocity(Vector currentVelocity) {
        this.currentVelocity = currentVelocity;
    }
}
