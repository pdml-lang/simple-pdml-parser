# Simple Core PDML Parser Written in Java

## Overview

This repository contains the source code of a simple [Core PDML](https://pdml-lang.dev/docs/core/specification/index.html) recursive descent LL(1) parser written in Java.

PDML extensions are not supported.

This implementation is dependency-free and focuses on simplicity and minimalism (no bells and whistles).
It's just an example of basic Core PDML reading/parsing operations implemented in Java.
The code might serve as a starting point to create more sophisticated parsers providing user-friendly error messages, feature-rich utilities to explore and transform a PDML tree, etc.

A much more advanced implementation, covering all PDML extensions, is available in the  [full-pdml-impl](https://github.com/pdml-lang/full-pdml-impl) repository.

## Usage

File `Start.java` in the source code tree contains a simple usage example.
You can also have a look at unit tests in the `test` directory.

You can use the standard CLI commands of the [Gradle Build Tool](https://gradle.org/) to build and run the project, run tests, and create Javadoc files, e.g.:

```
./gradlew build
./gradlew run
./gradlew test
./gradlew javadoc
```
