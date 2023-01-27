package com.codepole.testcontainersspockinstancio.item;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

interface ItemDao extends CrudRepository<Item, String> {

    default Optional<List<Item>> findByQuery(ItemQuery query) {
        return findByNameContainsOrDescriptionContains(query.value(), query.value());
    }

    Optional<List<Item>> findByNameContainsOrDescriptionContains(String name, String desc);
}
