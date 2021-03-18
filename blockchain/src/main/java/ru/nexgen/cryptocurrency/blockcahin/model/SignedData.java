package ru.nexgen.cryptocurrency.blockcahin.model;

public interface SignedData {
    long id();
    byte [] raw();
    byte[] dataSignature();
    byte[] publicKey();
}
