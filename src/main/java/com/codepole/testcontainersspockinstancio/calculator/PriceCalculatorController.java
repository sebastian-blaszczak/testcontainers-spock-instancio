package com.codepole.testcontainersspockinstancio.calculator;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PriceCalculatorController {

    private final PriceCalculator priceCalculator;

    @PostMapping("/price/calculate")
    ResponseEntity<?> calculate(@RequestBody PriceCalculationRequest request) {
        return priceCalculator.calculate(request.items(), request.code())
                .map(PriceDto::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.internalServerError().build());
    }
}
