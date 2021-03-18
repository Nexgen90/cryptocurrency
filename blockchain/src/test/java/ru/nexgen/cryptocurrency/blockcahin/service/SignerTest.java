package ru.nexgen.cryptocurrency.blockcahin.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.nexgen.cryptocurrency.blockcahin.configuration.SignerProperties;
import ru.nexgen.cryptocurrency.blockcahin.utils.KeysGenerator;

import java.security.NoSuchAlgorithmException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SignerTest.Config.class})
class SignerTest {
    private byte[] publicKeyBytes;
    private byte[] privateKeyBytes;

    @MockBean
    private SignerProperties signerProperties;

    @Autowired
    private Signer signer;

    @BeforeEach
    public void setUp() throws NoSuchAlgorithmException {
        KeysGenerator genPair = new KeysGenerator(1024).genPair();
        publicKeyBytes = genPair.getPublicKey().getEncoded();
        privateKeyBytes = genPair.getPrivateKey().getEncoded();

        when(signerProperties.getPrivateKeyBytes())
                .thenReturn(privateKeyBytes);
        when(signerProperties.getPublicKeyBytes())
                .thenReturn(publicKeyBytes);
    }

    @Test
    void shouldSignData() {
//        signer.sign();
    }

    @Test
    void shouldReturnPublicKeyBytes() {
        byte[] publicKeyBytes = signer.getPublicKeyBytes();
        assertThat(publicKeyBytes, equalTo(this.publicKeyBytes));
    }

    @Configuration
    static class Config {

        @Bean
        Signer signer(SignerProperties signerProperties) {
            return new Signer(signerProperties);
        }
    }
}