package com.darkempire.lab.parallelprogramming;

import com.darkempire.anji.log.Log;

import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by siredvin on 06.02.15.
 *
 * @author siredvin
 */
public class Lab1 {
    private static class MonoProgram {
        private int N;
        private double[] vector;

        private MonoProgram(int n, double[] vector) {
            N = n;
            this.vector = vector;
        }

        public void run() {
            LocalTime startTime = LocalTime.now();
            double sum = 0;
            for (int i = 0; i < N; i++) {
                sum += vector[i] * vector[i];
            }
            sum = Math.sqrt(sum);
            LocalTime endTime = LocalTime.now();
            endTime = endTime.minusHours(startTime.getHour()).minusMinutes(startTime.getMinute())
                    .minusSeconds(startTime.getSecond()).minusNanos(startTime.getNano());
            Log.log(Log.logIndex, "Отримана сума при звичайній роботі:", sum, "\nЧас роботи:", endTime);
        }
    }

    private static class ParallelProgram {
        private int N;
        private double[] vector;
        private int thread_count;

        private ParallelProgram(int n, double[] vector, int thread_count) {
            N = n;
            this.vector = vector;
            this.thread_count = thread_count;
        }

        public void run() {
            LocalTime startTime = LocalTime.now();
            List<Calc> threadList = new LinkedList<>();
            int maxCount = N / (thread_count);
            int startIndex = 0;
            thread_count--;
            for (int counter = 0; counter < thread_count; counter++) {
                int endIndex = startIndex + maxCount;
                threadList.add(new Calc(startIndex, endIndex, vector));
                startIndex = endIndex;
            }
            threadList.add(new Calc(startIndex, N, vector));
            for (Calc c : threadList) {
                c.start();
            }
            for (Calc c : threadList) {
                try {
                    c.join();
                } catch (InterruptedException e) {
                    Log.err(Log.logIndex, e);
                }
            }
            double sum = threadList.stream().mapToDouble(Calc::getSum).sum();
            sum = Math.sqrt(sum);
            LocalTime endTime = LocalTime.now();
            endTime = endTime.minusHours(startTime.getHour()).minusMinutes(startTime.getMinute())
                    .minusSeconds(startTime.getSecond()).minusNanos(startTime.getNano());
            Log.log(Log.logIndex, "Отримана сума при паралельній роботі:", sum, "\nЧас роботи:", endTime);
        }

        private static class Calc extends Thread {
            private int start;
            private int endIndex;
            private double[] vector;
            private double sum;

            private Calc(int start, int endIndex, double[] vector) {
                this.start = start;
                this.endIndex = endIndex;
                this.vector = vector;
                sum = 0;
            }

            @Override
            public void run() {
                for (int i = start; i < endIndex; i++) {
                    sum += vector[i] * vector[i];
                }
            }

            public double getSum() {
                return sum;
            }
        }
    }

    public static void main(String[] args) {
        int N = 40000000;
        int thread_count = 4;
        double[] vector = new double[N];
        Random random = new Random();
        for (int i = 0; i < N; i++) {
            vector[i] = random.nextDouble();
        }
        MonoProgram program = new MonoProgram(N, vector);
        program.run();
        ParallelProgram parallelProgram = new ParallelProgram(N, vector, thread_count);
        parallelProgram.run();
    }
}
