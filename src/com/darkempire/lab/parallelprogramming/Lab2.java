package com.darkempire.lab.parallelprogramming;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by siredvin on 11.02.15.
 *
 * @author siredvin
 */
public class Lab2 {
    private static final int sleepTime = 500;
    private static final int operationCount = 50;
    private static final CPU cpu1 = new CPU();
    private static final CPU cpu2 = new CPU();
    private static final CPUQueue cpuQueue = new CPUQueue();
    private static volatile boolean isFinished;
    private static final Random rand = new Random();

    public static void main(String[] args) {
        CPUProcessFirst first = new CPUProcessFirst();
        CPUProcessSecond second = new CPUProcessSecond();
        cpu1.start();
        cpu2.start();
        first.start();
        second.start();
        try {
            first.join();
            second.join();
            cpu1.join();
            cpu2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();//TODO:обробити
        }
        System.out.println("Кількість згенерований потоків");
        System.out.println("Перший генератор:"+first.generatedCount);
        System.out.println("Другий генератор:"+second.generatedCount);
        System.out.println("Кількість знищених потоків:");
        System.out.println("Перший потік:" + first.failedCount);
        System.out.println("Другий потік(залишок черги):" + cpuQueue.queue.size());
        System.out.println("Відсоток знищених потоків:"+ Double.valueOf(first.failedCount/(double)first.generatedCount));
        System.out.println("Максимальний розмір черги:" + cpuQueue.max_len);
    }

    public static Runnable generate(){
        return () -> {
            System.out.println(Math.sin(Math.random()));
            try {
                Thread.sleep(rand.nextInt(sleepTime));
            } catch (InterruptedException e) {
                e.printStackTrace();//TODO:обробити
            }
        };
    }

    private static class CPUProcessFirst extends Thread{
        public int generatedCount;
        public int failedCount;

        public CPUProcessFirst() {
            generatedCount = failedCount = 0;
        }

        @Override
        public void run() {
            while (!isFinished) {
                Runnable task = generate();
                generatedCount++;
                System.out.println("1:"+generatedCount);
                if (!cpu1.isBusy()) {
                    cpu1.setTask(task);
                } else {
                    if (!cpu2.isBusy()) {
                        cpu2.setTask(task);
                    } else {
                        failedCount++;
                    }
                }
                try {
                    Thread.sleep(rand.nextInt(sleepTime));
                } catch (InterruptedException e) {
                    e.printStackTrace();//TODO:обробити
                }
            }
        }
    }

    private static class CPUProcessSecond extends Thread{
        public int generatedCount;
        public int failedCount;

        public CPUProcessSecond() {
            generatedCount = failedCount = 0;
        }

        @Override
        public void run() {
            for (int i=0;i<operationCount;i++){
                Runnable task = generate();
                generatedCount++;
                System.out.println("2:"+generatedCount);
                if (!cpu2.isBusy()) {
                    cpu2.setTask(task);
                } else {
                    cpuQueue.put(task);
                }
                try {
                    Thread.sleep(rand.nextInt(sleepTime));
                } catch (InterruptedException e) {
                    e.printStackTrace();//TODO:обробити
                }
            }
            isFinished = true;
        }
    }

    private static class CPUQueue{
        private ConcurrentLinkedQueue<Runnable> queue;
        public int max_len;

        public CPUQueue() {
            this.queue = new ConcurrentLinkedQueue<>();
            max_len = 0;
        }

        public void put(Runnable run){
                queue.add(run);
                int len = queue.size();
                if (len>max_len){
                    max_len=len;
                }
        }

        public boolean isPresent(){
            return !queue.isEmpty();
        }

        public Runnable get(){
            return queue.poll();
        }
    }
    private static class CPU extends Thread{
        private Runnable task = null;


        public boolean isBusy(){
            return task!=null;
        }

        public synchronized void setTask(Runnable task ){
            if (this.task==null){
                this.task = task;
            }
        }

        @Override
        public void run() {
            while (!isFinished){
                if (task!=null){
                    task.run();
                    task = null;
                } else {
                    if (cpuQueue.isPresent()){
                        task = cpuQueue.get();
                        task.run();
                        task = null;
                    } else {
                        try {
                            Thread.sleep(rand.nextInt(sleepTime));
                        } catch (InterruptedException e) {
                            e.printStackTrace();//TODO:обробити
                        }
                    }
                }

            }
        }
    }
}
