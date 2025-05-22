package kebab_simulator.event.services;

import com.google.common.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.*;
import java.util.PriorityQueue;

public class EventProcessingQueue {

    private final Logger logger = LoggerFactory.getLogger(EventProcessingQueue.class);
    private final PriorityQueue<PriorityTask> taskQueue = new PriorityQueue<>();
    private final ListeningExecutorService servicesExecutor = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("service-thread-%s").build()));

    public synchronized void queue(EventProcess task) {
        this.queue(task, 0);
    }

    public synchronized void queue(EventProcess task, int priority) {
        this.taskQueue.offer(new PriorityTask(task, priority));
        processNext();
    }

    public PriorityQueue<PriorityTask> getTaskQueue() {
        return this.taskQueue;
    }

    private synchronized void processNext() {
        if (!this.taskQueue.isEmpty()) {
            PriorityTask nextTask = this.taskQueue.poll();
            var thread = this.servicesExecutor.submit(nextTask.task);
            Futures.addCallback(thread, new FutureCallback() {
                @Override
                public void onSuccess(Object result) {
                    taskComplete(nextTask);
                }
                @Override
                public void onFailure(Throwable t) {
                    logger.error("Error while processing", t);
                }
            }, this.servicesExecutor);
        }
    }

    private synchronized void taskComplete(PriorityTask task) {
        task.setProcessTime();
        this.logger.info("Task \"{}\" finished in {}ms", task.getTask().getName(), task.getProcessingTime());
        this.processNext();
    }

    public void shutdown() {
        this.servicesExecutor.shutdown();
    }

    public ListeningExecutorService getServicesExecutor() {
        return servicesExecutor;
    }

    public static class PriorityTask implements Comparable<PriorityTask> {
        private final EventProcess task;
        private final int priority;
        private final Instant start;
        private long processingTime;

        public PriorityTask(EventProcess task, int priority) {
            this.task = task;
            this.priority = priority;
            this.start = Instant.now();
        }

        public EventProcess getTask() {
            return this.task;
        }

        public int getPriority() {
            return this.priority;
        }

        public Instant getStart() {
            return this.start;
        }

        public long getProcessingTime() {
            return this.processingTime;
        }

        public void setProcessTime() {
            this.processingTime = ChronoUnit.MILLIS.between(this.start, Instant.now());
        }

        @Override
        public int compareTo(PriorityTask o) {
            return Integer.compare(o.priority, this.priority);
        }
    }
}
