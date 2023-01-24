package com.codepole.testcontainersspockinstancio.item;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Builder
@Document(indexName = "item")
public record Item(@Id String id,
                   String name,
                   String ean,
                   Double price,
                   String description,
                   ItemType type) {
}
