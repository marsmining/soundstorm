(ns soundstorm.util
  (:require [clojure.algo.monads :refer :all]))

(defn update-values
  "'Updates' every value in map"
  [m f]
  (into {} (for [[k v] m] [k (f v)])))