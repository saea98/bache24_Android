package com.cmi.bache24.util;

import com.cmi.bache24.data.model.Report;

import java.util.Comparator;

/**
 * Created by omar on 2/3/16.
 */
public class ReportComparator implements Comparator<Report> {

    @Override
    public int compare(Report left, Report right) {
        return right.getTicket().compareToIgnoreCase(left.getTicket());
    }
}
