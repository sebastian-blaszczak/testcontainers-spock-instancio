package com.codepole.testcontainersspockinstancio.item;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

interface ItemDao extends CrudRepository<Item, String> {

    default Optional<List<Item>> findByRequest(ItemRequest request) {
        return findByNameContainsAndDescriptionContainsAndEanContains(request.name(),
                request.description(),
                request.ean());
    }

    Optional<List<Item>> findByNameContainsAndDescriptionContainsAndEanContains(String name, String description,
                                                                                String ean);
}
