package ru.nexgen.service;

import ru.nexgen.dto.Block;
import ru.nexgen.dto.SignedData;

import java.io.Serializable;
import java.util.List;

public interface Persister<T extends Serializable & SignedData> {
    void save(List<Block<T>> blockchain);
    List<Block<T>> load();
}
