(ns soundstorm.util)

(defn update-values
  "'Updates' every value in map"
  [m f]
  (into {} (for [[k v] m] [k (f v)])))