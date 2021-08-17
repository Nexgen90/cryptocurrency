package ru.nexgen.utils;

import ru.nexgen.dto.Transaction;

public class TransactionFormater implements DataFormatter<Transaction> {
    @Override
    public String format(Transaction item) {
        return String.format("%s sent %s VC to %s", item.getFrom(), item.getValue(), item.getTo());
    }
}
