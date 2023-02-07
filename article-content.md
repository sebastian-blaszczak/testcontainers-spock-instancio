# Spock, Instancio and Testcontainers - 3 powerful testing tools

Today, automatic tests are an indispensable part of every project (as they always should be). Good test cases can
increase
code quality and creates a feeling, that everything works as it should. They should be made in a fast and reliable
manner, it will encourage developers to run them as often as they can. In a properly configured process, tests should
run every time when something is changed in code, this will create some sort of guarantee, that we've just created
something that actually works. And Last, but not least, test cases give an easy entry point to the code,
especially, when You have to refactor it. To make life a bit easier, there have been created tools to speed up creating
test cases, and here are some of them.

# Spock

## Introduction

Spock is a tool for creating verbose and highly readable test cases. It uses JUnit runner, so it's accessible to
all sorts of editing, building, or CI tools. The best thing is that Spock can be used for every JVM language. This
framework has different terminology from that known in JUnit. Test classes are called `Specifications` and
test methods are called `feature methods`, so to sum up, Spock introduces: "Specifications that describe
features". It also provides an approach to how feature methods should look, using the Given-When-Then pattern:

```groovy
def "exponentiation calculation"() {
    given:
    def base = 2
    def exponent = 3

    when:
    def result = Math.pow(base, exponent)

    then:
    result == 8
}
```

In the above example, we can spot the so-called blocks of the feature method. Spock provides six blocks that could be
used in
there: `given`, `when`, `then`, `expect`, `cleanup`, and `where`. Every block is pretty straightforward:

- given - the block where local setup is done,
- when - block defining test action,
- then - here we can check all the conditions from `when` block,
- expect - acts like when-then block,
- cleanup - a place where we can clean all things, that was created in the feature method,
- where - block for creating data-driven tests

Now, the feature method can be rewritten in another, data-driven way:

```groovy
def "data driven exponentiation calculation"() {
    expect:
    result == Math.pow(base, exponent)

    where:
    base | exponent | result
    2    | 3        | 8
    5    | 2        | 25
    6    | 3        | 216
}
```

The same test can be written in JUnit, using `@ParametrizedTest`, but it is far less readable, and the format of the
method
is not kept by the framework:

```java
@ParameterizedTest
@CsvSource(
        value = {
                "2, 3, 8",
                "5, 2, 25",
                "6, 3, 216"
        })
void dataDrivenExponentiationCalculation(Integer base,Integer exponent,Double expected){
        // expect
        double result=Math.pow(base,exponent);

        // then
        assertThat(result).isEqualTo(expected);
        }
```

## Structure and features

Every specification can be divided into a couple of parts. Not all the parts are required, the final shape depends on
specific case:

- `field part` - place where we can define all the global variables, that can be accessed in every feature method,

- `fixture methods` - the part where a specification is set up. Spock has pre-defined methods:
    - `setup()` - a setup that runs before every feature method - JUnit @Before,
    - `setupSpec()` - a setup that runs before all feature methods - JUnit @BeforeAll,
    - `cleanup()` - cleaning that runs after every feature method - JUnit @After,
    - `cleanupSpec()` - cleaning that runs after all feature methods - JUnit @AfterAll

- `feature methods` - the part where we define the test cases, this is a core of specification. Those methods will be
  used
  to
  check the system behavior,

- `helper methods` - here we can define all the methods that help feature methods to be more readable. It is also
  common to extract code that is duplicated across the feature methods.

Framework provides a variety of tools that can be used in mocking and interaction-based testing, without any additional
libraries. Here is an example:

```java

@Getter
@AllArgsConstructor
public class ListWrapper {

    private List<String> value;

    public void add(String item) {
        value.add(item);
    }
}
```

Now we can create a feature method that mocks nested lists and check how many times the method `add` is invoked:

```groovy
 def "interaction based test"() {
    given:
    List<String> list = Mock()
    def wrapper = new ListWrapper(list)

    when:
    wrapper.add("item")

    then:
    1 * list.add("item")
}
```

We can go even further and creates a variety of use cases:

```groovy
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
    // or
    (1..3) * list.add(_ as String)
    // or
    3 * list.add({ it.endsWith("Item") })
}
```

This example shows, how we can handle conditions of invocation. Character `_` means any value. Sometimes it is needed to
specify of type, then we have to use the keyword `as`. We can specify an inclusive range of invocation using round
brackets.
It is also possible to specify custom conditions, and how the input of a method should look like. One note here, in the
example above
Spock removes invocation after each usage, so we can't use all showed conditions in one feature method.

### Why You should use it

This framework provides all the features that can be found in JUnit, but it delivers in a more accessible and readable
way. It works perfectly in data-driven tests, and it runs straight away in projects that are written in one of JVM
languages. For more details check Spock [documentation](https://spockframework.org/spock/docs/2.3/all_in_one.html).

# Instancio

### Introduction

One of the first things, while creating tests, is preparing data. This part is crucial because, without it, we can't
test
anything. Unfortunately, this part creates, a lot of boilerplate code. It is common to see a huge amount of helper
methods or even classes that are designed only for providing a generated object for test purposes. Consider these two
classes:

```java
public record Item(String id,
                   String name,
                   String ean,
                   Double price,
                   String description,
                   ItemType type) {
}
```

```java
record DeliveryItem(String id,
                    String code,
                    LocalDateTime prepareDate,
                    LocalDateTime deliveryDate,
                    Item item) {
}
```

Let's create a simple test that only creates `DeliveryItem` object:

```groovy
def "create delivery item"() {
    given:
    def delivery = createDeliveryItem()

    expect:
    delivery.id() != null
    delivery.code() != null
    delivery.prepareDate() != null
    delivery.deliveryDate() != null
    delivery.item() != null
}
```

This is fine, isn't it? but wait, let's see how method `createDeliveryItem` looks like:

```groovy
private def createDeliveryItem() {
    return new DeliveryItem(
            RandomStringUtils.randomAlphabetic(5),
            RandomStringUtils.randomAlphabetic(5),
            LocalDateTime.now(),
            LocalDateTime.now(),
            createItem()
    )
}

private def createItem() {
    return new Item(
            RandomStringUtils.randomAlphabetic(5),
            RandomStringUtils.randomAlphabetic(5),
            RandomStringUtils.randomAlphabetic(5),
            random.nextDouble(),
            RandomStringUtils.randomAlphabetic(5),
            generateType()
    )
}

private def generateType() {
    def typeIndex = random.nextInt(0, ItemType.values().size())
    return List.of(ItemType.values()).get(typeIndex)
}

private final ThreadLocalRandom random = ThreadLocalRandom.current()
```

In this case, it is required to create three helper methods, that only create one test object. Now, let's imagine
that `Item` have to have another field called `category` and the field `DeliveryItem` should have `address`. This
triggers changes
in those 2 helper methods and probably will create another one. Instancio library was created, to handle this sort of
problem.
For instance, let's see how this test will look like when the power of Instancio is used:

```groovy
def "create delivery item"() {
    given:
    def delivery = Instancio.create(DeliveryItem.class)

    expect:
    delivery.id() != null
    delivery.code() != null
    delivery.prepareDate() != null
    delivery.deliveryDate() != null
    delivery.item() != null
}
```

Looks similar, right? The main difference is that the helper methods have gone. The best thing is, if
something will change in domain classes, it will not break the test case.

### Features

There are a lot of ways to create objects using this library, this section will be provided some features, that
might be useful in any project.

The simplest way to build an object is to use `create` static method. This allows us to create a filled object with
randomly generated data. Only thing is to provide a type class as a method parameter and Instancio will do the magic for
us:

```groovy
def item = Instancio.create(Item.class)
```

Instancio brings a lot of possibilities to manipulate data. One of the methods is `set`, in the example below, Instancio
will set all `String` fields (also in nested objects) as `string-value`. The documentation says "The
allXxx() methods such as allInts(), are available for all core types.", it has a lot of predefined methods:

```groovy
def stringValues = Instancio.of(Item.class)
        .set(allStrings(), "string-value")
        .create()
```

The library provides also the ability to sets only a specific field:

```groovy
def withField = Instancio.of(Item.class)
        .set(field("description"), "desc")
        .create()
```

We can define a custom generate algorithm using `generate` method. For instance a number in the range of 1-50:

```groovy
def generatedValue = Instancio.of(Item.class)
        .generate(allDoubles(), generator -> generator.doubles().range(1D, 50D))
        .create()
```

In the library, we can find two similar methods `set` and `supply`. The `set` method allows setting the static value to
the
object. On the other hand, `supply` method will create a new instance of value when setting fields. So in the example
below, all strings will
have the same value `same-string`, but all the dates will differ from each other:

```groovy
def setSupply = Instancio.of(DeliveryItem.class)
        .set(allStrings(), "same-string")
        .supply(all(LocalDateTime.class), (Supplier) (() -> LocalDateTime.now()))
        .create()
```

Instancio gives a powerful tool called `model`. It is a convenient way of defining objects that have a common core, so
that
can be defined in one place and re-used for all other objects:

```groovy
def model = Instancio.of(DeliveryItem.class)
        .set(all(LocalDateTime.class), LocalDateTime.now())
        .set(field("id"), "id")
        .set(field("code"), "code")
        .toModel()
```

Using the model defined above, Instancio allows the creation two objects with the same delivery fields and different
items objects:

```groovy
def firstDelivery = Instancio.of(model)
        .set(field("item"), Instancio.create(Item.class))
        .create()

def secondDelivery = Instancio.of(model)
        .set(field("item"), Instancio.create(Item.class))
        .create()
```

### Why You should use it

The best code to maintain is the one that not exists. This library helps reduce boilerplate code to an absolute minimum
and makes the process of creating test data, fast and simple. If Your project has a lot of useless code, Instancio is
one of
the quick solutions for it. It also provides a variety of ways for creating an object, with custom modes of generating
values.
For more details check Instancio [documentation](https://www.instancio.org/user-guide/).

# Testcontainers

### Introduction

Have You ever had problems with spinning up the Docker container in Your CI pipeline or ever wonder how could You
simplify
that process? The answer to Your problem is Testcontainers. This library can encapsulate everything related to the
Docker
containers in Your test cases! It brings integration tests to another level, from now You don't have to mock services
or use in-memory databases (such as H2 - which, in some cases, don't have all the features). It has a lot of use cases
such as:

- Providing real data layer,
- Allows running acceptance tests involving containerized web browsers,
- Spinning up cloud environment - LocalStack(AWS), Azurite or GCP emulators,
- Delivering an easy way to test Dockerized microservices,
- Anything You can think of - that has a Docker container

### Features

Testcontainers have already prepared some pre-defined modules, that You can use out of the box e.g.
[mongoDB](https://www.testcontainers.org/modules/databases/mongodb/). There are a lot more prepared modules,
to check all of them, You can go to [Testcontainers website](https://www.testcontainers.org/) and open `Modules`
section.
One note, to use this library make sure, that the Docker service is up and running! Let's now spin some containers:

```groovy
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
```

This is all it takes to spin up a Docker container. The test specification above uses a predefined module of Nginx,
which
simplify the process of setting up a container. Testcontainers provide all kinds of features that Docker has, so if
there is a
need of creating a custom network or mounting volumes, the library provides all:

```groovy
new NginxContainer(DockerImageName.parse("nginx"))
        .withNetwork(Network.newNetwork())
        .withNetworkAliases("nginx-network")
        .withClasspathResourceMapping("hello-world.html",
                "/usr/share/nginx/html/hello-world.html",
                BindMode.READ_ONLY)
```

Using the above container configuration, Testcontainers will create nginx server, with a custom network
called `nginx-network`
and mount `hello-world.html` into a static Nginx repository. It is possible to check all of that:

```groovy
def "check nginx network"() {
    expect:
    nginx.getNetworkAliases().contains("nginx-network")
}
```

Nginx container should contain static resource on path `/hello-world.html`, let's check that also:

```groovy
def helloWorldFile = "hello-world.html"

def "create nginx container with mounted volume"() {
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
```

Library provides a possibility to check, what is going on inside the container. This can be done using the container log
feature,
it could be very helpful when using custom containerized services - like, the ones, that were created as microservice in
our project:

```groovy
def helloWorldFile = "hello-world.html"

def "check nginx container logs"() {
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
```

### Why You should use it

This library was created for providing an easy and fast way to integrate with containers in test cases. It works
perfectly, with all kinds of data layers (like DB) and external services. The main principle here is to be as close to
the production environment as possible. It could detect bugs on the development level. Testcontainers create
opportunities to discover
new things, and it simplifies the way of experimenting with them. The main problem here is time. It takes a while to
spin up a
container, and even more when we don't have the downloaded Docker image that we want to use (it has to download
first). It could be beneficial to create test cases, that can re-use running containers. Another thing is to
pre-download
Docker image. Despite that, this is a powerful tool that can rise the quality of Your code. For more details check
Testcontainers [documentation](https://www.testcontainers.org/quickstart/spock_quickstart/).

# Real world example

Now, let's see how we can combine those 3 tools and use them in some real-world example. Assume that we have a
clothing shop and the owners want to create an application for it. One of the features that need to be done is a search
engine
for products. It should have the ability to quickly find items using a full-text search. The suitable solution here
would be Elasticsearch.
The client also wants to create some discounts with different politics e.g. using code or item-based discounts.
If there are two applicable discounts, the service should take the one with the lowest price. Let's create some code for
those requirements:

```java

@Builder
@Document(indexName = "item")
public record Item(@Id String id,
                   String name,
                   String ean,
                   Double price,
                   String description,
                   ItemType type) {
}

public enum ItemType {
    T_SHIRT, SHIRT, TROUSERS, BELT, SOCKS
}
```

We have just described how an item should look like, now let's see how discounts may be defined:

```java
public interface Discount {

    Double calculateDiscount(List<ItemDto> items, String code);

    default Double noDiscount(List<ItemDto> items) {
        return items.stream()
                .map(ItemDto::price)
                .reduce(Double::sum)
                .orElse(0D);
    }

    default Double percentageMultiplier(Double discount) {
        return Optional.of(discount)
                .filter(value -> value < 100)
                .map(value -> (100 - value) / 100)
                .orElseThrow(IllegalArgumentException::new);
    }

    default Double priceWithDiscount(List<ItemDto> items, Double discount) {
        return noDiscount(items) * percentageMultiplier(discount);
    }
}

@RequiredArgsConstructor
public class CodeDiscount implements Discount {

    private final String discountCode;
    private final Double discount;

    @Override
    public Double calculateDiscount(List<ItemDto> items, String code) {
        return codeMatches(code) ? priceWithDiscount(items, discount) : noDiscount(items);
    }

    private boolean codeMatches(String code) {
        return code.equals(discountCode);
    }
}

@RequiredArgsConstructor
public class SpecialItemTypeDiscount implements Discount {

    private final ItemType type;
    private final Double discount;

    @Override
    public Double calculateDiscount(List<ItemDto> items, String code) {
        return hasType(items, type) ? priceWithDiscount(items, discount) : noDiscount(items);
    }

    private boolean hasType(List<ItemDto> items, ItemType type) {
        return items.stream()
                .map(ItemDto::type)
                .anyMatch(itemType -> itemType == type);
    }

}
```

Everything is set up, so now we can move to actual testing. Our goal is to create a similar test environment to the
production one, so
we won't be satisfied using some in-memory database for search testing. In this case, Testcontainers becomes handy and
on top of that, we don't have to create boilerplate code only for test purposes, so we can use Instancio in that matter:

```groovy
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

@SpringBootTest
class ItemDaoTest extends ElasticContainerSpec {

    @Autowired
    ItemDao itemDao

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry properties) {
        properties.add("elastic.host", elasticsearch::getHttpHostAddress)
    }

    def "should properly find item by partial name"() {
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
}
```

In the example above, we can see that the Elasticsearch container configuration can be extracted to another class, so
specifications could be even more readable and free of unnecessary distractions. We are using the power of Instancio
to create a filled entity and Testcontainers give us the possibility to test against an actual Elasticsearch instance.
To see more examples You can
check [this](https://github.com/sebastian-blaszczak/testcontainers-spock-instancio/blob/master/src/test/groovy/com/codepole/testcontainersspockinstancio/item/ItemDaoTest.groovy).
Now, we have confirmation that data layers work as it should, let's test discount logic:

```groovy
@SpringBootTest(classes = PriceCalculator.class)
class PriceCalculatorSpockTest extends Specification {

    @Autowired
    private PriceCalculator calculator

    def "should properly calculate price based on input"() {
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
```

To comparison here is the same test case written in JUnit:

```java

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
    void shouldProperlyCalculatePrice(ItemType type, Double price, String code, Double afterDiscount) {
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
```

As You can see, the data that is used in this test case is not clear, and adding a new portion of data can be tricky.
All
parameters
are `Strings`, so we can pass there anything e.g. `TSHIRT` instead of `T_SHIRT`. The other problem is the structure of,
JUit
don't track any blocks, this is only the goodwill of the developer to keep tests clean in the Given-When-Then way.

# Conclusion

In this article, we have shown three powerful tools that can leverage code testing to another level. Spock framework
that
was created to be more verbose and readable, Instancio that can speed up the preparation of tests
and Testcontainers which in some integrations cases is irreplaceable. Those libraries can improve readability and make
test
process a bit less boring. It is also shown that those tools can be used in a real-life project, so why wait and give
them a spin in Your next project? All examples and code for the project You can find in
this [repository](https://github.com/sebastian-blaszczak/testcontainers-spock-instancio).

Happy coding!

# Source

- https://spockframework.org/spock/docs/2.3/all_in_one.html
- https://www.instancio.org/user-guide/
- https://www.testcontainers.org/quickstart/spock_quickstart/