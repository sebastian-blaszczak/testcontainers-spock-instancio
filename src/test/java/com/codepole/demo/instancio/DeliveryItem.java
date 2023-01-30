package com.codepole.demo.instancio;

import com.codepole.testcontainersspockinstancio.item.Item;

import java.time.LocalDateTime;

record DeliveryItem(String id,
                    String code,
                    LocalDateTime prepareDate,
                    LocalDateTime deliveryDate,
                    Item item) {
}
