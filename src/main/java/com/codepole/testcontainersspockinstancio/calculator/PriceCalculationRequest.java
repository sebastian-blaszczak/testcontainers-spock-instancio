package com.codepole.testcontainersspockinstancio.calculator;

import com.codepole.testcontainersspockinstancio.item.ItemDto;

import java.util.List;

public record PriceCalculationRequest(List<ItemDto> items, String code) {
}
