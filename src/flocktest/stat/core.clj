(ns flocktest.stat.core
  (:require [feedparser-clj.core :as fp]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [com.climate.claypoole :as cp]
            [clojure.set :as set]))

(def ^:private pool (atom nil))
(def ^:private poolsize 10)

(defn set-pool-size
  [size]
  (reset! poolsize size))

(defn- get-or-create-threadpool
  [size]
  (if-let [pl @pool]
    pl
    (reset! pool (cp/threadpool size))))

(defn- make-bing-rss-url
  [query]
  (str "https://www.bing.com/search?q=" query "&format=rss&count=10"))

(defn- get-l2-domain-from-link [link]
  (as-> link x
    (io/as-url x)
    (.getHost x)
    (s/split x #"\.")
    (take-last 2 x)
    (s/join "." x)))

(defn- fetch-rss-entries-by-link
  [link]
  (as-> link x
    (fp/parse-feed x)
    (map :link (:entries x))))

; TODO replace double-space in query.
(defn- prepare-queries
  [queries]
  (as-> queries x
    (map s/trim x)
    (map s/lower-case x)
    (set x)))

(defn get-bing-domains-stat-by-queries
  [queries]
  (let [pool (get-or-create-threadpool 2)]
    (as-> queries x
      (prepare-queries x)
      (map make-bing-rss-url x)
      (cp/pmap pool fetch-rss-entries-by-link x)
      (apply set/union x)
      (map get-l2-domain-from-link x)
      (frequencies x))))
