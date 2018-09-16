package org.wenxueliu.scheduler;

public interface Computable<A, V> {

    public V compute(final A arg) throws InterruptedException;
}
