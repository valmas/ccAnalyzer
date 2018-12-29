package com.ccAnalyzer.controller;

import java.util.Random;

public class AlgoController {

    public static double randomwalk(double currentPrice) {
        Random ran = new Random();
        double u = ran.nextGaussian();
        return currentPrice + u;
    }

    public static void runAlgo() {

    }
}
