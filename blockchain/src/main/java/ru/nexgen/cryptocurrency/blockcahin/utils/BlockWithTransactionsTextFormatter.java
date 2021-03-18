package ru.nexgen.cryptocurrency.blockcahin.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.nexgen.cryptocurrency.blockcahin.model.Block;
import ru.nexgen.cryptocurrency.blockcahin.model.Transaction;

@Component
@RequiredArgsConstructor
public class BlockWithTransactionsTextFormatter {
    private static final String DEFAULT_TRANSACTIONS_TEXT = "No transactions\n";
    private final TransactionFormatter transactionFormatter;

    public String format(Block<Transaction> block) {
        StringBuilder blockInfo = new StringBuilder();
        blockInfo.append("Block:\n")
                .append("Created by: ").append(block.getMiner())
                .append("\n")
                .append("Id: ").append(block.getId())
                .append("\n")
                .append("Timestamp: ").append(block.getTimestamp())
                .append("\n")
                .append("Magic number: ").append(block.getMagicNumber())
                .append("\n")
                .append("Hash of the previous block:\n")
                .append(block.getHashPreviousBlock()).append("\n")
                .append("Hash of the block:\n")
                .append(block.getHash())
                .append("\n")
                .append("Block data:\n");

        if (block.getData() == null || block.getData().isEmpty()) {
            blockInfo.append(DEFAULT_TRANSACTIONS_TEXT);
        } else {
            block.getData().forEach(t ->
                    blockInfo.append(transactionFormatter.format(t)).append("\n"));
        }
        return blockInfo.toString();
    }
}
