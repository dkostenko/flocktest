(ns flocktest.stat.core
  (:require [remus :refer [parse-url]]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [com.climate.claypoole :as cp]
            [clojure.set :as set]))

(def ^:private pool (atom nil))
(def ^:private poolsize (atom 1))
(def ^:private cached-bing-cookies (atom nil))

(defn- get-or-create-threadpool
  "Return or create and return new threadpool"
  []
  (if-let [pl @pool]
    pl
    (reset! pool (cp/threadpool @poolsize))))

(defn- make-bing-rss-url
  "Return bing rss url"
  [cnt query]
  (str "https://www.bing.com/search?q=" query "&format=rss&count=" cnt))

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
  [cookies url]
  (as-> url x
    (parse-url x {:cookies cookies})
    (map :link (:entries (:feed x)))))

; TODO replace double-space in query.
(defn- prepare-queries
  "Clean each query string (trim, make lowercase, make unique)"
  [queries]
  (as-> queries x
    (map s/trim x)
    (map s/lower-case x)
    (set x)))

(defn init
  "Set thread pool size and make http request for cookies"
  [size]
  ; It's a hack: bing.com doesn't return entries without cookies,
  ; that's why it's needed to get and cache cookies in init function.
  (reset! cached-bing-cookies (:cookies (:response (parse-url (make-bing-rss-url 1 "cookies")))))
  (reset! poolsize size))

(defn get-bing-domains-stat-by-queries
  "Return domains histogram as map from bing RSS feed"
  [queries]
  (let [pool (get-or-create-threadpool)
        cookies @cached-bing-cookies]
    (as-> queries x
      (prepare-queries x)
      (map (partial make-bing-rss-url 10) x)
      (cp/pmap pool (partial fetch-rss-entries-by-url cookies) x)
      (apply set/union x)
      (map get-l2-domain-from-url x)
      (frequencies x))))
