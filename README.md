# flocktest

Test task solution.

## Installation

```
$ lein compile
$ lein uberjar
```

## Usage

```
Usage: java -jar ./target/flocktest-1.0.1-SNAPSHOT-standalone.jar [OPTIONS]
or: lein run [OPTIONS]

Options:
  --port                        Port number for public API (default 3000)
  --tpsize                      Threadpool size (default 1)
```

To get domains stat:

```
$ curl localhost:3000/search?query=clojure

{
  "clojure.org" : 2,
  "wikipedia.org" : 2,
  "reddit.com" : 1,
  "cyberforum.ru" : 2,
  "ibm.com" : 1,
  "alexott.net" : 2
}
```

## TODO / Roadmap

* Fix TODOs in source code
* Document the internal design
* Create deployment scripts
* Test coverage
* Benchmarks
* Handle timeouts
* Refresh cookies for bing.com rss fetching after some time

## License

MIT
