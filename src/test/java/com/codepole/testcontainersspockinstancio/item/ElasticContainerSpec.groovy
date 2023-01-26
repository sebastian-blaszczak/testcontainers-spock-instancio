package com.codepole.testcontainersspockinstancio.item

import org.testcontainers.elasticsearch.ElasticsearchContainer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.DockerImageName
import spock.lang.Specification

@Testcontainers
class ElasticContainerSpec extends Specification {

    static protected ElasticsearchContainer elasticsearch

    def setupSpec() {
        elasticsearch = new ElasticsearchContainer(DockerImageName
                .parse("docker.elastic.co/elasticsearch/elasticsearch")
                .withTag("7.17.8"))
                .withExposedPorts(9200)
        elasticsearch.start()
    }
}
