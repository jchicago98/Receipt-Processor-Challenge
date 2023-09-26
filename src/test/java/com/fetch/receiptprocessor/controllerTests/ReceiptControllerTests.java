package com.fetch.receiptprocessor.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fetch.receiptprocessor.controllers.ReceiptController;
import com.fetch.receiptprocessor.models.Receipt;
import com.fetch.receiptprocessor.services.ReceiptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;

import static org.mockito.Mockito.when;

@WebMvcTest(ReceiptController.class)
public class ReceiptControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReceiptService receiptService;

    @BeforeEach
    public void setUp() {
        // Mock the behavior of receiptService methods as needed for your tests
        // For example:
        when(receiptService.getPoints("validId")).thenReturn("100");
        when(receiptService.getPoints("invalidId")).thenReturn(null);

        Receipt mockReceipt = createMockReceipt();
        when(receiptService.processReceipt(mockReceipt)).thenReturn("newId");
    }


    @Test
    public void testGetPoints_WithValidId() throws Exception {
        // Mock the behavior of receiptService.getPoints()
        when(receiptService.getPoints("validId")).thenReturn("100");

        mockMvc.perform(MockMvcRequestBuilders.get("/receipts/validId/points"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.points").value(100));
    }

    @Test
    public void testGetPoints_WithInvalidId() throws Exception {
        // Mock the behavior of receiptService.getPoints()
        when(receiptService.getPoints("invalidId")).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/receipts/invalidId/points"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("No receipt found for that id"));
    }

    @Test
    public void testProcessReceipt_WithValidReceipt() throws Exception {
        // Create a mock receipt
        Receipt mockReceipt = createMockReceipt();

        // Mock the behavior of receiptService.processReceipt()
        when(receiptService.processReceipt(mockReceipt)).thenReturn("newId");

        String json = objectMapper.writeValueAsString(mockReceipt);

        mockMvc.perform(MockMvcRequestBuilders.post("/receipts/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testProcessReceipt_WithInvalidReceipt() throws Exception {
        // Create an invalid receipt (missing required fields)
        Receipt invalidReceipt = new Receipt();

        String json = objectMapper.writeValueAsString(invalidReceipt);

        mockMvc.perform(MockMvcRequestBuilders.post("/receipts/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("The receipt is invalid"));
    }

    private Receipt createMockReceipt() {
        // Create a valid receipt with required fields
        Receipt receipt = new Receipt();
        receipt.setRetailer("Example Retailer");
        receipt.setTotal("100.00");
        receipt.setPurchaseDate("2023-09-26");
        receipt.setPurchaseTime("15:30");
        receipt.setItems(new ArrayList<>());

        return receipt;
    }
}

