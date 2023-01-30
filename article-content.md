# Spock, Instancio and Testcontainers, 3 powerful testing tools

Today, automatic tests are indispensable part of every project (as it should always be). Good test cases can increase
code quality and creates feeling that every thing works as it should be. They should be make in fast and reliable
manner, it will encourage developers to run them as often as they can. In properly configured process, tests should
run every time when something is changed in code, this will create some sort of guarantee, that we've just created
something that actually works, as we expected. And Last, but no least, test cases give easy entry point to new code,
especially when You have to refactor it. To make life a bit easier, there have been created tools to speed up creating
test cases and here are some of them.

# Spock

# Instancio

### What ?

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

### How ?

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

### Why ?

The best code to maintain is the one that not exists. This library helps reduce boilerplate code to absolute minimum
and creating test data become fast and simple. If Your project has this problem, Instancio is one of the quick
solutions for it. It also provides variety of ways for creating object, with custom modes of generating values. For more
details check Instancio (documentation)[https://www.instancio.org/user-guide/].