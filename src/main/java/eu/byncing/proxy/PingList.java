package eu.byncing.proxy;

import eu.byncing.proxy.json.JsonDocument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class PingList {

    public static final JsonDocument JSON_RESPONSE = new JsonDocument();

    private final Version version;
    private final Players players;

    private String description, favicon;

    public PingList(Version version, Players players, String description, String favicon) {
        this.version = version;
        this.players = players;
        this.description = description;
        this.favicon = favicon;
    }

    public PingList(Version version, Players players, String description) {
        this.version = version;
        this.players = players;
        this.description = description;
    }

    public String toJson() {
        JSON_RESPONSE.append("version", version);
        JSON_RESPONSE.append("players", players);
        JSON_RESPONSE.append("description", description);
       // JSON_RESPONSE.append("favicon", favicon);

        return JSON_RESPONSE.toString();
    }

    static class Version {

        private String name;
        private int protocol;

        public Version(String name, int protocol) {
            this.name = name;
            this.protocol = protocol;
        }

        public String name() {
            return name;
        }

        public void name(String name) {
            this.name = name;
        }

        public int protocol() {
            return protocol;
        }

        public void protocol(int protocol) {
            this.protocol = protocol;
        }
    }

    static class Players {

        private int max, online;

        private final Collection<Sample> sample = new ArrayList<>();

        public Players(int max, int online, Sample... samples) {
            this.max = max;
            this.online = online;
            this.sample.addAll(Arrays.asList(samples));
        }

        public Collection<Sample> sample() {
            return Collections.unmodifiableCollection(sample);
        }

        static class Sample {

            private String name, id;

            public Sample(String name, String id) {
                this.name = name;
                this.id = id;
            }

            public String name() {
                return name;
            }

            public void name(String name) {
                this.name = name;
            }

            public String id() {
                return id;
            }

            public void id(String id) {
                this.id = id;
            }
        }
    }
}