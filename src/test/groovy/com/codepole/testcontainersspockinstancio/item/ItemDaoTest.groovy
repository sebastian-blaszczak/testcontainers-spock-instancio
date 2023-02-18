package com.codepole.testcontainersspockinstancio.item

import org.instancio.Instancio
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource

@SpringBootTest
class ItemDaoTest extends ElasticContainerSpec {

    @Autowired
    ItemDao itemDao

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry properties) {
        properties.add("elastic.host", elasticsearch::getHttpHostAddress)
    }

    def "should save item"() {
        given:
        def item = Instancio.create(Item.class)

        when:
        def savedItem = itemDao.save(item)

        then:
        def foundItems = itemDao.findById(savedItem.id())
        foundItems.isPresent()
        foundItems.get() == savedItem
    }

    def "should find item by partial name"() {
        given:
        def item = Instancio.create(Item.class)
        def savedItem = itemDao.save(item)

        and:
        def query = substring(item.name())

        when:
        def itemsFound = itemDao.findByQuery(new ItemQuery(query))

        then:
        itemsFound.isPresent()
        itemsFound.get().stream()
                .filter { it -> it.id() == savedItem.id() }
                .allMatch { it -> it == savedItem }
    }

    def "should update item"() {
        given:
        def item = Instancio.create(Item.class)
        def savedItem = itemDao.save(item)

        and:
        def newName = "new name"
        def updatedItem = Item.builder()
                .id(savedItem.id())
                .name(newName)
                .ean(savedItem.ean())
                .price(savedItem.price())
                .description(savedItem.description())
                .type(savedItem.type())
                .build()

        when:
        itemDao.save(updatedItem)

        then:
        def foundItems = itemDao.findById(updatedItem.id())
        foundItems.isPresent()
        foundItems.get().name() == newName
    }

    def " should delete item"() {
        given:
        def item = Instancio.create(Item.class)

        and:
        def savedItem = itemDao.save(item)

        when:
        itemDao.delete(savedItem)

        then:
        def foundItems = itemDao.findById(savedItem.id())
        foundItems.isEmpty()
    }

    def substring(String value) {
        value.substring(0, value.length() - 3)
    }
}
