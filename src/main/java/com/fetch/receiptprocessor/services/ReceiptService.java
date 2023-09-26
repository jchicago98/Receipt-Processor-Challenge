package com.fetch.receiptprocessor.services;

import com.fetch.receiptprocessor.models.Item;
import com.fetch.receiptprocessor.models.Receipt;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ReceiptService {
    ConcurrentHashMap<String, String> receiptData = new ConcurrentHashMap<>();

    public String getPoints(String id){
        return receiptData.get(id);
    }

    public String processReceipt(Receipt newReceipt) {
        // Calculate points based on rules
        int points = 0;

        // Rule 1: One point for every alphanumeric character in the retailer name.
        points += newReceipt.getRetailer().replaceAll("[^a-zA-Z0-9]", "").length();

        // Rule 2: 50 points if the total is a round dollar amount with no cents.
        double total = Double.parseDouble(newReceipt.getTotal());
        if (total == (int) total) {
            points += 50;
        }

        // Rule 3: 25 points if the total is a multiple of 0.25.
        if (Math.abs(total - Math.round(total)) < 0.01 && total % 0.25 == 0) {
            points += 25;
        }

        // Rule 4: 5 points for every two items on the receipt.
        int itemCount = newReceipt.getItems().size();
        points += (itemCount / 2) * 5;

        // Rule 5: If the trimmed length of the item description is a multiple of 3,
        // multiply the price by 0.2 and round up to the nearest integer.
        for (Item item : newReceipt.getItems()) {
            String description = item.getShortDescription().trim();
            if (description.length() % 3 == 0) {
                double price = Double.parseDouble(item.getPrice());
                points += (int) Math.ceil(price * 0.2);
            }
        }

        // Rule 6: 6 points if the day in the purchase date is odd.
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(newReceipt.getPurchaseDate(), dayFormatter);
        int day = date.getDayOfMonth();
        if (day % 2 != 0) {
            points += 6;
        }

        // Rule 7: 10 points if the time of purchase is after 2:00pm and before 4:00pm.
        DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime time = LocalTime.parse(newReceipt.getPurchaseTime(), hourFormatter);
        int hour = time.getHour();
        if (hour >= 14 && hour <= 16) {
            points += 10;
        }

        // Generate a unique ID and store the receipt
        UUID uuid = UUID.randomUUID();
        String uniqueId = uuid.toString();
        receiptData.put(uniqueId, Integer.toString(points));
        return uniqueId;
    }


}