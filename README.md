# ffa-account-analysis

Family Food Action account transaction analysis

An attempt to distinguish between regular and one-off donations so that the level of funds can be predicted.

## Installation

Download from https://github.com/jerzywie/ffa-account-analysis

## Usage

Run the project directly, via `:main-opts` (`-m jerzywie.ffa-account-analysis`):

    $ clojure -M:run-m

Run the project's tests:

    $ clojure -X:test:runner

Build an uberjar:

    $ clojure -X:uberjar

This will update the generated `pom.xml` file to keep the dependencies synchronized with
your `deps.edn` file. You can update the version (and SCM tag) information in the `pom.xml` using the
`:version` argument:

    $ clojure -X:uberjar :version '"1.2.3"'

If you don't want the `pom.xml` file in your project, you can remove it, but you will
also need to remove `:sync-pom true` from the `deps.edn` file (in the `:exec-args` for `depstar`).

Run that uberjar:

    $ java -jar ffa-account-analysis.jar

## Options

tbd.

## Examples

...

### Bugs

...

## License

Copyright © 2021 Jerzy

Distributed under the Eclipse Public License version 1.0.
