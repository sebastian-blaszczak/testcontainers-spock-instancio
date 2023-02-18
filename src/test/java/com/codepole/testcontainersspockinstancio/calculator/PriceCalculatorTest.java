package com.codepole.testcontainersspockinstancio.calculator;

import com.codepole.testcontainersspockinstancio.item.ItemDto;
import com.codepole.testcontainersspockinstancio.item.ItemType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = PriceCalculator.class)
class PriceCalculatorTest {

    @Autowired
    private PriceCalculator calculator;

    @ParameterizedTest
    @CsvSource(
            value = {
                    "SHIRT, 50, SPECIAL_CODE_15, 40",
                    "BELT, 50, WRONG_CODE, 45",
                    "SHIRT, 50, WRONG_CODE, 50",
                    "SOCKS, 50, {}, 50",
            },
            emptyValue = "{}")
    void shouldCalculatePrice(ItemType type, Double price, String code, Double afterDiscount) {
        // given
        List<ItemDto> items = List.of(ItemDto.builder()
                .price(price)
                .type(type)
                .build());

        // when
        Optional<Double> resultPrice = calculator.calculate(items, code);

        // then
        assertThat(resultPrice).isPresent().contains(afterDiscount);
    }

}