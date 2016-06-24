package org.wenxueliu.concurrent;

import java.util.List;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

/*
 * From: http://ifeve.com/java8-concurrency-tutorial-thread-executor-examples/
 *
 */
public class ExecutorsTest {

    public void test() {
        title("test");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            String threadName = Thread.currentThread().getName();
            System.out.println("Hello " + threadName);
        });

        shutdown(executor);
    }

    public void testCallable() {
        title("testCallable");
        Callable<Integer> task = () -> {
            try {
                TimeUnit.SECONDS.sleep(3);
                return 123;
            }
            catch (InterruptedException e) {
                throw new IllegalStateException("task interrupted", e);
            }
        };

        ExecutorService executor = Executors.newFixedThreadPool(1);
        Future<Integer> future = executor.submit(task);

        while(!future.isDone()) {
            System.out.println("future no done");
            try {
                TimeUnit.SECONDS.sleep(1);
            }
            catch (InterruptedException e) {
                throw new IllegalStateException("task interrupted", e);
            }
        }

        System.out.println("future done? " + future.isDone());

        Integer result = 0;
        try {
            //result = future.get();
            result = future.get(1, TimeUnit.SECONDS);

        } catch (InterruptedException e) {
            throw new IllegalStateException("task interrupted", e);
        } catch (Exception e) {
            throw new IllegalStateException("task interrupted", e);
        }

        System.out.println("result: " + result);

        shutdown(executor);
    }

    public void testInvorkAll() {
        title("testInvorkAll");

        ExecutorService executor = Executors.newWorkStealingPool();

        List<Callable<String>> callables = Arrays.asList(
                () -> "task1",
                () -> "task2",
                () -> "task3");

        try {
            executor.invokeAll(callables)
                .stream()
                .map(future -> {
                    try {
                        return future.get();
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                })
                .forEach(System.out::println);
        } catch (InterruptedException e) {
            System.out.println("InterruptedException occur");
        } finally {
            shutdown(executor);
        }
    }

    private Callable<String> callable(String result, long sleepSeconds) {
        return () -> {
            TimeUnit.SECONDS.sleep(sleepSeconds);
            return result;
        };
    }

    public void testInvorkAny() {
        title("testInvorkAny");

        ExecutorService executor = Executors.newWorkStealingPool();

        List<Callable<String>> callables = Arrays.asList(
        callable("task1", 2),
        callable("task2", 1),
        callable("task3", 3));

        try {
            String result = executor.invokeAny(callables);
            System.out.println(result);
        } catch (InterruptedException e) {
            System.out.println("InterruptedException occur");
        } catch (Exception e) {
            System.out.println("Exception occur");
        }

        // => task2
    }

    public void testSchedule() {
        title("testSchedule");

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);

        Runnable task = () -> System.out.println("Scheduling: " + System.nanoTime());
        ScheduledFuture<?> future = executor.schedule(task, 3, TimeUnit.SECONDS);
        sleep(1);
        long remainingDelay = future.getDelay(TimeUnit.MILLISECONDS);
        System.out.printf("Remaining Delay: %sms", remainingDelay);

        //fixedRateTask
        Runnable fixedRateTask = () -> {
            try {
                TimeUnit.SECONDS.sleep(2);
                System.out.println("Scheduling: " + System.nanoTime());
            }
            catch (InterruptedException e) {
                System.err.println("task interrupted");
            }
        };
        int initialDelay = 0;
        int period = 1;
        executor.scheduleAtFixedRate(fixedRateTask, initialDelay, period, TimeUnit.SECONDS);

        //fixedDelayTask
        Runnable fixedDelayTask = () -> {
            try {
                TimeUnit.SECONDS.sleep(2);
                System.out.println("Scheduling: " + System.nanoTime());
            }
            catch (InterruptedException e) {
                System.err.println("task interrupted");
            }
        };

        executor.scheduleWithFixedDelay(fixedDelayTask, 0, 1, TimeUnit.SECONDS);

        //sleep(10);
        //shutdown(executor);
    }

    static void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new IllegalStateException("task interrupted", e);
        }
    }

    static void shutdown(ExecutorService executor) {
        try {
            System.out.println("attempt to shutdown executor");
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("tasks interrupted");
        } finally {
            if (!executor.isTerminated()) {
                System.err.println("cancel non-finished tasks");
            }
            executor.shutdownNow();
            System.out.println("shutdown finished");
        }
    }

    private void title(String title) {
        System.out.println("-----" + title + "----");
    }
}
