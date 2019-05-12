(ns flocktest.api.handlers
  (:require [cheshire.core :as json]
            [flocktest.stat.core :as stat]))

(defn- pretty-json
  [data]
  (json/generate-string data {:pretty true}))

(defn- make-response
  [status data]
  {:status status
   :headers {"Content-Type" "application/json; charset=utf-8"}
   :body (-> (case status
               500 {:error data}
               200 data)
            pretty-json)})

(defn handler-search
  [req]
  (as-> req x
    (get-in x [:params "query"])
    (stat/get-bing-domains-stat-by-queries x)
    (make-response 200 x)))
