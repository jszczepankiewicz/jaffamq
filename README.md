### JaffaMQ Stomp messaging broker
---
##### build status: [![Build Status](https://travis-ci.org/jszczepankiewicz/jaffamq.png?branch=master)](https://travis-ci.org/jszczepankiewicz/jaffamq)
---

JaffaMQ is working name for Stomp server messaging implementation written in Akka (Java). It uses new Akka TCP stack (introduced in akka 2.2).

Destinations currently supported:
- non-durable non-persisted topics
- queues (with non-persisted message if subscribers already connected, but unconsumed messages are stored in persisted filesystem journal for future consumption)

Please note: project is not yet compatible with STOMP 1.2 and we do not recommend using it in production.
### Implementation notes
Broker is implemented in Scala (used in REST container) and Java for the rest. It is build using Maven. Scala modules are build using scala maven plugin.
For persistence of incoming messages fast journaling is used. For configuration H2 embedded database is used.

### Limitations

##### Known unimplemented yet Stomp 1.2 specification features
- Heart-beating
- only acknowledge mode auto is supported

[STOMP specification static website](http://stomp.github.com/)

##### Implementation limitations
- current transactions are implemented as in memory transactions thus using very large uncommited transactions may lead to heap allocations above JVM limits
- whole project is using default type of akka mailboxes (unbounded in memory) which may lead to JVM heap memory problems in some unfriendly conditions

### Requirements to compile / build / test
- Java 7 (tested on OpenJDK 7 and Oracle JDK 7).
- Maven 3

### Testing and building

To run tests run command:

`$ mvn verify`

No packaging option is available yet.

### Credits

[Akka toolkit](http://http://akka.io/)

[H2 Database Engine](http://http://www.h2database.com/)

[The Scala Programming Language](http://www.scala-lang.org/)

[Journal.IO journal storage implementation](https://github.com/sbtourist/Journal.IO)

and others...
