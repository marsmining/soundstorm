(ns soundstorm.config
  "Load edn config file"
  (:require [clojure.tools.reader :as edn]
            [clojure.java.io :refer [resource]]))

(def cp-slurp (comp slurp resource))

(def config (edn/read-string (cp-slurp "ss.edn")))

(defn fetch [k]
  (k config))
