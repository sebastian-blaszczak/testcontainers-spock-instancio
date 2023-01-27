package com.codepole.testcontainersspockinstancio.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ItemController {

    private final ItemDao itemDao;

    @PostMapping("item/save")
    ResponseEntity<?> save(@RequestBody ItemDto dto) {
        return saveItem(dto);
    }

    @GetMapping("/item/search")
    ResponseEntity<?> search(@RequestParam(required = false, defaultValue = "") String query) {
        return itemDao.findByQuery(new ItemQuery(query))
                .map(ItemMapper::toResponseEntity)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/item/update")
    ResponseEntity<?> update(@RequestBody ItemDto dto) {
        return itemDao.existsById(dto.id()) ? save(dto) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/item/delete")
    ResponseEntity<?> delete(@RequestBody ItemDto dto) {
        return itemDao.existsById(dto.id()) ? deleteItem(dto) : ResponseEntity.notFound().build();
    }

    private ResponseEntity<ItemDto> saveItem(ItemDto dto) {
        return Optional.of(itemDao.save(ItemMapper.toEntity(dto)))
                .map(ItemMapper::toResponseEntity)
                .orElse(ResponseEntity.internalServerError().build());
    }

    private ResponseEntity<Boolean> deleteItem(ItemDto dto) {
        itemDao.delete(ItemMapper.toEntity(dto));
        return ResponseEntity.ok().build();
    }

}
