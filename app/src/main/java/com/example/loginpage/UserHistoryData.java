package com.example.loginpage;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class UserHistoryData implements Serializable {

    // User email
    private String usrID;
    // Vendors for a delivery
    private String[] vendor;
    // Order numbers for a delivery
    private int[] vendorID;
    private String[] orderNo;
    // Schedule of delivery
    private String time;
    // Transaction ID for checkout
    private int transacID;
    private int vendorIndex = 0, orderIndex = 0;

    public UserHistoryData() {
        vendor = new String[5];
        orderNo = new String[5];
        vendorIndex = 0;
        orderIndex = 0;
        usrID = "";
        vendorID = new int[5];
    }
    public void setUserName(String userName){
        usrID = userName;
    }

    public void setVendor(String v, int id){
        if (vendorIndex < 5) {
            vendor[vendorIndex] = v;
            vendorID[vendorIndex] = id;
            vendorIndex++;
        }
    }
    public void setOrderNo(String orderNumber){
        if (orderIndex < 5) {
            orderNo[orderIndex] = orderNumber;
            orderIndex++;
        }
    }
    public void setTime(String t){
        time = t;
    }

    public String getUserName() { return usrID; }

    public String[] getVendor(){
        return vendor;
    }

    public String[] getOrderNo() { return orderNo; }

    public String getTime() { return time; }

    public int getTransac() { return transacID; }

    public int[] getVendorId(){ return vendorID; }

    // Generates strings for recent history on home screen
    public String displayTimeAndVendor() {
        StringBuilder displayString = new StringBuilder();
        displayString.append(time).append(" ").append(vendor[0]);
        // If more than one vendor was involved in the delivery,
        // add "and others" to the end of the String
        if (vendorIndex > 1)
            displayString.append(" and others");
        return displayString.toString();
    }

    // Send delivery data to payment system and then
    // save itself to cache file
    public String checkout(Context context) {
        String temp = usrID;
        // Convert email to hash before sending to payment system
        this.setUserName(Helper.getHashString(usrID));

        // Payment system is imaginary; if it existed,
        // a connection would be established to it here
        System.out.println("Connecting to payment system");

        // Simple example of generating a transaction ID
        // If payment system existed, it could generate a transaction ID instead
        transacID = Math.abs(usrID.hashCode() * time.hashCode());

        // Change email back before writing to internal storage
        this.setUserName(temp);

        // Get user history
        ArrayList<UserHistoryData> history = readHistory(context);
        // If there is no history, start a new history list
        if (history == null)
            history = new ArrayList<>();
        // Append data to history list
        history.add(this);
        // Write list to file
        try (FileOutputStream fos = context.openFileOutput(getFileName(), Context.MODE_PRIVATE)) {
            ObjectOutputStream output = new ObjectOutputStream(fos);
            output.writeObject(history);
            output.close();
            System.out.println("Writing object " + vendor[0]);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        String confirmation = "Transaction successful\nTransaction ID:\n" + transacID;
        return confirmation;
    }

    // Read contents of user history file and return them as an ArrayList
    public ArrayList<UserHistoryData> readHistory(Context context)
    {
        ArrayList<UserHistoryData> list = new ArrayList<>();
        File usrHistory = new File(context.getFilesDir(), getFileName());
        // User history file does not exist yet; there is nothing to read
        if (!usrHistory.exists())
            return null;
        // User history file exists; read from it
        else {
            try (FileInputStream fis = context.openFileInput(getFileName())) {
                ObjectInputStream input = new ObjectInputStream(fis);
                list = new ArrayList<>();
                list = (ArrayList<UserHistoryData>) input.readObject();
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return list;
        }
    }
    // Generate user history cache file name based on unique user ID
    private String getFileName() {
        String fileName = usrID.hashCode() + "";
        return fileName;
    }
}
