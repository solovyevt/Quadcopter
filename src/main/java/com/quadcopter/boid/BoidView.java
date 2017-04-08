package com.quadcopter.boid;

import com.quadcopter.boid.BoidModel;
import com.quadcopter.boid.BoidController;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.springframework.beans.factory.annotation.Autowired;
import com.quadcopter.utils.Vector;
import com.quadcopter.view.scene.Primitives;
import com.quadcopter.view.scene.Shader;
import com.quadcopter.view.scene.camera.Camera;

import java.util.ArrayList;
import java.util.Observable;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by solovyevt on 21.11.15 13:37
 */
public class BoidView {

    public BoidController boidController;

    private Camera camera;
    private Shader lightShader;
    private long lastTime = 0;

    public BoidView(BoidController boidController){
        this.boidController = boidController;
        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
        camera = new Camera(aspectRatio, 0f, 2f, 7f);

        start();
    }

    public void start(){
        setupDisplay();

        lightShader = new Shader("src/main/resources/shaders/light.vert", "src/main/resources/shaders/light.frag");
        camera.applyPerspectiveMatrix();

        lastTime = System.currentTimeMillis();

//        controller = new BoidController(new Vector(0), 10f, 1f, 256, 32);

        while (!Display.isCloseRequested()) {
            float dt = (System.currentTimeMillis()-lastTime)/1000f;
            checkInputs();
            render(dt);

            Display.update();
            Display.sync(60);
        }

        //server.interrupt();
        cleanUp();
    }

    private void setupDisplay() {
        try {
            Display.setDisplayMode(new DisplayMode(1600, 1080));
            Display.setVSyncEnabled(true);
            Display.setTitle("SUPER CRAZY QUADCOPTER MANIAC DELUXE v.0");
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
            cleanUp();
            System.exit(1);
        }
    }

    private void cleanUp() {
        Display.destroy();
    }

    public void checkInputs() {
        if (Mouse.isButtonDown(0)) {
            if (!Mouse.isGrabbed()) {
                Mouse.setGrabbed(true);
            }
        } else {
            Mouse.setGrabbed(false);
        }

        camera.processMouse(3, 90, -90);
        camera.processKeyboard(16, 0.01f, 0.01f, 0.01f);
    }

    public void render(float dt) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(1f, 1f, 1f, 1f);


        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);


        camera.applyModelviewMatrix(true);
        glPolygonMode(GL_FRONT_AND_BACK, GL_POLYGON);

//        lightShader.enable();
        lightShader.setUniform("viewMatrix", false, camera.getViewMatrix());


        boidController.render(dt);

        //Primitives.setColor(Color.GRAY);
        //Primitives.drawPlane(Vector.UP, 100, 100);

//        lightShader.disable();
    }

    public void update(Observable o, Object arg) {
//        if(arg instanceof BoidModel){
//            render((BoidModel) arg);
//        }
//
//        else if(arg instanceof ArrayList    ){
//            render((ArrayList<Vector>) arg);
//        }
    }

    //UNDER CONSTRUCTION
    public void render(ArrayList<Vector> state){
        /*GL11.glPushMatrix();
        GL11.glTranslatef(state.get(0).x, state.get(0).y, state.get(0).z);
        Primitives.setColor(this.color);
        Primitives.drawSphere(16, 0.3f);
        Vector normalizedVelocity = currentVelocity.normalize();
        Primitives.drawLine(Vector.ZERO, normalizedVelocity);
        GL11.glPopMatrix();*/
    }

    public static void render(BoidModel boidModel){
        GL11.glPushMatrix();
        GL11.glTranslatef(boidModel.getCurrentPosition().x, boidModel.getCurrentPosition().y, boidModel.getCurrentPosition().z);
        Primitives.setColor(boidModel.getColor());
        Primitives.drawSphere(16, 0.3f);
        Vector normalizedVelocity = boidModel.getCurrentVelocity().normalize();
        Primitives.drawLine(Vector.ZERO, normalizedVelocity);
        GL11.glPopMatrix();
    }
}
