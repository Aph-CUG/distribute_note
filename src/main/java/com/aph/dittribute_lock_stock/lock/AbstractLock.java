package com.aph.dittribute_lock_stock.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public abstract class AbstractLock implements Lock {

    protected String lockName;
    @Override
    public void lock() {
    }

    public void lock(TimeUnit timeUnit, long expireTime) {

    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    public abstract void lock(TimeUnit timeUnit, Long expireTime);

    @Override
    public void unlock() {

    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
