(ns flocktest.main
  (:require [flocktest.api.server :as api]
            [clojure.tools.cli :refer [parse-opts]])
  (:gen-class))

(def ^:private cli-options
  [["-p" "--port PORT" "Port number"
    :default 3000
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 0x10000) "Must be a number between 0 and 65536"]]

  ["-t" "--tpsize SIZE" "Threadpool size"
    :default 20
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 1000) "Must be a number between 0 and 1000"]]
  ])

(defn -main
  "Test task solution"
  [& args]
  (let [opts (parse-opts args cli-options)]
    (api/start-server (get-in opts [:options, :port]))))
