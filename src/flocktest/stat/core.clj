(ns flocktest.stat.core
  (:require [feedparser-clj.core :as fp]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [com.climate.claypoole :as cp]
            [clojure.set :as set]))

(def ^:private pool (atom nil))
(def ^:private poolsize (atom 1))

(defn set-pool-size
  "Set thread pool size"
  [size]
  (reset! poolsize size))

(defn- get-or-create-threadpool
  "Return or create and return new threadpool"
  []
  (if-let [pl @pool]
    pl
    (reset! pool (cp/threadpool @poolsize))))

(defn- make-bing-rss-url
  "Return bing rss url"
  [query]
  (str "https://www.bing.com/search?q=" query "&format=rss&count=10"))

(defn- get-l2-domain-from-url [url]
  "Return second-level domain from url string"
  (as-> url x
    (io/as-url x)
    (.getHost x)
    (s/split x #"\.")
    (take-last 2 x)
    (s/join "." x)))

(defn- fetch-rss-entries-by-url
  "Return entries from parsed RSS feed"
  [url]
  (as-> url x
    (fp/parse-feed x)
    (map :link (:entries x))))

; TODO replace double-space in query.
(defn- prepare-queries
  "Clean each query string (trim, make lowercase, make unique)"
  [queries]
  (as-> queries x
    (map s/trim x)
    (map s/lower-case x)
    (set x)))

(defn get-bing-domains-stat-by-queries
  "Return domains histogram as map from bing RSS feed"
  [queries]
  (let [pool (get-or-create-threadpool)]
    (as-> queries x
      (prepare-queries x)
      (map make-bing-rss-url x)
      (cp/pmap pool fetch-rss-entries-by-url x)
      (apply set/union x)
      (map get-l2-domain-from-url x)
      (frequencies x))))
