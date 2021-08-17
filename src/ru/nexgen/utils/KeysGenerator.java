package ru.nexgen.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class KeysGenerator {
    private static final String PUBLIC_KEY_PATH = "/Users/nick/git/Blockchain1/Blockchain/task/keys/test.pub";
    private static final String PRIVATE_KEY_PATH = "/Users/nick/git/Blockchain1/Blockchain/task/keys/test";

    private final KeyPairGenerator keyGen;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    public static void main(String[] args) throws Exception {
        KeysGenerator gen = new KeysGenerator(1024)
                .genPair();
        writeToFile(PUBLIC_KEY_PATH, gen.getPublicKey().getEncoded());
        writeToFile(PRIVATE_KEY_PATH, gen.getPrivateKey().getEncoded());
    }

    private KeysGenerator(int keyLen) throws NoSuchAlgorithmException {
        this.keyGen = KeyPairGenerator.getInstance("RSA");
        this.keyGen.initialize(keyLen);
    }


    private KeysGenerator genPair() {
        KeyPair pair = keyGen.generateKeyPair();
        privateKey = pair.getPrivate();
        publicKey = pair.getPublic();
        return this;
    }

    private PrivateKey getPrivateKey() {
        return privateKey;
    }

    private PublicKey getPublicKey() {
        return publicKey;
    }

    private static void writeToFile(String path, byte[] key) throws IOException {
        Path keyPath = Paths.get(path);
        Files.createDirectories(keyPath.getParent());
        try (OutputStream fos = Files.newOutputStream(keyPath)) {
            fos.write(key);
        }
    }
}
