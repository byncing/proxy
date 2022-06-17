package eu.byncing.proxy.protocol;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ProtocolSupport {

    public static final int MINECRAFT_1_8 = 47;

    public static final Map<String, Protocol> PROTOCOLS = new HashMap<>();

    public static boolean isSupported(int version) {
        for (Protocol value : PROTOCOLS.values()) return value.isSupported(version);
        return false;
    }

    static class Protocol {

        private final int[] versions;

        public Protocol(int... versions) {
            this.versions = versions;
        }

        public boolean isSupported(int version) {
            return Arrays.stream(versions).anyMatch(value -> value == version);
        }
    }
}