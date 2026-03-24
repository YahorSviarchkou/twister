package com.twister.persistence.service;

public final class AuditorProvider {

    private static final ThreadLocal<String> AUDITOR = new ThreadLocal<>();

    public static void setAuditor(String auditor) {
        AUDITOR.set(auditor);
    }

    public static String getAuditor() {
        return AUDITOR.get();
    }

    public static void clear() {
        AUDITOR.remove();
    }
}
