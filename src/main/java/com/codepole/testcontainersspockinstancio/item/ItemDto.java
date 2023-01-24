package com.codepole.testcontainersspockinstancio.item;

import lombok.Builder;

@Builder
public record ItemDto(String id,
                      String name,
                      String ean,
                      Double price,
                      String description,
                      ItemType type) {
}
