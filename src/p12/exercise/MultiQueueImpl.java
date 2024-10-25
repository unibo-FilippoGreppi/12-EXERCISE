package p12.exercise;

import java.util.*;

public class MultiQueueImpl<T, Q> implements MultiQueue<T, Q> {
    private final Map<Q, Queue<T>> queues = new HashMap<>();

    @Override
    public Set<Q> availableQueues() {
        return this.queues.keySet();
    }

    @Override
    public void openNewQueue(Q queue) {
        if (this.availableQueues().contains(queue)) {
            throw new IllegalArgumentException("Queue" + queue + "already exists");
        }

        this.queues.putIfAbsent(queue, new PriorityQueue<>());
    }

    @Override
    public boolean isQueueEmpty(Q queue) {
        if (!this.availableQueues().contains(queue)) {
            throw new IllegalArgumentException("Queue" + queue + "not available");
        }

        return (this.queues.get(queue)).isEmpty();
    }

    @Override
    public void enqueue(T elem, Q queue) {
        if (!this.availableQueues().contains(queue)) {
            throw new IllegalArgumentException("Queue" + queue + "not available");
        }

        (this.queues.get(queue)).add(elem);
    }

    @Override
    public T dequeue(Q queue) {
        if (!this.availableQueues().contains(queue)) {
            throw new IllegalArgumentException("Queue" + queue + "not available");
        }

        return (this.queues.get(queue)).poll();
    }

    @Override
    public Map<Q, T> dequeueOneFromAllQueues() {
        final Map<Q, T> dequeuedMap = new HashMap<>();

        for (var queue : this.availableQueues()) {
            dequeuedMap.put(queue, this.dequeue(queue));
        }

        return dequeuedMap;
    }
    
    @Override
    public Set<T> allEnqueuedElements() {   
        Set<T> set = new HashSet<>();

        for (var queue : this.availableQueues()) {
            set.addAll(queues.get(queue));
        }
        return set;
    }

    @Override
    public List<T> dequeueAllFromQueue(Q queue) {
        if (!this.availableQueues().contains(queue)) {
            throw new IllegalArgumentException("Queue" + queue + "not available");
        }

        List<T> list = new LinkedList<>();

        while (!this.isQueueEmpty(queue)) {
            list.add(this.dequeue(queue));
        }

        return list;
    }

    @Override
    public void closeQueueAndReallocate(Q queue) {
        if (!this.availableQueues().contains(queue)) {
            throw new IllegalArgumentException("Queue" + queue + "not available");
        }

        if (this.availableQueues().size() <= 1) {
            throw new IllegalStateException("No alternative queue for moving elements to");
        }
        
        var lessClientsQueue = this.queues.get(queue);

        /* Look for queue with less clients */
        for (var iterableQueue : this.availableQueues()) {
            if (iterableQueue != queue && this.queues.get(iterableQueue).size() < lessClientsQueue.size()) {
                lessClientsQueue = this.queues.get(iterableQueue);
            } 
        }

        lessClientsQueue.addAll(dequeueAllFromQueue(queue));

        this.queues.remove(queue);
    }
}
