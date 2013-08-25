(ns soundstorm.redis
  (:require [clojure.tools.logging :as log]
            [taoensso.carmine :as car]))

(def r1-conn {:pool {} :spec {:host "localhost" :port 6379}})

(defmacro wcar* [& body] `(car/wcar r1-conn ~@body))

(defn store [k v]
  (log/info "store key:" k "val:" v)
  (wcar* (car/set k v)))

(defn fetch [k]
  (log/info "fetch key:" k)
  (wcar* (car/get k)))

(comment

  (wcar*
   (car/ping)
   (car/set "foo" "bar")
   (car/get "foo"))

  )
