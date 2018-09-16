package org.wenxueliu.scheduler;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.CancellationException;

public class ConcurrentTask<V> {
    private final ExecutorService executor;
    private final Collection<Callable<V>> tasks;
    private final Collection<V> results;

    public ConcurrentTask(ExecutorService executor, Collection<Callable<V>> tasks) {
        this.executor = executor;
        this.tasks = tasks;
        this.results = new ArrayList<V>();
    }

    public Collection<V> invokeAll(long timeOut, TimeUnit unit) {
        try {
            List<Future<V>> futures = executor.invokeAll(tasks, timeOut, unit);
            for (Future<V> f : futures) {
                try {
                    this.results.add(f.get());
                } catch (ExecutionException e) {
                    //TODO
                } catch (CancellationException e) {
                    //TODO
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return this.results;
    }

    public Collection<V> run(long timeOut, TimeUnit unit) {
        CompletionService<V> ecs =
            new ExecutorCompletionService<V>(executor);
        for (final Callable<V> task: tasks) {
            ecs.submit(task);
        }

        try {
            for (int t = 0, n = tasks.size(); t < n; t++) {
                V ret;
                Future<V> future = ecs.take();
                try {
                    if (timeOut <= 0) {
                        ret = future.get();
                    } else {
                        ret = future.get(timeOut, unit);
                    }
                } catch (TimeoutException e) {
                    future.cancel(true);
                    continue;
                }
                if (ret != null) {
                    this.results.add(ret);
                }
            }
            return this.results;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw LaunderThrowable.launderThrowable(e.getCause());
        }
        return this.results;
    }

    public Collection<V> run() {
        run(0, TimeUnit.MILLISECONDS);
        return null;
    }

    public abstract class DownloadImagesTask {

        private Collection<String> urls;
        private Collection<Callable<ImageData>> tasks;

        public DownloadImagesTask(Collection<String> urls) {
            this.urls = urls;
        }

        private ImageData downloadImage(String url) {
            //TODO
            return null;
        }

        private void downloadImages() {
            for (String url : urls) {
                Callable<ImageData> downloadImage = new Callable<ImageData>() {
                    public ImageData call() {
                        return downloadImage(url);
                    }
                };
                tasks.add(downloadImage);
            }
        }

        abstract void storeText(CharSequence s);

        abstract List<ImageInfo> getAllImageInfo(CharSequence s);

        abstract void storeImage(ImageData i);

        void run() {
            downloadImages();
            ExecutorService ec = Executors.newCachedThreadPool();
            ConcurrentTask<ImageData> concurrentTasks = new ConcurrentTask<ImageData>(ec, tasks);
            concurrentTasks.run();
        }
    }

    //void downloadPage(CharSequence source) {
    //    final List<ImageInfo> infos = getAllImageInfo(source);
    //    CompletionService<ImageData> completionService =
    //            new ExecutorCompletionService<ImageData>(executor);
    //    for (final ImageInfo imageInfo : infos)
    //        completionService.submit(new Callable<ImageData>() {
    //            public ImageData call() {
    //                return imageInfo.downloadImage();
    //            }
    //        });

    //    storeText(source);

    //    try {
    //        for (int t = 0, n = infos.size(); t < n; t++) {
    //            Future<ImageData> f = completionService.take();
    //            ImageData imageData = f.get();
    //            storeImage(imageData);
    //        }
    //    } catch (InterruptedException e) {
    //        Thread.currentThread().interrupt();
    //    } catch (ExecutionException e) {
    //        throw LaunderThrowable.launderThrowable(e.getCause());
    //    } finally {
    //    }
    //}

    interface ImageData {
    }

    interface ImageInfo {
        ImageData downloadImage();
    }

}
