package edu.neu.coe.info6205.sort.par;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * This code has been fleshed out by Ziyao Qiao. Thanks very much.
 * TODO tidy it up a bit.
 */
public class Main {
//keep levels consts and vary cutoffs
    public static void runwithDifferentCutoffs(String fileName) {
        ForkJoinPool pool = new ForkJoinPool(1048);
        Map<Integer, List<Long>> readings = new HashMap<>();
        System.out.println("Degree of parallelism: " + ForkJoinPool.getCommonPoolParallelism());
        Random random = new Random();
        for (int i = 1; i < 20; ++i)
        {
            int size = 500 * i;
            int[] array = new int[size];
            List<Long> timeList = new ArrayList<>();
            long time=0;
            ParSort.maxlevels=1;
            for(int k=0;k<5;k++){
                long startTime = System.currentTimeMillis();
                for (int j = 0; j < array.length; j++) array[i] = random.nextInt(10000000);
                ParSort.sort(array, 0, array.length,pool,0);
                long endTime = System.currentTimeMillis();
                time = time+(endTime - startTime);
                System.out.printf("Array size: %d, recursion depth: %d, elapsed time: %d ms \n",
                        size, ParSort.maxlevels, endTime - startTime);
            }
            time=time/5;
            timeList.add(time);
            /*
            long startTime = System.currentTimeMillis();
            for (int j = 0; j < array.length; j++) array[i] = random.nextInt(10000000);
            ParSort.sort(array, 0, array.length,pool,0);
            long endTime = System.currentTimeMillis();
            timeList.add(endTime - startTime);
            System.out.printf("Array size: %d, recursion depth: %d, elapsed time: %d ms \n",
                    size, ParSort.maxlevels, endTime - startTime);
            */

            time=0;
            ParSort.maxlevels=1;
            for(int k=0;k<5;k++){
                long startTime = System.currentTimeMillis();
                for (int j = 0; j < array.length; j++) array[i] = random.nextInt(10000000);
                ParSort.sort(array, 0, array.length,pool,0);
                long endTime = System.currentTimeMillis();
                time = time+(endTime - startTime);
                System.out.printf("Array size: %d, recursion depth: %d, elapsed time: %d ms \n",
                        size, ParSort.maxlevels, endTime - startTime);
            }
            time=time/5;
            timeList.add(time);

            readings.put(size, timeList);
        }

        writeToFile(readings, "./src/cutoff.csv", null);

        //ParSort.pool.shutdown();
    }
//keep cutoff const and vary threads
    private static void runWithDifferentThreads(String fileName) {
        ForkJoinPool pool = new ForkJoinPool(1048);
        Map<Integer, List<Long>> readings = new HashMap<>();
        System.out.println("Degree of parallelism: " + ForkJoinPool.getCommonPoolParallelism());
        Random random = new Random();
        int size=50000;

        //ParSort.cutoff = 1000;
        for (int j = 0; j < 7; j++) {
            int[] array = new int[size];
            ArrayList<Long> timeList = new ArrayList<>();
            readings.put(size,timeList);
            // for (int i = 0; i < array.length; i++) array[i] = random.nextInt(10000000);
            //ParSort.pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(j);
            long time=0;

            for (int t = 1; t < 10; t++) {
                for(int k=0;k<5;k++){
                    long startTime = System.currentTimeMillis();
                    ParSort.maxlevels=t;
                    for (int i = 0; i < array.length; i++) array[i] = random.nextInt(10000000);
                    ParSort.sort(array, 0, array.length,pool,0);
                    long endTime = System.currentTimeMillis();
                    time = time+(endTime - startTime);
                }
                //taking averages here
                time=time/5;
                readings.get(size).add(time);
                System.out.printf("Array size: %d, recursion depth: %d, elapsed time: %d ms \n",
                        size, ParSort.maxlevels, time);
            }

           size=size*2;
        }

        writeToFile(readings, fileName, null);
    }



    public static void main(String[] args) {
        processArgs(args);



        //runwithDifferentCutoffs("./src/cutoff.csv");
        runWithDifferentThreads("./src/threads.csv");



    }

    private static void processArgs(String[] args) {
        String[] xs = args;
        while (xs.length > 0)
            if (xs[0].startsWith("-")) xs = processArg(xs);
    }

    private static String[] processArg(String[] xs) {
        String[] result = new String[0];
        System.arraycopy(xs, 2, result, 0, xs.length - 2);
        processCommand(xs[0], xs[1]);
        return result;
    }

    public static void writeToFile(Map<Integer, List<Long>> result, String filepath, String header)
    {
        try {
            FileOutputStream f = new FileOutputStream(filepath);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(f));
            if (header != null)
            {
                bw.write(header);
                bw.newLine();
            }
            for (int arrSize : result.keySet()) {
                StringBuilder content = new StringBuilder(Integer.toString(arrSize));
                for (long elapsed : result.get(arrSize)) {
                    content.append(",").append(elapsed);
                }
                bw.write(content.toString());
                bw.newLine();
            }
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processCommand(String x, String y) {
        if (x.equalsIgnoreCase("N")) setConfig(x, Integer.parseInt(y));
        else
            // TODO sort this out
            if (x.equalsIgnoreCase("P")) //noinspection ResultOfMethodCallIgnored
                ForkJoinPool.getCommonPoolParallelism();
    }

    private static void setConfig(String x, int i) {
        configuration.put(x, i);
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static final Map<String, Integer> configuration = new HashMap<>();


}
