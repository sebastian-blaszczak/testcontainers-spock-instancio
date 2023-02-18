package com.codepole.demo.instancio


import com.codepole.testcontainersspockinstancio.item.Item
import com.codepole.testcontainersspockinstancio.item.ItemType
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils
import spock.lang.Specification

import java.time.LocalDateTime
import java.util.concurrent.ThreadLocalRandom

class TraditionalDataSpecification extends Specification {

    def "should create delivery item"() {
        given:
        def delivery = createDeliveryItem()

        expect:
        delivery.id() != null
        delivery.code() != null
        delivery.prepareDate() != null
        delivery.deliveryDate() != null
        delivery.item() != null
    }

    private def createDeliveryItem() {
        return new DeliveryItem(
                RandomStringUtils.randomAlphabetic(5),
                RandomStringUtils.randomAlphabetic(5),
                LocalDateTime.now(),
                LocalDateTime.now(),
                createItem()
        )
    }

    private def createItem() {
        return new Item(
                RandomStringUtils.randomAlphabetic(5),
                RandomStringUtils.randomAlphabetic(5),
                RandomStringUtils.randomAlphabetic(5),
                random.nextDouble(),
                RandomStringUtils.randomAlphabetic(5),
                generateType()
        )
    }

    private def generateType() {
        def typeIndex = random.nextInt(0, ItemType.values().size())
        return List.of(ItemType.values()).get(typeIndex)
    }

    private final ThreadLocalRandom random = ThreadLocalRandom.current()
}
