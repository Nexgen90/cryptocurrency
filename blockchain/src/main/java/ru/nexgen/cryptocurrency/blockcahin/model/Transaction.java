package ru.nexgen.cryptocurrency.blockcahin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

@AllArgsConstructor
public class Transaction implements SignedData, Serializable {
    private long id;
    @Getter
    private String from;
    @Getter
    private String to;
    @Getter
    private BigDecimal value;
    private final byte[] dataSignature;
    private final byte[] publicKey;

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
