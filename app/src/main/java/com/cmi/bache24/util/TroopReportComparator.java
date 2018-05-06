package com.cmi.bache24.util;

import com.cmi.bache24.data.model.Report;

import java.util.Comparator;

/**
 * Created by omar on 3/10/16.
 */
public class TroopReportComparator implements Comparator<Report> {

    @Override
    public int compare(Report left, Report right) {
        return left.getCreationDate().compareTo(right.getCreationDate());
    }
}
