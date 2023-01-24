package com.codepole.testcontainersspockinstancio.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ItemMapper {

    static ResponseEntity<List<ItemDto>> toResponseEntity(List<Item> items) {
        return ResponseEntity.ok(items.stream()
                .map(ItemMapper::toDto)
                .toList());
    }

    static ResponseEntity<ItemDto> toResponseEntity(Item item) {
        return ResponseEntity.ok(toDto(item));
    }

    static Item toEntity(ItemDto dto) {
        return Item.builder()
                .id(UUID.randomUUID().toString())
                .name(dto.name())
                .ean(dto.ean())
                .price(dto.price())
                .description(dto.description())
                .type(dto.type())
                .build();
    }

    static ItemDto toDto(Item item) {
        return ItemDto.builder()
                .id(item.id())
                .name(item.name())
                .ean(item.ean())
                .price(item.price())
                .description(item.description())
                .type(item.type())
                .build();
    }
}
