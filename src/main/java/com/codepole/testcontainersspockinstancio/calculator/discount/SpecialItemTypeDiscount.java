package com.codepole.testcontainersspockinstancio.calculator.discount;

import com.codepole.testcontainersspockinstancio.item.ItemDto;
import com.codepole.testcontainersspockinstancio.item.ItemType;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class SpecialItemTypeDiscount implements Discount {

    private final ItemType type;
    private final Double discount;

    @Override
    public Double calculateDiscount(List<ItemDto> items, String code) {
        return hasType(items, type) ? priceWithDiscount(items, discount) : noDiscount(items);
    }

    private boolean hasType(List<ItemDto> items, ItemType type) {
        return items.stream()
                .map(ItemDto::type)
                .anyMatch(itemType -> itemType == type);
    }

}
