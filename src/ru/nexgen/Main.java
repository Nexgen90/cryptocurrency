package ru.nexgen;

import ru.nexgen.dto.Transaction;
import ru.nexgen.service.Blockchain;
import ru.nexgen.service.FilePersister;
import ru.nexgen.service.Persister;
import ru.nexgen.service.cryptocurrency.Wallet;
import ru.nexgen.service.miner.Miner;
import ru.nexgen.utils.DataFormatter;
import ru.nexgen.utils.TransactionFormater;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    private static final String OUTPUT_FILE_NAME = "blockchain.data";
    private static final String minderPublicKeyPath = "/Users/nick/git/myBlockchain/keys/publicKey";
    private static final String minerPrivateKeyPath = "/Users/nick/git/myBlockchain/keys/privateKey";
    private static final String publicKeyPath = "/Users/nick/git/myBlockchain/keys/test.pub";
    private static final String privateKeyPath = "/Users/nick/git/myBlockchain/keys/test";
    private static final int NUMBER_OF_MINERS = 20;
    private static final AtomicInteger minersIdGenerator = new AtomicInteger(1);


    public static void main(String[] args) throws Exception {

        Persister<Transaction> persister = new FilePersister<>(OUTPUT_FILE_NAME);
        DataFormatter<Transaction> formatter = new TransactionFormater();
        Blockchain<Transaction> blockchain = new Blockchain<>(persister, formatter);
        Wallet userWallet = new Wallet(blockchain, privateKeyPath, publicKeyPath);
        Wallet minersWallet = new Wallet(blockchain, minerPrivateKeyPath, minderPublicKeyPath);


        List<Thread> miners = Stream.generate(() -> new Thread(new Miner<>(blockchain, String.valueOf(minersIdGenerator.getAndIncrement()), minersWallet)))
                .limit(NUMBER_OF_MINERS)
                .collect(Collectors.toList());

        miners.forEach(Thread::start);


        userWallet.sendTransaction("bot_1", "bot_3", new BigDecimal(100));
        userWallet.sendTransaction("bot_1", "bot_3", new BigDecimal(101));
        userWallet.sendTransaction("bot_1", "bot_3", new BigDecimal(102));
        userWallet.sendTransaction("bot_1", "bot_3", new BigDecimal(103));
        TimeUnit.SECONDS.sleep(3);
        userWallet.sendTransaction("bot_1", "bot_3", new BigDecimal(104));
        TimeUnit.SECONDS.sleep(3);
        userWallet.sendTransaction("bot_1", "bot_3", new BigDecimal(105));
        TimeUnit.SECONDS.sleep(3);
        userWallet.sendTransaction("bot_1", "bot_3", new BigDecimal(106));
        userWallet.sendTransaction("bot_1", "bot_3", new BigDecimal(107));
        userWallet.sendTransaction("bot_1", "bot_3", new BigDecimal(108));
        TimeUnit.SECONDS.sleep(3);
        userWallet.sendTransaction("bot_1", "bot_3", new BigDecimal(109));
    }
}

