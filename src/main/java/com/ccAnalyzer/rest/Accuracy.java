package com.ccAnalyzer.rest;

public enum Accuracy {

    DAYS(0, "histoday"),
    HOURS(1, "histohour"),
    MINUTES(2, "histominute");

    int code;
    String path;

    Accuracy(int code, String path) {
        this.code = code;
        this.path = path;
    }
}
