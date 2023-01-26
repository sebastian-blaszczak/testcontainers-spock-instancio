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
        properties.add("elastic.host", elasticsearch::getHttpHostAddress);
    }

    def "test"() {
        given:
        def item = Instancio.create(Item.class)

        and:
        def savedItem = itemDao.save(item)

        when:
        def itemsFound = itemDao.findByRequest(ItemRequest.builder()
                .name(item.name())
                .description(item.description())
                .ean(item.ean())
                .build()
        )

        then:
        itemsFound.isPresent()
        itemsFound.get().get(0) == savedItem
    }
}
