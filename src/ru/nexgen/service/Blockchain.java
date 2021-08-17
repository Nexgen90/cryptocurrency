package ru.nexgen.service;

import ru.nexgen.dto.Block;
import ru.nexgen.dto.SignedData;
import ru.nexgen.dto.Transaction;
import ru.nexgen.utils.DataFormatter;
import ru.nexgen.utils.SignValidator;
import ru.nexgen.utils.StringUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.reverseOrder;

public class Blockchain<T extends Serializable & SignedData> {
    private static final int MILLISECONDS_TO_GENERATE_NEW_BLOCK = 1_000;
    private static final BigDecimal MINERS_BENEFIT_VALUE = new BigDecimal(10);
    private static final String SYSTEM_NAME = "System";
    //    private static final int BLOCK_SIZE = 100;
    private final List<Block<T>> blockchain;
    private final ReentrantReadWriteLock blockchainLock;
    private final Persister<T> persister;
    private final Queue<List<T>> pendingMessages;
    private final ReentrantReadWriteLock pendingMessagesLock;
    private final AtomicLong idGenerator;
    private final DataFormatter<T> formatter;
    private int prefixLength;

    public Blockchain(Persister<T> persister, DataFormatter<T> formatter) {
        this.blockchainLock = new ReentrantReadWriteLock();
        this.pendingMessagesLock = new ReentrantReadWriteLock();
        this.prefixLength = 1;
        this.persister = persister;
        this.formatter = formatter;
        List<Block<T>> loadedBlockchain = persister.load();
        pendingMessages = new LinkedList<>();

        if (!loadedBlockchain.isEmpty() && loadedBlockchain.size() > 1 && isValidBlockchain(loadedBlockchain)) {
            this.blockchain = loadedBlockchain;
            Block<T> lastBlock = getLastBlock();
            if (lastBlock.getId() == 1) {
                this.idGenerator = new AtomicLong(2);
            } else {
                this.idGenerator = new AtomicLong(lastBlock.getData().stream().map(SignedData::id).sorted(reverseOrder()).findFirst().get() + 1);
            }
        } else {
            this.blockchain = new ArrayList<>();
            this.idGenerator = new AtomicLong(1);
            Block<T> firstBlock = new Block<>(1, "0", new Date().getTime());
            firstBlock.setHash(StringUtil.applySha256("0" + "" + 1));
            firstBlock.setMiner(SYSTEM_NAME);
            firstBlock.setId(1);
            blockchain.add(firstBlock);
            printStats(firstBlock);
            persister.save(blockchain);
        }
    }

    public void acceptNewBlock(Block<T> block) {
        WriteLock blocksLock = blockchainLock.writeLock();
        WriteLock pendingMsgLock = pendingMessagesLock.writeLock();
        try {
            blocksLock.lock();
            pendingMsgLock.lock();

            if (blockchain.size() != 0) {
                List<T> transactions = pendingMessages.peek();
                if (transactions == null || transactions.isEmpty()) return;
                List<T> transactionsInBlock = block.getData();
                if (transactionsInBlock == null || transactionsInBlock.isEmpty()) return;
                if ((transactionsInBlock.size() - 1) != transactions.size()) return;

                for (int i = 0; i < transactions.size(); i++) {
                    boolean isEqual = transactions.get(i).equals(transactionsInBlock.get(i));
                    if (!isEqual) return;
                }

                //Проверяю корректность транзации с наградой, которую майнер добавил сам себе
                Transaction minersBenefitTransaction = (Transaction) transactionsInBlock.get(transactionsInBlock.size() - 1);
                if (!SYSTEM_NAME.equals(minersBenefitTransaction.getFrom())) {
                    return;
                }
                if (!MINERS_BENEFIT_VALUE.equals(minersBenefitTransaction.getValue())) {
                    return;
                }
            }

            if (isValidPrevHash(block) && isSigned(block.getData()) && isCorrectDataIds(block.getData())) {
                blockchain.add(block);
                pendingMessages.poll();
                printStats(block);
                adjustComplexity(block);
                persister.save(blockchain);
            }
        } finally {
            blocksLock.unlock();
            pendingMsgLock.unlock();
        }
    }

    public boolean isValidPrevHash(Block<T> block) {
        if (blockchain.size() == 0) {
            return true;
        }
        String lastBlockHash = blockchain.get(blockchain.size() - 1).getHash();
        return Objects.equals(block.getHashPreviousBlock(), lastBlockHash);
    }

    public boolean isValidBlockchain(List<Block<T>> listBlocks) {
        for (int i = listBlocks.size() - 1; i > 0; i--) {
            Block<T> lastBlock = listBlocks.get(i);
            List<T> lastBlockData = lastBlock.getData();
            String data = "";
            if (lastBlockData != null) {
                data = lastBlockData.stream().map(Object::toString).collect(Collectors.joining());
            }
            String justCalculatedHash = StringUtil.applySha256(lastBlock.getHashPreviousBlock() + data + lastBlock.getMagicNumber());

            if (!justCalculatedHash.equals(lastBlock.getHash())) {
                return false;
            }
            Block<T> prevBlock = listBlocks.get(i - 1);
            if (!prevBlock.getHash().equals(lastBlock.getHashPreviousBlock())) {
                return false;
            }
            if (!isSigned(lastBlock.getData())) {
                return false;
            }

            if (prevBlock.getId() != 1) {
                long lastTransactionMaxId = lastBlock.getData().stream().map(SignedData::id).sorted(reverseOrder()).findFirst().get();
                long prevTransactionMaxId = prevBlock.getData().stream().map(SignedData::id).sorted(reverseOrder()).findFirst().get();
                if (lastTransactionMaxId <= prevTransactionMaxId) {
                    return false;
                }
            }

        }
        return true;
    }

    private void printStats(Block<T> block) {
        StringBuilder dataText = new StringBuilder();
        if (block.getData() == null) {
            dataText.append("No transactions\n");
        } else {
            for (T transaction : block.getData()) {
                if (transaction == null) {
                } else {
                    dataText.append(formatter.format(transaction)).append("\n");
                }
            }
        }

        System.out.println("Block:\n" +
                "Created by: " + block.getMiner() + "\n" +
                "Id: " + block.getId() + "\n" +
                "Timestamp: " + block.getTimestamp() + "\n" +
                "Magic number: " + block.getMagicNumber() + "\n" +
                "Hash of the previous block:\n" +
                block.getHashPreviousBlock() + "\n" +
                "Hash of the block:\n" +
                block.getHash() + "\n" +
                "Block data:\n" + dataText
        );
        System.out.printf("Block was generating for %d milliseconds\n", getGenerationTime(block));
    }

    private void adjustComplexity(Block block) {
        long generationTime = getGenerationTime(block);

        if (generationTime < MILLISECONDS_TO_GENERATE_NEW_BLOCK) {
            prefixLength = prefixLength + 1;
            System.out.printf("N was increased to %s\n\n", prefixLength);
        } else {
            if (prefixLength >= 2) {
                prefixLength = prefixLength - 1;
                System.out.printf("N was decreased to %s\n\n", prefixLength);
            } else {
                System.out.print("N stays the same\n\n");
            }
        }

    }

    private long getGenerationTime(Block block) {
        if (blockchain.size() < 2) {
            return 0;
        }
        Long lastBlockGenTime = blockchain.get(blockchain.size() - 2).getTimestamp();
        return block.getTimestamp() - lastBlockGenTime;
    }

    public String getPrefix() {
        ReadLock readLock = blockchainLock.readLock();
        try {
            readLock.lock();
            return Stream.iterate("0", x -> "0")
                    .limit(prefixLength)
                    .reduce("", (x, y) -> x + y);
        } finally {
            readLock.unlock();
        }
    }

    public Block<T> getLastBlock() {
        ReadLock readLock = blockchainLock.readLock();
        try {
            readLock.lock();
            return blockchain.get(blockchain.size() - 1);
        } finally {
            readLock.unlock();
        }
    }

    public List<T> getPendingMessages() {
        ReadLock readLock = pendingMessagesLock.readLock();
        try {
            readLock.lock();
            return pendingMessages.peek();
        } finally {
            readLock.unlock();
        }
    }

    public void sendTransaction(T transaction) {
        //todo: проверить что transaction корректно заполнен (сумма перевода > 0)

        WriteLock writeLock = pendingMessagesLock.writeLock();
        try {
            writeLock.lock();

            Transaction currentMessage = (Transaction) transaction;
            Optional<Transaction> lastSendedMsgFormCurrentUser = blockchain.stream()
                    .filter(block -> block.getData() != null)
                    .map(Block::getData)
                    .flatMap(Collection::stream)
                    .map(t -> (Transaction) t)
                    .filter(t -> t.getFrom().equals(currentMessage.getFrom()))
                    .findFirst();

            if (lastSendedMsgFormCurrentUser.isPresent()) {
                byte[] lastPublicKey = lastSendedMsgFormCurrentUser.get().publicKey();
                if (!Arrays.equals(lastPublicKey, currentMessage.publicKey())) {
                    return;
                }
            }

            if (isSigned(transaction) && transaction.id() < idGenerator.get()) {
                List<T> lastList = pendingMessages.peek();
                if (lastList == null) {
                    lastList = new ArrayList<>();
                    pendingMessages.offer(lastList);
                }

                lastList.add(transaction);

            }
        } finally {
            writeLock.unlock();
        }
    }

    public long getUnicTransactionId() {
        return idGenerator.getAndIncrement();
    }

    private boolean isSigned(List<T> data) {
        for (T transaction : data) {
            if (!isSigned(transaction)) {
                return false;
            }
        }
        return true;
    }

    private boolean isSigned(T data) {
        try {
            SignValidator signValidator = new SignValidator(data.publicKey());
            return signValidator.verifySignature(data.raw(), data.dataSignature());
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isCorrectDataIds(List<T> data) {
        for (T transaction : data) {
            if (!isCorrectDataIds(transaction)) {
                return false;
            }
        }
        return true;
    }

    private boolean isCorrectDataIds(T data) {
        if (getLastBlock().getId() == 1) {
            return true;
        }

        List<T> lastBlockData = getLastBlock().getData();
        Optional<Long> maxId = lastBlockData.stream()
                .limit(lastBlockData.size() - 1) //смотрим отдельно транзакции из вне
                .map(SignedData::id)
                .sorted(reverseOrder())
                .findFirst();
        if (maxId.isPresent()) {
            return data.id() > maxId.get();
        } else {
            return false;
        }

        //смотрим отдельно транзакцию награды майнера

    }

    public BigDecimal getMinerBenefit() {
        return MINERS_BENEFIT_VALUE;
    }

    public String getSystemName() {
        return SYSTEM_NAME;
    }
}
