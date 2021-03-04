package ru.nexgen.cryptocurrency.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nexgen.cryptocurrency.service.Blockchain;
import ru.nexgen.cryptocurrency.utils.BlockWithTransactionsTextFormatter;

@Slf4j
@RestController
@RequestMapping("blockchain")
@RequiredArgsConstructor
public class BlockchainController {

    private final Blockchain blockchain;
    private final BlockWithTransactionsTextFormatter formatter;

//    @GetMapping("/{blockId}")
    @GetMapping("/block/last")
//    public String getBlock(@PathVariable Long blockId) {
    public String getBlock() {
        return formatter.format(blockchain.getLastBlock());
    }
}
