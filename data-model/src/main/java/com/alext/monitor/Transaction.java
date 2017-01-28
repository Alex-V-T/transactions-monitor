package com.alext.monitor;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Transaction {
    private final long id;
    private final TransactionType type;
    private final String timestamp; // type is String to simplify serialization
    private final Map<String, String> fields;

    private Transaction(final Builder builder) {
        id = builder.id;
        type = builder.type;
        timestamp = builder.timestamp;
        fields = builder.fields;
    }

    public long getId() {
        return id;
    }

    public TransactionType getType() {
        return type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public Map<String, String> getFields() {
        if (fields == null)
            return Collections.emptyMap();

        return Collections.unmodifiableMap(fields);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("type", type)
                .append("timestamp", timestamp)
                .append("other fields", fields == null ? 0 : fields.size())
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return  true;
        if (o == null || getClass() != o.getClass())
            return false;

        Transaction that = (Transaction) o;

        if (getId() != that.getId())
            return false;
        if (getType() != that.getType())
            return false;
        if (getTimestamp() != null ? !getTimestamp().equals(that.getTimestamp()) : that.getTimestamp() != null)
            return false;
        return getFields() != null ? getFields().equals(that.getFields()) : that.getFields() == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + (getType() != null ? getType().hashCode() : 0);
        result = 31 * result + (getTimestamp() != null ? getTimestamp().hashCode() : 0);
        result = 31 * result + (getFields() != null ? getFields().hashCode() : 0);
        return result;
    }

    public static final class Builder {
        private long id;
        private TransactionType type;
        private String timestamp;
        private Map<String, String> fields;

        public Builder id(final long id) {
            this.id = id;
            return this;
        }

        public Builder type(final TransactionType type) {
            this.type = type;
            return this;
        }

        public Builder timestamp(final String timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder field(final String key, final String value) {
            if (fields == null)
                fields = new HashMap<>();

            fields.put(key, value);
            return this;
        }

        public Transaction build() {
            return new Transaction(this);
        }
    }
}
