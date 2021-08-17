package ru.nexgen.dto;

import java.io.Serializable;
import java.util.List;

public class Block<T extends Serializable> implements Serializable {
    private final String hashPreviousBlock;
    private Integer id;
    private String miner;
    private String hash;
    private Long timestamp;
    private Integer magicNumber;
    private List<T> data;

    public Block(Integer magicNumber, String hashPreviousBlock, Long timestamp) {
        this.magicNumber = magicNumber;
        this.hashPreviousBlock = hashPreviousBlock;
        this.timestamp = timestamp;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getHashPreviousBlock() {
        return hashPreviousBlock;
    }

    public Integer getMagicNumber() {
        return magicNumber;
    }

    public void setMagicNumber(Integer magicNumber) {
        this.magicNumber = magicNumber;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMiner() {
        return miner;
    }

    public void setMiner(String miner) {
        this.miner = miner;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
