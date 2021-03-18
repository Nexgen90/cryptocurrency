package ru.nexgen.cryptocurrency.miner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nexgen.cryptocurrency.blockcahin.model.Block;
import ru.nexgen.cryptocurrency.blockcahin.model.SignedData;
import ru.nexgen.cryptocurrency.blockcahin.service.Blockchain;
import ru.nexgen.cryptocurrency.wallet.service.Wallet;
import ru.nexgen.cryptocurrency.blockcahin.utils.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class Miner<T extends Serializable & SignedData> implements Runnable {
    private final Random random = new Random();
    private final Blockchain<T> blockchain;
    private final String name;
    private final Wallet wallet;

//    public Miner(Blockchain<T> blockchain, String name, Wallet wallet) {
//        this.blockchain = blockchain;
//        this.name = name;
//        this.wallet = wallet;
//        this.random = new Random();
//    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            Block<T> lastBlock = blockchain.getLastBlock();
            Block<T> nextBlock = generateNewBlock(lastBlock.getId() + 1, name, lastBlock.getHash());
            if (nextBlock == null) {
                try {
                    TimeUnit.SECONDS.sleep(1L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            blockchain.acceptNewBlock(nextBlock);
        }
    }

    public Block<T> generateNewBlock(Integer blockId, String minerId, String prevBlockHash) {
        Block<T> generatedBlock;
        int magicNumber;
        String currentHash;
        String prefix;

        List<T> pendingMessages = blockchain.getPendingMessages();
        if (pendingMessages == null || pendingMessages.isEmpty()) {
            return null;
        }

        do {
            prefix = blockchain.getPrefix();
            magicNumber = random.nextInt();
            generatedBlock = new Block<>(magicNumber, prevBlockHash, new Date().getTime());
            generatedBlock.setData(new ArrayList<>(pendingMessages));
            generatedBlock.setId(blockId);
            generatedBlock.setMiner(minerId);
//            StringBuilder data = new StringBuilder();
//            generatedBlock.getData().forEach(t -> data.append(t.toString()));


            try {
                wallet.addMinerBenefit(name, generatedBlock);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String data = generatedBlock.getData().stream().map(Object::toString).collect(Collectors.joining());
            currentHash = StringUtil.applySha256(prevBlockHash + data + magicNumber);

        } while (!currentHash.startsWith(prefix));
        generatedBlock.setHash(currentHash);
        return generatedBlock;
    }
}
