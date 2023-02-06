package com.codepole.demo.spock

import spock.lang.Shared
import spock.lang.Specification

class SpockSpecification extends Specification {

    @Shared
    List<String> names

    List<Integer> numbers

    def setupSpec() {
        names = ["Aragorn", "Legolas", "Gimli"]
    }

    def setup() {
        numbers = [0, 1, 1, 2, 3]
    }

    def "exponentiation calculation"() {
        given:
        def base = 2
        def exponent = 3

        when:
        def result = Math.pow(base, exponent)

        then:
        result == 8
    }

    def "data driven exponentiation calculation"() {
        expect:
        result == Math.pow(base, exponent)

        where:
        base | exponent | result
        2    | 3        | 8D
        5    | 2        | 25D
        6    | 3        | 216D
    }

    def "interaction based test"() {
        given:
        List<String> list = Mock()
        def wrapper = new ListWrapper(list)

        when:
        wrapper.add("item")

        then:
        1 * list.add("item")
    }

    def "multiple interaction with wrapper"() {
        given:
        List<String> list = Mock()
        def wrapper = new ListWrapper(list)

        when:
        wrapper.add("firstItem")
        wrapper.add("secondItem")
        wrapper.add("thirdItem")

        then:
        3 * list.add(_ as String)
//        (1..3) * list.add(_ as String)
//        3 * list.add({ it.endsWith("Item") })
    }

}
