(ns flocktest.api.handlers
  (:require [cheshire.core :as json]
            [flocktest.stat.core :as stat]))

(defn- pretty-json
  "Convert map to pretty JSON"
  [data]
  (json/generate-string data {:pretty true}))

(defn- make-response
  "Return response map for success and failure requests"
  [status data]
  {:status status
   :headers {"Content-Type" "application/json; charset=utf-8"}
   :body (-> (case status
               500 {:error data}
               200 data)
            pretty-json)})

(defn handler-search
  "Handle search request"
  [req]
  (if-let [queries (get-in req [:params "query"])]
    (try
      (as-> queries x
        (if (vector? x) x (vector x))
        (stat/get-bing-domains-stat-by-queries x)
        (make-response 200 x))
      (catch Exception e (make-response 500 "something went wrong please try again")))
    (make-response 500 "no query params")))
