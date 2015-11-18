package gamepad;


import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by solovyevt on 03.11.15.
 */
public class GamepadControl implements Observer {
    static Controller controller;


    public static void run(){
        try {
            Controllers.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
        }

        Controllers.poll();

        for (int i = 0; i < Controllers.getControllerCount(); i++) {
            controller = (Controllers.getController(i));
            System.out.println(controller.getName());
        }
        controller = Controllers.getController(0);
        System.out.println("Axis:");
        for (int i = 0; i < controller.getAxisCount(); i++) {
            System.out.println(i + ".   " + controller.getAxisName(i));
        }
        System.out.println("Buttons:");
        for (int i = 0; i < controller.getButtonCount(); i++) {
            System.out.println(i + ".   " + controller.getButtonName(i));
        }
    }

    public static void main (String[] args){
        run();
    }

    @Override
    public void update(Observable o, Object arg) {

    }

    //Testing
    public void testKeys(){
        float[] currentAxisValues = new float[4];
        while(true){
            controller.poll();
            float[] bufferValues = new float[4];
            bufferValues[0] = controller.getXAxisValue();
            bufferValues[1] = controller.getYAxisValue();
            bufferValues[2] = controller.getZAxisValue();
            bufferValues[3] = controller.getRZAxisValue();
            if(currentAxisValues.hashCode() != bufferValues.hashCode()){
                for(float v: bufferValues){
                    System.out.print(v + "  ");
                }
                System.out.println("\n");
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
