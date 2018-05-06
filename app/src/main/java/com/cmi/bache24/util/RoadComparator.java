package com.cmi.bache24.util;

import com.cmi.bache24.data.model.Road;

import java.util.Comparator;

/**
 * Created by omar on 9/9/16.
 */
public class RoadComparator implements Comparator<Road> {
    @Override
    public int compare(Road left, Road right) {
        return Utils.clearText(left.getThoroughfare()).compareToIgnoreCase(Utils.clearText(right.getThoroughfare()));
    }
}
