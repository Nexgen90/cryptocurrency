package ru.nexgen.dto;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;

public class Signer {
    public static final String KEY = "RSA";
    public static final String ALGORITHM = "SHA256withRSA";
    private final byte[] publicKeyBytes;
    private final PrivateKey privateKey;

    public Signer(String privateKeyPath, String publicKeyFilePath) throws IOException, GeneralSecurityException {
        byte[] privateKeyBytes = Files.readAllBytes(new File(privateKeyPath).toPath());
        this.publicKeyBytes = Files.readAllBytes(new File(publicKeyFilePath).toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory kf = KeyFactory.getInstance(KEY);
        this.privateKey = kf.generatePrivate(spec);
    }

    public byte[] sign(String text) throws Exception {
        Signature rsa = Signature.getInstance(ALGORITHM);
        rsa.initSign(privateKey);
        rsa.update(text.getBytes());
        return rsa.sign();
    }

    public byte[] getPublicKeyBytes() {
        return publicKeyBytes;
    }
}
