package com.codepole.testcontainersspockinstancio.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ItemController {

    private final ItemDao itemDao;

    @GetMapping("/item/get")
    ResponseEntity<?> getItem(@RequestParam(required = false, defaultValue = "") String name,
                              @RequestParam(required = false, defaultValue = "") String description,
                              @RequestParam(required = false, defaultValue = "") String ean) {
        return itemDao.findByRequest(ItemRequest.builder()
                        .name(name)
                        .description(description)
                        .ean(ean)
                        .build())
                .map(ItemMapper::toResponseEntity)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("item/save")
    ResponseEntity<?> saveItem(@RequestBody ItemDto dto) {
        return Optional.of(itemDao.save(ItemMapper.toEntity(dto)))
                .map(ItemMapper::toResponseEntity)
                .orElse(ResponseEntity.internalServerError().build());
    }

}
