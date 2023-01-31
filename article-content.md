# Spock, Instancio and Testcontainers, 3 powerful testing tools

Today, automatic tests are indispensable part of every project (as it should always be). Good test cases can increase
code quality and creates feeling that every thing works as it should be. They should be make in fast and reliable
manner, it will encourage developers to run them as often as they can. In properly configured process, tests should
run every time when something is changed in code, this will create some sort of guarantee, that we've just created
something that actually works, as we expected. And Last, but no least, test cases give easy entry point to new code,
especially when You have to refactor it. To make life a bit easier, there have been created tools to speed up creating
test cases and here are some of them.

# Spock

## Introduction

Spock is tool for creating verbose and highly readable test cases. It uses JUnit runner, so it's accessible to
all sorts of editing, building or CI tools. The best thing is that, Spock can be used for every JVM language. This
framework has, a bit of different terminology from that are known in JUnit. Test classes are call `Specifications` and
test methods are called `feature methods`, so to sum up everything, Spock introduces: "Specifications that describes
features". It also provides an approach how feature methods should look like using Given-When-Then pattern:

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

In above example we can spot so-called blocks of feature method. Spock provides six blocks that could be used in feature
methods: `given`, `when`, `then`, `expect`, `cleanup`, and `where`. Every block is pretty straightforward:

- given - block where local set up is done,
- when - block defining test action,
- then - here we can check all the conditions from when block,
- expect - it acts like when-then block,
- cleanup - place when we can clean all things that was created in feature method,
- where - block for creating data driven tests

Now, feature method can be rewritten in another, data-driven way:

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

Same test can be written in JUnit, using `@ParametrizedTest`, but it is far less readable and format of method is not
kept by framework:

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

- `field part` - place when we can define all the global variables that can be accessed in every feature method,

- `fixture methods` - part where specification is set up. Here we can use:
    - `setup()` - setup that runs before every feature method - JUnit @Before,
    - `setupSpec()` - setup that runs before all feature methods - JUnit @BeforeAll,
    - `cleanup()` - clean methods that runs after every feature method - JUnit @After,
    - `cleanupSpec()` - clean method that runs after all feature methods - JUnit @AfterAll

- `feature methods` - part when we define the test cases, this is a core of specification. Those methods will be used to
  check the system
  behavior,

- `helper methods` - here we can define all the methods that helps feature methods to be more readable. It is also
  common to extract here
  code that is duplicated across the feature methods.

Framework provides variety of tools that can be used in mocking and interaction based testing without any additional
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

Now we can create feature method that mocks nested list and check how many times method `add` will be invoked:

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

We can go event further and creates a variety of use cases:

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

This example shows, how we can handle conditions of invocation. Character `_` means any value, sometime it is needed to
specify of type, then we have to use key word `as`. We can specify inclusive range of invocation using round backers. We
can also specify custom condition how input of a method should look like. One note here, in example above Spock removes
invocation after each usage, so we can't use all conditions in one feature
method.

### Why You should use it

This framework provides all the features that can be found in JUnit, but it delivers in more accessible and readable
way. It works perfectly in data driven tests, and it works straight away in projects that are written in one of JVM
language. For more details check Spock [documentation](https://spockframework.org/spock/docs/2.3/all_in_one.html).

# Instancio

### Itroduction

One of the first thing, while creating tests, is preparing data. This part is crucial, because without it, we can't test
anything. Unfortunately this huge part creates, a lot of boilerplate code. It is common to see huge amount of help
methods or even classes that are designed only for providing generated object for test purposes. Consider this two
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

and

```java
record DeliveryItem(String id,
                    String code,
                    LocalDateTime prepareDate,
                    LocalDateTime deliveryDate,
                    Item item) {
}
```

Let's create simple test that only creates `DeliveryItem` object:

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

This looks alright, isn't it ? but wait, let's see how metod `createDeliveryItem` looks like.

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

In this case, it is required to create three help methods, that only creates one test object. Now, let's image
that `Item` needs to have another field called `category` and `DeliveryItem` should have `address`. This triggers
changes
in those 2 help methods and probably will create another one. To handle this sort of problems, Instancio library was
created. To make and example, let's see how this test will look like, when the power of Instancio is used.

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

Looks similar, right ? The main difference is that, the help methods not exists in this approach. The best thing is, if
something will change in those classes, it will not break the test case.

### Features

There is a lot of ways to create objects using this library, in this section will be provided some features, that
might be useful in any project.

The simplest way to build an object, is to use `create` static method. This allows as to create fully filled object with
randomly generated data. Only thing is to provide type class as method parameter and Insatancio will do the magic for
us:

```groovy
def item = Instancio.create(Item.class)
```

Instancio gives a lot of possibilities to manipulate data. One of the methods is `set`, in example below, Instancio
will set all `String` fields (also in nested objects) as `string-value`. Best thing is that, documentation says "The
allXxx() methods such as allInts(), are available for all core types.":

```groovy
def stringValues = Instancio.of(Item.class)
        .set(allStrings(), "string-value")
        .create()
```

Library provides also ability to sets only a specific field:

```groovy
def withField = Instancio.of(Item.class)
        .set(field("description"), "desc")
        .create()
```

Using `generate` method it is possible to provide, own generate algorithm, for example number from range 1-50:

```groovy
def generatedValue = Instancio.of(Item.class)
        .generate(allDoubles(), generator -> generator.doubles().range(1D, 50D))
        .create()
```

In library, we can find two similar methods `set` and `supply`. The `set` method allows setting static value to the
object. The `supply` method will create new instance of value when setting fields. So in example below, all string will
have value `same-string`, but all the dates will differ from each-other:

```groovy
def setSupply = Instancio.of(DeliveryItem.class)
        .set(allStrings(), "same-string")
        .supply(all(LocalDateTime.class), (Supplier) (() -> LocalDateTime.now()))
        .create()
```

Instancio gives powerful tool called `model`. It is a convenient way of defining objects that have common core, so that
can be defined in one place and re-used for all other objects:

```groovy
def model = Instancio.of(DeliveryItem.class)
        .set(all(LocalDateTime.class), LocalDateTime.now())
        .set(field("id"), "id")
        .set(field("code"), "code")
        .toModel()
```

Using model defined above, Instancio allows to create two object with same delivery fields and different items objects:

```groovy
def firstDelivery = Instancio.of(model)
        .set(field("item"), Instancio.create(Item.class))
        .create()

def secondDelivery = Instancio.of(model)
        .set(field("item"), Instancio.create(Item.class))
        .create()
```

### Why You should use it

The best code to maintain is the one that not exists. This library helps reduce boilerplate code to absolute minimum
and creating test data become fast and simple. If Your project has this problem, Instancio is one of the quick
solutions for it. It also provides variety of ways for creating object, with custom modes of generating values. For more
details check Instancio [documentation](https://www.instancio.org/user-guide/).

# Testcontainers

TODO

# Real world example

TODO

# Conclusion

In this article we have shown three powerful tools that can leverage code testing to another level. Spock framework that
was created to be as close to verbose test case setup as possible, Instancio that can speed up preparation of test
proces
and Testcontainers which in some integrations cases is irreplaceable. Those libraries can improve readability and make
test
proces a bit less boring. We also have shown that those tools can be used in real life project, so don't wait and give
them a spin in Your next project.

Happy coding!