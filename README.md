#  Java Uuid Generator (JUG)

JUG is a set of Java classes for generating UUIDs. It generates UUIDs according to the UUID specification (IETF draft), found  (for example) at:

    http://www1.ics.uci.edu/~ejw/authoring/uuid-guid/draft-leach-uuids-guids-01.txt

or at [Wikipedia](http://en.wikipedia.org/wiki/UUID) (draft id being "&lt;draft-leach-uuids-guids-01.txt>")

Alternatively you can also read [newer IETF draft](http://www.ietf.org/internet-drafts/draft-mealling-uuid-urn-00.txt)
 that describes URN name space for UUIDs, and also contains UUID definition.

JUG was written by Tatu Saloranta (<tatu.saloranta@iki.fi>) in 2002 (or so?), and has been updated over years.
In addition, many other individuals have helped fix bugs and implement new features: please see CREDITS for the complete list.

Jug licensing is explained in file LICENSE; basically you have a choice of one of 2 common Open Source licenses (when downloading source package) -- Apache License 2.0 or GNU LGPL 2.1 -- and you will need to accept terms for one of the license.
Please read LICENSE to understand requirements of the license you choose.

## Usage

JUG can be used as a command-line tool (via class 'com.fasterxml.uuid.Jug`), or as a pluggable component.
Maven coordinates are:

    <dependency>
      <groupId>com.fasterxml.uuid</groupId>
      <artifactId>java-uuid-generator</artifactId>
      <version>3.1.3</version>
    </dependency>

