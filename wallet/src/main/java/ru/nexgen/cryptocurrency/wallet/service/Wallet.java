package ru.nexgen.cryptocurrency.wallet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nexgen.cryptocurrency.blockcahin.model.Block;
import ru.nexgen.cryptocurrency.blockcahin.model.SignedData;
import ru.nexgen.cryptocurrency.blockcahin.model.Transaction;
import ru.nexgen.cryptocurrency.blockcahin.service.Blockchain;
import ru.nexgen.cryptocurrency.blockcahin.service.Signer;

import java.io.Serializable;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class Wallet {
    private final Blockchain<Transaction> blockchain;
    private final Signer signer;

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
