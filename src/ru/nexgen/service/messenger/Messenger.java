package ru.nexgen.service.messenger;
//
//import blockchain.dto.Message;
//import blockchain.dto.Signer;
//import blockchain.service.Blockchain;
//
//import java.io.IOException;
//import java.security.GeneralSecurityException;
//
//public class Messenger {
//    private final Blockchain<Message> blockchain;
//    private final Signer signer;
//
//    public Messenger(Blockchain<Message> blockchain, String privateKeyPath, String publicKeyFilePath) throws IOException, GeneralSecurityException {
//        this.blockchain = blockchain;
//        this.signer = new Signer(privateKeyPath, publicKeyFilePath);
//    }
//
//    public void sendMessage(String from, String text) throws Exception {
//        int id = blockchain.getUnicMessageId();
//        Message message = new Message(id, from, text, signer.sign(from + text + id), signer.getPublicKeyBytes());
//        blockchain.sendMessage(message);
//    }
//}
