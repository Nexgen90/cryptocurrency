package ru.nexgen.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Transaction implements SignedData, Serializable {
    private final byte[] dataSignature;
    private final byte[] publicKey;
    private long id;
    private String from;
    private String to;
    private BigDecimal value;

    public Transaction(long id, String from, String to, BigDecimal value, byte[] dataSignature, byte[] publicKey) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.value = value;
        this.dataSignature = dataSignature;
        this.publicKey = publicKey;
    }

    @Override
    public long id() {
        return id;
    }

    @Override
    public byte[] raw() {
        return (from + to + value + id).getBytes(UTF_8);
    }

    @Override
    public byte[] dataSignature() {
        return dataSignature;
    }

    @Override
    public byte[] publicKey() {
        return publicKey;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction)) return false;
        Transaction that = (Transaction) o;
        return id == that.id && from.equals(that.from) && to.equals(that.to) && value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, from, to, value);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", value=" + value +
                '}';
    }
}
