package ru.nexgen.cryptocurrency.blockcahin.utils;

import org.springframework.stereotype.Component;
import ru.nexgen.cryptocurrency.blockcahin.model.Transaction;

@Component
public class TransactionFormatter implements DataFormatter<Transaction> {
    @Override
    public String format(Transaction item) {
        return String.format("%s sent %s VC to %s", item.getFrom(), item.getValue(), item.getTo());
    }
}
