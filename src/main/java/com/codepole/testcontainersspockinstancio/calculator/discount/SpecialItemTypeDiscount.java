package com.codepole.testcontainersspockinstancio.calculator.discount;

import com.codepole.testcontainersspockinstancio.item.ItemDto;
import com.codepole.testcontainersspockinstancio.item.ItemType;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class SpecialItemTypeDiscount implements Discount {

    private static final Double DISCOUNT = 10D;
    private final ItemType type;

    @Override
    public Double calculateDiscount(List<ItemDto> items, String code) {
        return contains(items, type) ? priceWithDiscount(items, DISCOUNT) : noDiscount(items);
    }

    private boolean contains(List<ItemDto> items, ItemType type) {
        return items.stream()
                .map(ItemDto::type)
                .anyMatch(itemType -> itemType == type);
    }

}
