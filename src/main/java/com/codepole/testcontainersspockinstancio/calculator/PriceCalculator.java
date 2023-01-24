package com.codepole.testcontainersspockinstancio.calculator;

import com.codepole.testcontainersspockinstancio.calculator.discount.CodeDiscount;
import com.codepole.testcontainersspockinstancio.calculator.discount.Discount;
import com.codepole.testcontainersspockinstancio.calculator.discount.SpecialItemTypeDiscount;
import com.codepole.testcontainersspockinstancio.item.ItemDto;
import com.codepole.testcontainersspockinstancio.item.ItemType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class PriceCalculator {

    @Value("${discount.code}")
    private String discountCode;

    @Value("${discount.item.type}")
    private String discountItemType;

    private List<Discount> discounts;

    @PostConstruct
    private void initialize() {
        discounts = List.of(new SpecialItemTypeDiscount(ItemType.valueOf(discountItemType)),
                new CodeDiscount(discountCode));
    }

    Optional<Double> calculate(List<ItemDto> items, String code) {
        return discounts.stream()
                .map(discount -> discount.calculateDiscount(items, code))
                .map(price -> BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP).doubleValue())
                .reduce(Double::min);
    }

}
