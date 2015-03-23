package com.darkempire.lab.parallelprogramming;

import com.darkempire.anji.log.Log;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

/**
 * Created by siredvin on 27.02.15.
 *
 * @author siredvin
 */
public class Lab3 {
    private static class MonoProgram {
        private int N;
        private List<Double> vector;

        private MonoProgram(int n, List<Double> vector) {
            N = n;
            this.vector = vector;
        }

        public void run() {
            LocalTime startTime = LocalTime.now();
            long sys_startTime = System.currentTimeMillis();
            double sum = 0;
            for (int i = 0; i < N; i++) {
                sum += vector.get(i) * vector.get(i);
            }
            sum = Math.sqrt(sum);
            LocalTime endTime = LocalTime.now();
            endTime = endTime.minusHours(startTime.getHour()).minusMinutes(startTime.getMinute())
                    .minusSeconds(startTime.getSecond()).minusNanos(startTime.getNano());
            long sys_endTime = System.currentTimeMillis();
            Log.log(Log.logIndex, "Отримана сума при звичайній роботі:", sum, "\nЧас роботи:", endTime,"\n",sys_endTime-sys_startTime);
        }
    }

    private static class ParallelProgram {
        private List<Double> vector;

        private ParallelProgram(List<Double> vector) {
            this.vector = vector;
        }

        public void run() {
            LocalTime startTime = LocalTime.now();
            long sys_startTime = System.currentTimeMillis();
            double sum = vector.parallelStream().mapToDouble(x->x*x).sum();
            sum = Math.sqrt(sum);
            LocalTime endTime = LocalTime.now();
            endTime = endTime.minusHours(startTime.getHour()).minusMinutes(startTime.getMinute())
                    .minusSeconds(startTime.getSecond()).minusNanos(startTime.getNano());
            long sys_endTime = System.currentTimeMillis();
            Log.log(Log.logIndex, "Отримана сума при паралельній роботі:", sum, "\nЧас роботи:", endTime,"\n",sys_endTime-sys_startTime);
        }
    }

    public static void main(String[] args) {
        ForkJoinPool commonPool = ForkJoinPool.commonPool();
        System.out.println(commonPool.getParallelism());
        int N = 40_000_000;
        List<Double> vector = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < N; i++) {
            vector.add(random.nextDouble());
        }
        MonoProgram program = new MonoProgram(N, vector);
        program.run();
        ParallelProgram parallelProgram = new ParallelProgram(vector);
        parallelProgram.run();
    }
}
