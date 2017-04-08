package com.quadcopter.utils;

import com.google.common.collect.EvictingQueue;

import java.util.*;

/**
 * Created by solovyevt on 26.03.17.
 */
public class SlidingAverage {
    private EvictingQueue<Vector> history;

    public SlidingAverage(int capacity) {
        history = EvictingQueue.create(capacity);
    }

    private void addPoint(Vector point) {
        history.add(point);
    }

    private Vector getAverage(){
        Vector result = new Vector(0.0f, 0.0f, 0.0f);
        for(Vector v: history){
            result = Vector.add(v);
        }
        return Vector.div(result, history.stream().filter(Objects::nonNull).count());
    }

    public Vector getNewValue(Vector point) {
        addPoint(point);
        return getAverage();
    }
}
