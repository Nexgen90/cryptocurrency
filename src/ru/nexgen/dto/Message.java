package ru.nexgen.dto;
//
//import java.io.Serializable;
//
//import static java.nio.charset.StandardCharsets.UTF_8;
//
//public class Message implements SignedData, Serializable {
//    private final Integer id;
//    private final String from;
//    private final String message;
//    private final byte[] dataSignature;
//    private final byte[] publicKey;
//
//    public Message(Integer id, String from, String message, byte[] dataSignature, byte[] publicKey) {
//        this.id = id;
//        this.from = from;
//        this.message = message;
//        this.dataSignature = dataSignature;
//        this.publicKey = publicKey;
//    }
//
//    public String getFrom() {
//        return from;
//    }
//
//    public String getMessage() {
//        return message;
//    }
//
//    @Override
//    public Integer id() {
//        return id;
//    }
//
//    @Override
//    public byte[] raw() {
//        return (from + message + id).getBytes(UTF_8);
//    }
//
//    @Override
//    public byte[] dataSignature() {
//        return dataSignature;
//    }
//
//    @Override
//    public byte[] publicKey() {
//        return publicKey;
//    }
//
//    @Override
//    public String toString() {
//        return from + ": " + message + "\n";
//    }
//}
