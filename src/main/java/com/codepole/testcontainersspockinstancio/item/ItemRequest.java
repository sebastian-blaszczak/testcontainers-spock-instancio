package com.codepole.testcontainersspockinstancio.item;

import lombok.Builder;

@Builder
public record ItemRequest(String name,
                          String description,
                          String ean) {
}
