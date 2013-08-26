(ns soundstorm.jrepl
  (:require [soundstorm.core :as core]
            [ring.server.standalone :as rs]
            [ring.adapter.jetty :refer [run-jetty]]))

(comment
  (start)

  (stop)
  
  )

(def jetty (atom nil))

(defn start []
  (reset! jetty (rs/serve #'core/secured-app {:port 3000 :browser-uri "/" :auto-reload? false})))

(defn stop []
  (.stop @jetty))

(defn -main [port]
  (run-jetty core/secured-app {:port (Integer. port)}))

;; (rs/serve
;;  myapp/app {:port 3000 :join? false
;;             :open-browser? true :browser-uri "/entities/user/4"
;;             :stacktrace? true :auto-reload? true :auto-refresh? true})
