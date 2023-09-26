package com.fetch.receiptprocessor.serviceTests;

import com.fetch.receiptprocessor.models.Item;
import com.fetch.receiptprocessor.models.Receipt;
import com.fetch.receiptprocessor.services.ReceiptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReceiptServiceTests {

    @InjectMocks
    private ReceiptService receiptService;

    @Mock
    private ConcurrentHashMap<String, String> receiptData;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetPoints() {
        Receipt mockReceipt = createMockReceipt();
        String uniqueId = receiptService.processReceipt(mockReceipt);
        when(receiptData.get(uniqueId)).thenReturn(calculateExpectedPoints(mockReceipt));
        String points = receiptService.getPoints(uniqueId);
        //Tests the example that Fetch provided
        assertEquals("109", points);
    }

    @Test
    public void testProcessReceipt() {
        Receipt mockReceipt = createMockReceipt();
        String expectedPoints = calculateExpectedPoints(mockReceipt);

        when(receiptData.put(anyString(), anyString())).thenReturn("uniqueId");
        String uniqueId = receiptService.processReceipt(mockReceipt);
        verify(receiptData).put(anyString(), eq(expectedPoints));

        assertFalse(uniqueId.isEmpty());
    }

    private String calculateExpectedPoints(Receipt receipt) {
        int points = 0;

        // Rule 1: One point for every alphanumeric character in the retailer name.
        points += receipt.getRetailer().replaceAll("[^a-zA-Z0-9]", "").length();

        // Rule 2: 50 points if the total is a round dollar amount with no cents.
        double total = Double.parseDouble(receipt.getTotal());
        if (total == (int) total) {
            points += 50;
        }

        // Rule 3: 25 points if the total is a multiple of 0.25.
        if (Math.abs(total - Math.round(total)) < 0.01 && total % 0.25 == 0) {
            points += 25;
        }

        // Rule 4: 5 points for every two items on the receipt.
        int itemCount = receipt.getItems().size();
        points += (itemCount / 2) * 5;

        // Rule 5: If the trimmed length of the item description is a multiple of 3,
        // multiply the price by 0.2 and round up to the nearest integer.
        for (Item item : receipt.getItems()) {
            String description = item.getShortDescription().trim();
            if (description.length() % 3 == 0) {
                double price = Double.parseDouble(item.getPrice());
                points += (int) Math.ceil(price * 0.2);
            }
        }

        // Rule 6: 6 points if the day in the purchase date is odd.
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(receipt.getPurchaseDate(), dayFormatter);
        int day = date.getDayOfMonth();
        if (day % 2 != 0) {
            points += 6;
        }

        // Rule 7: 10 points if the time of purchase is after 2:00pm and before 4:00pm.
        DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime time = LocalTime.parse(receipt.getPurchaseTime(), hourFormatter);
        int hour = time.getHour();
        if (hour >= 14 && hour <= 16) {
            points += 10;
        }

        return Integer.toString(points);
    }

    private Receipt createMockReceipt() {
        Receipt receipt = new Receipt();
        receipt.setRetailer("M&M Corner Market");
        receipt.setTotal("9.00");
        receipt.setPurchaseDate("2022-03-20");
        receipt.setPurchaseTime("14:33");

        List<Item> items = new ArrayList<>();
        Item item1 = new Item();
        item1.setShortDescription("Gatorade");
        item1.setPrice("2.25");
        //items.add(item1);
        for(int n = 0; n < 4; n++){
            items.add(item1);
        }

        receipt.setItems(items);

        return receipt;
    }
}
