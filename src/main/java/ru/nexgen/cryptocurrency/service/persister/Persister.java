package ru.nexgen.cryptocurrency.service.persister;

import ru.nexgen.cryptocurrency.model.Block;
import ru.nexgen.cryptocurrency.model.SignedData;

import java.io.Serializable;
import java.util.List;

public interface Persister<T extends Serializable & SignedData> {
    void save(List<Block<T>> blockchain);
    List<Block<T>> load();
}
