package ru.podril1ak2.ponchekanticrash.util;

public final class Rethrow {
    private Rethrow() {
    }

    @SuppressWarnings("unchecked")
    public static <T extends Throwable> void unchecked(Throwable throwable) throws T {
        throw (T) throwable;
    }
}
