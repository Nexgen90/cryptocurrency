package ru.nexgen.cryptocurrency.blockcahin.configuration;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Validated
@Configuration
@ConfigurationProperties("keys")
public class SignerProperties {
    private String privateKeyPath;
    private String publicKeyFilePath;

    @Getter
    private byte[] publicKeyBytes;
    @Getter
    private byte[] privateKeyBytes;

    @PostConstruct
    public void init() throws IOException {
        this.privateKeyBytes = Files.readAllBytes(new File(privateKeyPath).toPath());
        this.publicKeyBytes = Files.readAllBytes(new File(publicKeyFilePath).toPath());
    }
}
