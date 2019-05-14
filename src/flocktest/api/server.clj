(ns flocktest.api.server
  (:require [ring.middleware.params :as middleware]
            [compojure.core :as cc]
            [compojure.route :as route]
            [flocktest.api.handlers :as handlers]
            [ring.adapter.jetty :as jetty]
            [ring.logger :as logger]))

(cc/defroutes routes
  (cc/GET "/search" req handlers/handler-search)
  (route/not-found "404 not found"))

(def ^:private app
  (-> routes
      middleware/wrap-params
      logger/wrap-with-logger))

(defn start-server
  "Start listening"
  [port]
  (jetty/run-jetty app {:port port}))
