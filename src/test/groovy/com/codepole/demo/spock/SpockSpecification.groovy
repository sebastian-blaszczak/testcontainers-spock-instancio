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

    def "should calculate power of two numbers"() {
        given:
        def base = 2
        def exponent = 3

        when:
        def result = Math.pow(base, exponent)

        then:
        result == 8
    }

    def "should calculate power of two numbers using data driven approach"() {
        expect:
        result == Math.pow(base, exponent)

        where:
        base | exponent | result
        2    | 3        | 8D
        5    | 2        | 25D
        6    | 3        | 216D
    }

    def "should repository interact once on save"() {
        given:
        Repository repository = Mock()
        def service = new Service(repository)

        when:
        service.saveItem("item")

        then:
        1 * repository.save("item")
    }

    def "should repository interact exact 3 times"() {
        given:
        Repository repository = Mock()
        def service = new Service(repository)

        when:
        service.saveItem("firstItem")
        service.saveItem("secondItem")
        service.saveItem("thirdItem")

        then:
        3 * repository.save(_ as String)
    }

    def "should repository interact between 1 and 3 times"() {
        given:
        Repository repository = Mock()
        def service = new Service(repository)

        when:
        service.saveItem("firstItem")
        service.saveItem("secondItem")
        service.saveItem("thirdItem")

        then:
        (1..3) * repository.save(_ as String)
    }

    def "should repository interact exact 3 times when saving value that ends with Item"() {
        given:
        Repository repository = Mock()
        def service = new Service(repository)

        when:
        service.saveItem("firstItem")
        service.saveItem("secondItem")
        service.saveItem("thirdItem")

        then:
        3 * repository.save({ it.endsWith("Item") })
    }

}
