(ns soundstorm.jrepl
  (:require [soundstorm.core :as core]
            [ring.server.standalone :as rs]
            [ring.adapter.jetty :refer [run-jetty]]))

(defonce jetty (atom nil))

(defn start []
  (reset! jetty (rs/serve #'core/secured-app {:port 3000 :browser-uri "/" :auto-reload? false})))

(defn stop []
  (.stop @jetty))

(defn -main [port]
  (run-jetty core/secured-app {:port (Integer. port)}))

(comment
  (start)

  (stop)
  
  )