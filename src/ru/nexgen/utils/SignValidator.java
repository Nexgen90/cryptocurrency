package ru.nexgen.utils;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;

import static ru.nexgen.dto.Signer.ALGORITHM;
import static ru.nexgen.dto.Signer.KEY;


public class SignValidator {
    private final PublicKey publicKey;

    public SignValidator(byte[] publicKeyBytes) throws Exception {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory kf = KeyFactory.getInstance(KEY);
        publicKey = kf.generatePublic(spec);
    }

    public boolean verifySignature(byte[] data, byte[] signature) throws Exception {
        Signature sig = Signature.getInstance(ALGORITHM);
        sig.initVerify(publicKey);
        sig.update(data);
        return sig.verify(signature);
    }
}
