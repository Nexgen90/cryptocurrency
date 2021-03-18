package ru.nexgen.cryptocurrency.blockcahin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.nexgen.cryptocurrency.blockcahin.configuration.SignerProperties;

import javax.annotation.PostConstruct;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;

@Slf4j
@Component
@RequiredArgsConstructor
public class Signer {
    public static final String KEY = "RSA";
    public static final String ALGORITHM = "SHA256withRSA";
    private final SignerProperties properties;
    private PrivateKey privateKey;

    @PostConstruct
    public void init() throws GeneralSecurityException {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(properties.getPrivateKeyBytes());
        KeyFactory kf = KeyFactory.getInstance(KEY);
        this.privateKey = kf.generatePrivate(spec);
    }

    public byte[] sign(String text) {
        byte[] sign;
        try {
            Signature rsa = Signature.getInstance(ALGORITHM);
            rsa.initSign(privateKey);
            rsa.update(text.getBytes());
            sign = rsa.sign();
        } catch (Exception e) {
            log.error("Can't sign data: ", e);
            sign = new byte[0];
        }
        return sign;
    }

    public byte[] getPublicKeyBytes() {
        return properties.getPublicKeyBytes();
    }
}
