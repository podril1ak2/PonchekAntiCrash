package ru.podril1ak2.ponchekanticrash.util;

public final class RateWindow {
    private long windowStart = System.nanoTime();
    private int count;

    public boolean exceeds(int limit, long windowNanos) {
        long now = System.nanoTime();
        if (now - windowStart >= windowNanos) {
            windowStart = now;
            count = 0;
        }
        return ++count > limit;
    }
}
