package org.zalando.nakadi.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nullable;

public class StreamMetadata {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StreamMetadata that = (StreamMetadata) o;

        return debug != null ? debug.equals(that.debug) : that.debug == null;

    }

    @Override
    public int hashCode() {
        return debug != null ? debug.hashCode() : 0;
    }

    public StreamMetadata(@Nullable @JsonProperty("debug") String debug) {
        this.debug = debug;
    }

    public String getDebug() {
        return debug;
    }

    public void setDebug(String debug) {
        this.debug = debug;
    }

    private String debug;

    @Override
    public String toString() {
        return "debug=" + debug;
    }
}
