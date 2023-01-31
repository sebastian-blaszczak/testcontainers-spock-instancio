package com.codepole.demo.instancio

import com.codepole.testcontainersspockinstancio.item.Item
import org.instancio.Instancio
import spock.lang.Specification

import java.time.LocalDateTime
import java.util.function.Supplier

import static org.assertj.core.api.Assertions.assertThat
import static org.instancio.Select.*

class InstancioSpecification extends Specification {

    def "create delivery item"() {
        given:
        def delivery = Instancio.create(DeliveryItem.class)

        expect:
        delivery.id() != null
        delivery.code() != null
        delivery.prepareDate() != null
        delivery.deliveryDate() != null
        delivery.item() != null
    }

    def "scope feature preview"() {
        given: "all string should be set up as 'string-value'"
        def stringValues = Instancio.of(Item.class)
                .set(allStrings(), "string-value")
                .create()

        and: "field named description should have value 'desc'"
        def withField = Instancio.of(Item.class)
                .set(field("description"), "desc")
                .create()

        and: "all doubles should have value in range 1-50"
        def generatedValue = Instancio.of(Item.class)
                .generate(allDoubles(), generator -> generator.doubles().range(1D, 50D))
                .create()

        expect:
        stringValues.name() == "string-value"
        withField.description() == "desc"
        assertThat(generatedValue.price()).isBetween(1D, 50D)
    }

    def "preview features of set and supply"() {
        given: "set vs supply"
        def setSupply = Instancio.of(DeliveryItem.class)
                .set(allStrings(), "same-string")
                .supply(all(LocalDateTime.class), (Supplier) (() -> LocalDateTime.now()))
                .create()

        expect:
        setSupply.code() == setSupply.id()
        setSupply.deliveryDate() != setSupply.prepareDate()
    }

    def "preview of model"() {
        given: "create common model"
        def model = Instancio.of(DeliveryItem.class)
                .set(all(LocalDateTime.class), LocalDateTime.now())
                .set(field("id"), "id")
                .set(field("code"), "code")
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
        firstDelivery.id() == secondDelivery.id()
        firstDelivery.code() == secondDelivery.code()
        firstDelivery.prepareDate() == secondDelivery.prepareDate()
        firstDelivery.deliveryDate() == secondDelivery.deliveryDate()
        firstDelivery.item() != secondDelivery.item()
    }

}
