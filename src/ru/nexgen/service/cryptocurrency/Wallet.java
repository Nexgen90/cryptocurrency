package ru.nexgen.service.cryptocurrency;

import ru.nexgen.dto.Block;
import ru.nexgen.dto.SignedData;
import ru.nexgen.dto.Signer;
import ru.nexgen.dto.Transaction;
import ru.nexgen.service.Blockchain;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.security.GeneralSecurityException;

public class Wallet {
    private final Blockchain<Transaction> blockchain;
    private final Signer signer;

    public Wallet(Blockchain<Transaction> blockchain, String privateKeyPath, String publicKeyFilePath) throws IOException, GeneralSecurityException {
        this.blockchain = blockchain;
        this.signer = new Signer(privateKeyPath, publicKeyFilePath);
    }

    public void sendTransaction(String from, String to, BigDecimal value) throws Exception {
        long id = blockchain.getUnicTransactionId();
        Transaction transaction = new Transaction(id, from, to, value,
                signer.sign(from + to + value + id), signer.getPublicKeyBytes());
        blockchain.sendTransaction(transaction);
    }

    public <T extends Serializable & SignedData> void addMinerBenefit(String to, Block<T> block) throws Exception {
        String from = blockchain.getSystemName();
        BigDecimal value = blockchain.getMinerBenefit();
        long transactionId = blockchain.getUnicTransactionId();
        block.getData().add((T) new Transaction(transactionId, from, to, value,
                signer.sign(from + to + value + transactionId), signer.getPublicKeyBytes()));
    }
}
