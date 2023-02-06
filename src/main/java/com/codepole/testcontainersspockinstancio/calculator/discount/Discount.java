package com.codepole.testcontainersspockinstancio.calculator.discount;

import com.codepole.testcontainersspockinstancio.item.ItemDto;

import java.util.List;
import java.util.Optional;

public interface Discount {

    Double calculateDiscount(List<ItemDto> items, String code);

    default Double noDiscount(List<ItemDto> items) {
        return items.stream()
                .map(ItemDto::price)
                .reduce(Double::sum)
                .orElse(0D);
    }

    default Double percentageMultiplier(Double discount) {
        return Optional.of(discount)
                .filter(value -> value < 100)
                .map(value -> (100 - value) / 100)
                .orElseThrow(IllegalArgumentException::new);
    }

    default Double priceWithDiscount(List<ItemDto> items, Double discount) {
        return noDiscount(items) * percentageMultiplier(discount);
    }
}
