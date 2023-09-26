package com.fetch.receiptprocessor.controllers;

import com.fetch.receiptprocessor.models.Receipt;
import com.fetch.receiptprocessor.services.ReceiptService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/receipts")
@CrossOrigin("*")
public class ReceiptController {

    private ReceiptService receiptService;

    public ReceiptController(ReceiptService receiptService){
        this.receiptService = receiptService;
    }

    @GetMapping("/{id}/points")
    public ResponseEntity getPoints(@PathVariable String id){
        Map<String, Integer> response = new HashMap<>();
        String points = this.receiptService.getPoints(id);
        if(points == null){
            return new ResponseEntity<>("No receipt found for that id",HttpStatus.NOT_FOUND);
        }
        else{
            Integer totalPoints = Integer.parseInt(points);
            response.put("points", totalPoints);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @PostMapping("/process")
    public ResponseEntity processReceipt(@RequestBody Receipt newReceipt) {
        Map<String, String> response = new HashMap<>();
        if(newReceipt.getRetailer() == null || newReceipt.getPurchaseDate() == null || newReceipt.getPurchaseTime() == null || newReceipt.getItems() == null || newReceipt.getTotal() == null){
            return new ResponseEntity<>("The receipt is invalid", HttpStatus.BAD_REQUEST);
        }
        else{
            String newId = this.receiptService.processReceipt(newReceipt);
            response.put("id", newId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

    }


}
