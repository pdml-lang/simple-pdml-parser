# Simple PDML Parser Written in Java

This repository contains the source code files of a simple PDML parser written in Java.

The parser supports [Core PDML](https://pdml-lang.dev/docs/core/specification/index.html), and the following PDML extensions:

- [Comments](https://pdml-lang.dev/docs/extensions/user_manual/index.html#comments)
- [Character escape sequences](https://pdml-lang.dev/docs/extensions/user_manual/index.html#escape_sequences)

There are no dependencies.

## Overview

This parser reads input from a standard Java character input stream (file, string, STDIN, Java `Reader`, etc.) and produces a PDML AST.

The AST can then be explored, modified, or transformed in your application.

## Usage example

List the node names in a PDML document:

```
PdmlParser parser = new PdmlParser();

RootNode rootNode = parser.parseString ( """
    [doc
        text
        [child1 text child 1]
        [child2 text child 2]
    ]""" );

NodeUtils.forEachNodeInTree ( rootNode, node -> {
    if ( node instanceof RootOrBranchNode branchNode ) {
        System.out.println ( branchNode.getName() );
    }
});
```

Output:

```
doc
child1
child2
````

## Useful links:

- [API Javadoc](https://pdml-lang.dev/docs/api/simple-parser/index.html)
- [PDML website](https://pdml-lang.dev/).
- [Full PDML implementation](https://github.com/pdml-lang/full-pdml-impl) (supports all PDML extensions)