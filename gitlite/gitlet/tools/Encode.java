package gitlet.tools;

import java.io.*;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encode {
    /**
     * Computes the SHA-1 hash of a file's content
     * @param file The file to hash
     * @return 40-character hexadecimal SHA-1 string
     */
    public static String sha1(File file) throws IOException {
        byte[] fileContent = Files.readAllBytes(file.toPath());
        return sha1(fileContent);
    }

    /**
     * Computes the SHA-1 hash of a byte array
     * @param input The bytes to hash
     * @return 40-character hexadecimal SHA-1 string
     */
    public static String sha1(byte[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hashBytes = md.digest(input);
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1 algorithm not found", e);
        }
    }

    /**
     * Converts a byte array to hexadecimal string
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}