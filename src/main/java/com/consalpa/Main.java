package com.consalpa;

import com.consalpa.Workers.SiteRoamer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main {
    public static void main(String[] args) {
        ExecutorService exec = Executors.newCachedThreadPool();
        exec.execute(new SiteRoamer());

    }
}
