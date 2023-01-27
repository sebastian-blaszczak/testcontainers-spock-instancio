package com.codepole.demo.instancio

import com.codepole.testcontainersspockinstancio.item.Item
import lombok.Getter
import lombok.RequiredArgsConstructor
import org.instancio.Instancio
import spock.lang.Specification

import java.time.LocalDateTime
import java.util.function.Supplier

import static org.instancio.Select.*

class InstancioTest extends Specification {

    def "scope feature preview"() {
        given: "simple and fast usage"
        def simple = Instancio.create(Item.class)

        and: "all string should be set up as 'string-value'"
        def stringValues = Instancio.of(Item.class)
                .set(allStrings(), "string-value")
                .create();

        and: "all double values should be set up as 50"
        def doubleValues = Instancio.of(Item.class)
                .set(allDoubles(), 50D)
                .create();

        and: "field named description should have value 'desc'"
        def withField = Instancio.of(Item.class)
                .set(field("description"), "desc")
                .create()

        expect:
        simple != null
        stringValues.name() == "string-value"
        doubleValues.price() == 50D
        withField.description() == "desc"
    }

    def "preview features of set and supply"() {
        given: "set vs supply"
        def setSupply = Instancio.of(ItemDelivery.class)
                .set(allStrings(), "same-string")
                .supply(all(LocalDateTime.class), (Supplier) (() -> LocalDateTime.now()))
                .create()

        expect:
        setSupply.deliveryStreet == setSupply.deliveryCity
        setSupply.deliveryDate != setSupply.prepareDate
    }

    def "preview of model and metamodel"() {
        given: "create common model"
        def model = Instancio.of(ItemDelivery.class)
                .set(all(LocalDateTime.class), LocalDateTime.now())
                .set(field("deliveryCity"), "city")
                .set(field("deliveryStreet"), "street")
                .toModel()

        and:
        def firstDelivery = Instancio.of(model)
                .set(field("item"), Instancio.create(Item.class))
                .create()

        and:
        def secondDelivery = Instancio.of(model)
                .set(field("item"), Instancio.create(Item.class))
                .create()

        expect:
        firstDelivery.deliveryCity == secondDelivery.deliveryCity
        firstDelivery.deliveryStreet == secondDelivery.deliveryStreet
        firstDelivery.prepareDate == secondDelivery.prepareDate
        firstDelivery.deliveryDate == secondDelivery.deliveryDate
        firstDelivery.item != secondDelivery.item
    }

    @Getter
    @RequiredArgsConstructor
    class ItemDelivery {
        private final String deliveryCity
        private final String deliveryStreet
        private final LocalDateTime prepareDate
        private final LocalDateTime deliveryDate
        private final Item item
    }
}
