package com.darkempire.lab.parallelprogramming;

import com.darkempire.anji.log.Log;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by siredvin on 11.02.15.
 *
 * @author siredvin
 */
public class Lab2 {
    private static final int main_delay = 500;
    private static final int additional_delay = 100;
    private static class CPU extends Thread{
        private AtomicLong finishedTaskCount;
        private volatile Runnable task;
        private boolean isWork;

        public CPU() {
            finishedTaskCount = new AtomicLong(0);
            isWork = true;
        }

        public boolean isFree(){
            return task==null;
        }

        public void setWork(boolean value){
            this.isWork = value;
        }
        public void setTask(Runnable runnable){
            task = runnable;
        }

        public void makeSome(Runnable runnable){
            runnable.run();
            finishedTaskCount.incrementAndGet();
            task = null;
        }

        public long getFinishedTaskCount(){
            return finishedTaskCount.get();
        }

        @Override
        public void run(){
            while (isWork){
                if (task!=null){
                    makeSome(task);
                }
                try {
                    Thread.sleep(main_delay);
                } catch (InterruptedException e) {
                    Log.err(Log.logIndex, e);
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        CPU cpu1 = new CPU(),cpu2 = new CPU(),cpu3 = new CPU(),cpu4 = new CPU();
        cpu1.start();
        cpu2.start();
        cpu3.start();
        cpu4.start();
        Random rand = new Random();
        ConcurrentLinkedQueue<String> list = new ConcurrentLinkedQueue<>();
        long all_count = 0;
        for (int i=0;i<50;i++){
            Runnable task = ()-> {
                double step = rand.nextDouble()+0.1;
                double finish = rand.nextDouble()*100;
                double sum = 0;
                for (double a=0;a<finish;a+=step){
                    sum += a;
                }
                list.add("Отримана сумма "+ sum+"; Обчисляли сумму до "+finish+", з кроком "+step);
            };
            int run_count =0;
            if (cpu1.isFree()){
                cpu1.setTask(task);
                run_count=0;
                all_count++;
            } else {
                if (cpu2.isFree()){
                    cpu2.setTask(task);
                    run_count=1;
                    all_count++;
                } else {
                    if (cpu3.isFree()){
                        cpu3.setTask(task);
                        run_count=2;
                        all_count++;
                    } else {
                        if (cpu4.isFree()){
                            cpu4.setTask(task);
                            run_count=3;
                            all_count++;
                        } else {
                            Log.err(Log.logIndex,"Немає вільних процесорів");
                            run_count = 4;
                        }
                    }
                }
            }
            while (!list.isEmpty()){
                Log.log(Log.logIndex,list.poll());
            }
            int delay = rand.nextInt(main_delay) + additional_delay;
            Log.log(Log.logIndex,"Кількість зайнятих CPU:",run_count);
            Log.log(Log.logIndex,"Затримка на створення нового потоку:",delay);
            Thread.sleep(delay);
        }
        cpu1.setWork(false);
        cpu2.setWork(false);
        cpu3.setWork(false);
        cpu4.setWork(false);
        double all_count_cast = all_count;
        double cpu1_percent = cpu1.getFinishedTaskCount()/all_count_cast;
        double cpu2_percent = cpu2.getFinishedTaskCount()/all_count_cast;
        double cpu3_percent = cpu3.getFinishedTaskCount()/all_count_cast;
        double cpu4_percent = cpu4.getFinishedTaskCount()/all_count_cast;
        Log.logf(Log.logIndex, "Відсоток оброблених процесів кожним потоком:\nCPU1:%f\nCPU2:%f\nCPU3:%f\nCPU4:%f", cpu1_percent, cpu2_percent,
                cpu3_percent, cpu4_percent);
    }
}
