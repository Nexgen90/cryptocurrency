package ru.nexgen.dto;

public interface SignedData {
    long id();

    byte [] raw();

    byte[] dataSignature();

    byte[] publicKey();
}
