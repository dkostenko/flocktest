(defproject flocktest "1.0.0-SNAPSHOT"
  :description "FIXME: write"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [ring/ring-core "1.6.3"]
                 [ring/ring-jetty-adapter "1.6.3"]
                 [clj-http "3.10.0"]
                 [compojure "1.6.1"]
                 [org.clojure/tools.cli "0.4.2"]
                 [org.clojure/data.json "0.2.6"]
                 [cheshire "5.8.1"]
                 [ring-logger "1.0.1"]
                 [org.clojars.scsibug/feedparser-clj "0.4.0" :exlusions [org.clojure/clojure]]
                 [com.climate/claypoole "1.1.4"]]
  :main flocktest.main)