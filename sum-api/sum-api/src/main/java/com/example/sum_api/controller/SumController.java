package com.example.sum_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SumController {

    @GetMapping("/sum")
    public String calculateSum(
            @RequestParam(value = "num1", defaultValue = "0") int num1,
            @RequestParam(value = "num2", defaultValue = "0") int num2) {
        int sum = num1 + num2;
        return String.format("The sum of %d and %d is %d", num1, num2, sum);
    }
}

