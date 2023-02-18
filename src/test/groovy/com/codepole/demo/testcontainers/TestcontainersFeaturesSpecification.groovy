package com.codepole.demo.testcontainers

import org.testcontainers.containers.BindMode
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.NginxContainer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.DockerImageName
import spock.lang.Shared
import spock.lang.Specification

import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Testcontainers
class TestcontainersFeaturesSpecification extends Specification {

    @Shared
    private static GenericContainer nginx
    private static def helloWorldFile = "hello-world.html"
    private static def network = "nginx-network"

    def setupSpec() {
        nginx = new NginxContainer(DockerImageName.parse("nginx"))
                .withNetwork(Network.newNetwork())
                .withNetworkAliases(network)
                .withClasspathResourceMapping(helloWorldFile,
                        "/usr/share/nginx/html/" + helloWorldFile,
                        BindMode.READ_ONLY)
        nginx.start()
    }

    def "check nginx network"() {
        expect:
        nginx.getNetworkAliases().contains(network)
    }

    def "should create nginx container with mounted volume"() {
        given:
        def port = nginx.firstMappedPort
        def host = nginx.host
        def getRequest = HttpRequest.newBuilder(URI.create("http://" + host + ":" + port + "/" + helloWorldFile))
                .GET()
                .build()
        def client = HttpClient.newHttpClient()

        when:
        def response = client.send(getRequest, HttpResponse.BodyHandlers.ofString())

        then:
        response.body().contains("This content is served using Nginx.")
    }

    def "should check nginx container logs"() {
        given:
        def port = nginx.firstMappedPort
        def host = nginx.host
        def getRequest = HttpRequest.newBuilder(URI.create("http://" + host + ":" + port + "/" + helloWorldFile))
                .GET()
                .build()
        def client = HttpClient.newHttpClient()

        and:
        client.send(getRequest, HttpResponse.BodyHandlers.ofString())

        when:
        def logs = nginx.getLogs()

        then:
        logs.contains("GET /hello-world.html")
    }
}
