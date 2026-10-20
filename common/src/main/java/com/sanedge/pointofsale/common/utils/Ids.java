package com.sanedge.pointofsale.common.utils;

/**
 * Util konversi id. Model MongoDB memakai id String numerik, sedangkan lapisan
 * response/gRPC masih memakai Long/int32 — jembatannya ada di sini.
 */
public final class Ids {

    private Ids() {
    }

    public static Long toLong(String id) {
        if (id == null) {
            return null;
        }
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
