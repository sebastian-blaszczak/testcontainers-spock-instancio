package com.codepole.testcontainersspockinstancio.calculator

import com.codepole.testcontainersspockinstancio.item.ItemDto
import com.codepole.testcontainersspockinstancio.item.ItemType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest(classes = PriceCalculator.class)
class PriceCalculatorSpockTest extends Specification {

    @Autowired
    private PriceCalculator calculator

    def "should calculate price based on input"() {
        given:
        def items = [
                ItemDto.builder()
                        .price(price)
                        .type(type)
                        .build()
        ]

        when:
        def resultPrice = calculator.calculate(items, code)

        then:
        resultPrice.isPresent()
        resultPrice.get() == afterDiscount

        where:
        type           | price | code              | afterDiscount
        ItemType.SHIRT | 50D   | "SPECIAL_CODE_15" | 40D
        ItemType.BELT  | 50D   | "WRONG_CODE"      | 45D
        ItemType.SHIRT | 50D   | "WRONG_CODE"      | 50D
        ItemType.SOCKS | 50D   | ""                | 50D
    }
}
