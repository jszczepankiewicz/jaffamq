## Overview

JaffaMQ is working name for Stomp server messaging implementation written in Akka (Java). It uses new Akka TCP stack (introduced in akka 2.2). It aims to be full Stomp 1.2 complete implementation.

Destinations currently supported:
- non-durable non-persisted topics
- non-persisted queues

Please note: project is not yet compatible with STOMP 1.2 and we do not recommend using it in production.

## Known unimplemented Stomp 1.2 specification features
- Heart-beating
- only acknowledge mode auto is supported

## Known limitations
- due to lack of any persistence layer current transactions are implemented as in memory transactions thus using very large transactions may lead to JVM heap memory problems
- whole project is using default type of akka mailboxes which may lead to JVM heap memory problems in some unfriendly conditions

[STOMP specification static website](http://stomp.github.com/)

## Requirements
- Java 7
- Maven 3

## Build status
[![Build Status](https://travis-ci.org/jszczepankiewicz/jaffamq.png?branch=master)](https://travis-ci.org/jszczepankiewicz/jaffamq)

