# constraint-validations-url

[![Build Status](https://travis-ci.org/listora/constraint-validations-url.svg?branch=master)](https://travis-ci.org/listora/constraint-validations-url)

Adds support for validating URLs to [constraint][]

## Installation

To install via Leiningen add the following dependency to your `project.clj`:

``` clj
[listora/constraint-validations-url "0.0.6"]
```

## Usage

``` clj
(require '[constraint.core :refer [valid?]]
         '[constraint.validations.url :refer [url]])

(valid? (url ["http"])  "http://example.com")  ; => true
(valid? (url ["http"])  "https://example.com") ; => false
(valid? (url ["https"]) "https://example.com") ; => true
(valid? (url ["http" "https"]) "example.com")  ; => false
```

Validation is handled by [commons-validator][] internally.

## License

Copyright © 2014 Listora

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

[constraint]: https://github.com/listora/constraint
[commons-validator]: http://commons.apache.org/proper/commons-validator/apidocs/overview-summary.html
