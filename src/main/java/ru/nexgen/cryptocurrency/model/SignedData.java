package ru.nexgen.cryptocurrency.model;

public interface SignedData {
    long id();

    byte [] raw();

    byte[] dataSignature();

    byte[] publicKey();
}
