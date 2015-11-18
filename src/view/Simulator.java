package view;

import flocking.BoidController;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import utils.Vector;
import view.scene.camera.Camera;
import view.scene.Quadcopter;
import view.scene.Shader;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class Simulator {

	private Camera camera;
	private Shader lightShader;
    BoidController controller;
	
	private long lastTime = 0;
	
	public void start() {
		setupDisplay();
		
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		camera = new Camera(aspectRatio, 0f, 2f, 7f);
		camera.applyPerspectiveMatrix();
		
		lightShader = new Shader("resources/shaders/light.vert", "resources/shaders/light.frag");
		
		lastTime = System.currentTimeMillis();
        controller = new BoidController(new Vector(0), 30f, 1f, 64, 8);
		while (!Display.isCloseRequested()) {
			float dt = (System.currentTimeMillis()-lastTime)/1000f;
			checkInputs();
			render(dt);
			
			lastTime = System.currentTimeMillis();
			
			Display.update();
			Display.sync(60);
		}
		
		//server.interrupt();
		
		cleanUp();
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

        /*
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		*/
		
		camera.applyModelviewMatrix(true);
		//glPolygonMode(GL_FRONT_AND_BACK, GL_POLYGON);
		
		lightShader.enable();
		lightShader.setUniform("viewMatrix", false, camera.getViewMatrix());
		

		controller.render(dt);
		
		//Primitives.setColor(Color.GRAY);
		//Primitives.drawPlane(Vector.UP, 100, 100);
		
		lightShader.disable();
	}
	
	public void setupDisplay() {
		try {
			Display.setDisplayMode(new DisplayMode(1024, 768));
			Display.setVSyncEnabled(true);
			Display.setTitle("SUPER CRAZY QUADCOPTER MANIAC DELUXE v.0");
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			cleanUp();
			System.exit(1);
		}
	}
	
	public void cleanUp() {
		Display.destroy();
	}
	
	public static void main(String [] args) {
		final Simulator sim = new Simulator();
		sim.start();
	}
	
}
