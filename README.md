# Simple Core PDML Parser Written in Java

## Overview

This repository contains the source code of a simple Core PDML recursive descent LL(1) parser written in Java.

The parser implements version 2.0.0 of the [Core PDML Specification](https://pdml-lang.dev/docs/core/specification/index.html).
[PDML extensions](https://pdml-lang.dev/docs/extensions/user_manual/index.html) are not supported.

This implementation is dependency-free and focuses on simplicity and minimalism (no bells and whistles).
It shows how basic Core PDML reading/parsing operations can be implemented in Java.
The code might serve as a starting point to create more sophisticated parsers providing advanced error handling, feature-rich utilities to explore and transform a PDML AST, etc.
You may also have a look at the unit tests in the `app/src/test` directory.

A much more advanced implementation, covering all PDML extensions, is available in the  [full-pdml-impl](https://github.com/pdml-lang/full-pdml-impl) repository.

## Usage

File `Start.java` in the source code tree contains a simple usage example.

You can use the standard CLI commands of the [Gradle Build Tool](https://gradle.org/) to manage the project:

```
./gradlew build         // build the project
./gradlew run           // run the application
./gradlew test          // run tests
./gradlew javadoc       // create Javadoc files
./gradlew installDist   // create a distribution
```

Note: On Windows type `gradlew` instead of `./gradlew`.
