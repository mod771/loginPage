// Class containing constants and methods that many other classes
// in the app need to use
package com.example.loginpage;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Helper {
    // ip of SQL database
    final static String ip = "192.168.1.79";
    // port of SQL database
    final static String port = "3306";
    // username for database
    // For communication with the database to work, a 'god' user
    // with many permissions had to be made
    final static String user = "GOD";
    // password for database
    final static String password = "dog";
    // connection String
    final static String conString = "jdbc:mysql://" + ip + ":" + port + "/delivery_app";

    // Helper method for converting Strings to SHA-256 hashes
    // Based on: https://www.geeksforgeeks.org/sha-256-hash-in-java/
    public static String getHashString(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(str.getBytes(StandardCharsets.UTF_8));
            BigInteger bigInt = new BigInteger(1, hash);
            return bigInt.toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "error";
        }
    }
}
