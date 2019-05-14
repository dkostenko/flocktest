(ns flocktest.main
  (:require [flocktest.api.server :as api]
            [flocktest.stat.core :as stat]
            [clojure.tools.cli :refer [parse-opts]])
  (:gen-class))

(def ^:private cli-options
  [["-port" "--port PORT" "Port number for public API"
    :default 3000
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 0x10000) "Must be a number between 0 and 65536"]]

  ["-tpsize" "--tpsize SIZE" "Threadpool size"
    :default 1
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 1000) "Must be a number between 0 and 1000"]]
  ])

(defn -main
  "Test task solution"
  [& args]
  (let [opts (parse-opts args cli-options)]
    (do
      (stat/set-pool-size (get-in opts [:options, :tpsize]))
      (api/start-server (get-in opts [:options, :port])))))
