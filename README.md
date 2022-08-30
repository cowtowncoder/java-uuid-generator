#  Java Uuid Generator (JUG)

JUG is a set of Java classes for working with UUIDs: generating UUIDs using any of standard methods, outputting
efficiently, sorting and so on.
It generates UUIDs according to the [UUID specification (RFC-4122)](https://tools.ietf.org/html/rfc4122)
(also see [Wikipedia UUID page](http://en.wikipedia.org/wiki/UUID) for more explanation)

JUG was written by Tatu Saloranta (<tatu.saloranta@iki.fi>) originally in 2002 and has been updated over the years.
In addition, many other individuals have helped fix bugs and implement new features: please see `release-notes/CREDITS` for the complete list.

JUG is licensed under [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).

## Status

| Type | Status |
| ---- | ------ |
| Build (CI) | [![Build (github)](https://github.com/cowtowncoder/java-uuid-generator/actions/workflows/main.yml/badge.svg)](https://github.com/cowtowncoder/java-uuid-generator/actions/workflows/main.yml) |
| Artifact |  [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.fasterxml.uuid/java-uuid-generator/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.fasterxml.uuid/java-uuid-generator/) |
| OSS Sponsorship | None yet |
| Javadocs | [![Javadoc](https://javadoc.io/badge/com.fasterxml.uuid/java-uuid-generator.svg)](http://www.javadoc.io/doc/com.fasterxml.uuid/java-uuid-generator)
| Code coverage (6.x) | [![codecov.io](https://codecov.io/github/cowtowncoder/java-uuid-generator/coverage.svg?branch=master)](https://codecov.io/github/cowtowncoder/java-uuid-generator?branch=master) |
| CodeQ (LGTM.com) | [![LGTM alerts](https://img.shields.io/lgtm/alerts/g/cowtowncoder/java-uuid-generator.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/cowtowncoder/java-uuid-generator/alerts/) [![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/cowtowncoder/java-uuid-generator.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/cowtowncoder/java-uuid-generator/context:java) |

## Usage

JUG can be used as a command-line tool (via class `com.fasterxml.uuid.Jug`),
or as a pluggable component.

### Maven Dependency

Maven coordinates are:

```xml
<dependency>
  <groupId>com.fasterxml.uuid</groupId>
  <artifactId>java-uuid-generator</artifactId>
  <version>4.0.1</version>
</dependency>
```

#### Third-party Dependencies by JUG

The only dependency for JUG is the logging library:

* For versions up to 3.x, `log4j` is used, optionally (runtime dependency)
* For versions 4.x and up, `slf4j` API is used: logging implementation to be provided by calling application

### JDK9+ module info

Since version `3.2.0`, JUG defines JDK9+ compatible `module-info.class`, with module name of `com.fasterxml.uuid`.

### Downloads

For direct downloads, check out [Project Wiki](../../wiki).

### Using JUG as Library

#### Generating UUIDs

The original use case for JUG was generation of UUID values. This is done by first selecting a kind of generator to use, and then calling its `generate()` method.
For example:

```java
UUID uuid = Generators.randomBasedGenerator().generate();
UUID uuid = Generators.timeBasedGenerator().generate();
```

If you want customize generators, you may also just want to hold on to generator instance:

```java
TimeBasedGenerator gen = Generators.timeBasedGenerator(EthernetAddress.fromInterface());
UUID uuid = gen.generate();
UUID anotherUuid = gen.generate();
```

If your machine has a standard IP networking setup, the `Generators.egressTimeBasedGenerator` (added in JUG 4.1) 
factory method will try to determine which network interface corresponds to the default route for 
all outgoing network traffic, and use that for creating a time based generator.
This is likely a good choice for common usage scenarios if you want a version 1 UUID generator, but unfortunately
is known not to work reliably on some platforms (MacOS seems to have some issues).

```java
TimeBasedGenerator gen = Generators.egressTimeBasedGenerator();
UUID uuid = gen.generate();
UUID anotherUuid = gen.generate();
```

Generators are fully thread-safe, so a single instance may be shared among multiple threads.

Javadocs for further information can be found from [Project Wiki](../../wiki).

#### Converting `java.util.UUID` values into byte[]

Sometimes you may want to convert from `java.util.UUID` into external serialization:
for example, as `String`s or byte arrays (`byte[]`).
Conversion to `String` is easy with `UUID.toString()` (provided by JDK), but there is no similar functionality for converting into `byte[]`.

But `UUIDUtil` class provides methods for efficient conversions:

```
byte[] asBytes = UUIDUtil.asByteArray(uuid);
// or if you have longer buffer already
byte[] outputBuffer = new byte[1000];
// append at position #100
UUIDUtil.toByteArray(uuid, outputBuffer, 100);
```

#### Constructing `java.util.UUID` values from String, byte[]

`UUID` values are often passed as java `String`s or `byte[]`s (byte arrays),
and conversion is needed to get to actual `java.util.UUID` instances.
JUG has optimized conversion functionality available via class `UUIDUtil` (package
`com.fasterxml.uuid.impl`), used as follows:

```
UUID uuidFromStr = UUIDUtil.uuid("ebb8e8fe-b1b1-11d7-8adb-00b0d078fa18");
byte[] rawUuidBytes = ...; // byte array with 16 bytes
UUID uuidFromBytes = UUIDUtil.uuid(rawUuidBytes)
```

Note that while JDK has functionality for constructing `UUID` from `String`, like so:

```
UUID uuidFromStr = UUID.fromString("ebb8e8fe-b1b1-11d7-8adb-00b0d078fa18");
```

it is rather slower than JUG version: for more information, read
[Measuring performance of Java UUID.fromString()](https://cowtowncoder.medium.com/measuring-performance-of-java-uuid-fromstring-or-lack-thereof-d16a910fa32a).

### Using JUG as CLI

JUG jar built under `target/`:

```
target/java-uuid-generator-4.1.1-SNAPSHOT.jar
```

can also be used as a simple Command-line UUID generation tool.

To see usage you can do something like:

    java -jar target/java-uuid-generator-4.1.1-SNAPSHOT.jar

and get full instructions, but to generate 5 Random-based UUIDs, you would use:

    java -jar target/java-uuid-generator-4.1.1-SNAPSHOT.jar -c 5 r

(where `-c` (or `--count`) means number of UUIDs to generate, and `r` means Random-based variant)

NOTE: this functionality is included as of JUG 4.1 -- with earlier versions you would need a bit longer invocation as Jar metadata did not specify "Main-Class".
If so, you would need to use

    java -cp target/java-uuid-generator-4.1.1-SNAPSHOT.jar com.fasterxml.uuid.Jug -c 5 r

## Compatibility

JUG versions 3.1 and later require JDK 1.6 to work, mostly to be able to access local Ethernet MAC address.
Earlier versions (3.0 and before) worked on 1.4 (which introduced `java.util.UUID`).

## Known Issues

JDK's `java.util.UUID` has flawed implementation of `compareTo()`, which uses naive comparison
of 64-bit values. This does NOT work as expected, given that underlying content is for all purposes
unsigned. For example two UUIDs:

```
7f905a0b-bb6e-11e3-9e8f-000000000000
8028f08c-bb6e-11e3-9e8f-000000000000
```

would be ordered with second one first, due to sign extension (second value is considered to
be negative, and hence "smaller").

Because of this, you should always use external comparator, such as
`com.fasterxml.uuid.UUIDComparator`, which implements expected sorting order that is simple
unsigned sorting, which is also same as lexicographic (alphabetic) sorting of UUIDs (when
assuming uniform capitalization).

## Enterprise support

Available as part of the Tidelift Subscription.

The maintainers of `java-uuid-generator` and thousands of other packages are working with Tidelift to deliver commercial support and maintenance for the open source dependencies you use to build your applications. Save time, reduce risk, and improve code health, while paying the maintainers of the exact dependencies you use. [Learn more.](https://tidelift.com/subscription/pkg/maven-com-fasterxml-uuid-java-uuid-generator?utm_source=maven-com-fasterxml-uuid-java-uuid-generator&utm_medium=referral&utm_campaign=enterprise&utm_term=repo)

## Contributing

For simple bug reports and fixes, and feature requests, please simply use projects
[Issue Tracker](../../issues), with exception of security-related issues for which
we recommend filing
[Tidelift security contact](https://tidelift.com/security) (NOTE: you do NOT have to be
a subscriber to do this).

## Alternative JVM UUID generators

There are many other publicly available UUID generators. For example:

* [Apache Commons IO](http://commons.apache.org/sandbox/commons-id/uuid.html) has UUID generator
* [eaio-uuid](http://stephenc.github.io/eaio-uuid/)
* JDK has included `java.util.UUID` since 1.4, but omits generation methods (esp. time/location based ones), has sub-standard performance for many operations and implements comparison in useless way
* [ohannburkard.de UUID generator](http://johannburkard.de/software/uuid/)

Note that although some packages claim to be faster than others, it is not clear whether:

1. Claims have been properly verified (or, if they have, can be independently verified), AND
2. It is not likely that performance differences truly matter: JUG, for example, can generate a millions of UUID per second per core (sometimes hitting the theoretical limit of 10 million per second) -- and it seems unlikely that generation will be bottleneck for about any use case

so it is often best to choose based on stability of packages and API.
