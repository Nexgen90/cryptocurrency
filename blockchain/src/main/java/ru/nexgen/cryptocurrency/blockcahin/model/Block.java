package ru.nexgen.cryptocurrency.blockcahin.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
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
}
