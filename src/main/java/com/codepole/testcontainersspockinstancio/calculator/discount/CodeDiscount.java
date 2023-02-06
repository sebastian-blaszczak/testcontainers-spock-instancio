package com.codepole.testcontainersspockinstancio.calculator.discount;

import com.codepole.testcontainersspockinstancio.item.ItemDto;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CodeDiscount implements Discount {

    private final String discountCode;
    private final Double discount;

    @Override
    public Double calculateDiscount(List<ItemDto> items, String code) {
        return codeMatches(code) ? priceWithDiscount(items, discount) : noDiscount(items);
    }

    private boolean codeMatches(String code) {
        return code.equals(discountCode);
    }
}
