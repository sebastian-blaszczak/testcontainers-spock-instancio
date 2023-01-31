package com.codepole.demo.spock;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

public class MathPowTest {

    @ParameterizedTest
    @CsvSource(
            value = {
                    "2, 3, 8",
                    "5, 2, 25",
                    "6, 3, 216"
            })
    void dataDrivenExponentiationCalculation(Integer base, Integer exponent, Double expected) {
        // expect
        double result = Math.pow(base, exponent);

        // then
        assertThat(result).isEqualTo(expected);
    }
}
