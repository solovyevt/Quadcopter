package com.quadcopter.quadcopter;

import com.quadcopter.boid.BoidModel;
import org.lwjgl.opengl.GL11;
import com.quadcopter.utils.Vector;
import com.quadcopter.view.scene.Primitives;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by solovyevt on 27.11.15 21:05.
 */
public class QuadcopterView implements Observer {
    public QuadcopterView(){

    }

    @Override
    public void update(Observable o, Object arg) {
        if(arg instanceof BoidModel){
            render((BoidModel) arg);
        }

        else if(arg instanceof ArrayList){
            render((ArrayList<Vector>) arg);
        }
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

    public void render(BoidModel boidModel){
        GL11.glPushMatrix();
        GL11.glTranslatef(boidModel.getCurrentPosition().x, boidModel.getCurrentPosition().y, boidModel.getCurrentPosition().z);
        Primitives.setColor(boidModel.getColor());
        Primitives.drawSphere(16, 0.3f);
        Vector normalizedVelocity = boidModel.getCurrentVelocity().normalize();
        Primitives.drawLine(Vector.ZERO, normalizedVelocity);
        GL11.glPopMatrix();
    }
}
