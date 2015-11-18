package view.scene;

import utils.Vector;
import org.lwjgl.opengl.GL11;

import java.awt.*;

//@TODO
public class Quadcopter {
	
	private float thrust = 0;
	private float yaw = 0;
	private float pitch = 0;
	private float roll = 0;

	private float x, y, z;
	
	public Quadcopter() {
		x = 0;
		y = 0;
		z = 0;
	}
	
	public void update(float dt) {
		
	}
	
	public Vector getForwardVector() {
    	float x = (float) (-Math.cos(Math.toRadians(90+pitch))*Math.sin(Math.toRadians(180-yaw+roll)));
    	float y = (float) (Math.sin(Math.toRadians(90+pitch)));
    	float z = (float) (Math.cos(Math.toRadians(90+pitch))*Math.cos(Math.toRadians(180-yaw+roll)));
    	return new Vector(x, y, z).normalize();
    }
	/*
	public void render(float dt) {
		update(dt);
		
		GL11.glPushMatrix();
		
		GL11.glTranslatef(x, y, z);
		
		GL11.glRotatef(pitch, 1, 0, 0);
		GL11.glRotatef(yaw, 0, 1, 0);
		GL11.glRotatef(roll, 0, 0, 1);
		
		GL11.glPopMatrix();
	}*/
}
