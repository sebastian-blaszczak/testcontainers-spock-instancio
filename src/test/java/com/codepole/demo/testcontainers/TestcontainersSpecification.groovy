package com.codepole.demo.testcontainers


import org.testcontainers.containers.NginxContainer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.DockerImageName
import spock.lang.Specification

import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Testcontainers
class TestcontainersSpecification extends Specification {

    private NginxContainer nginx = new NginxContainer(DockerImageName.parse("nginx"))

    def "create nginx container"() {
        given:
        def port = nginx.firstMappedPort
        def host = nginx.host
        def getRequest = HttpRequest.newBuilder(URI.create("http://" + host + ":" + port))
                .GET()
                .build()
        def client = HttpClient.newHttpClient()

        when:
        def response = client.send(getRequest, HttpResponse.BodyHandlers.ofString())

        then:
        response.body().contains("Welcome to nginx!")
    }
}