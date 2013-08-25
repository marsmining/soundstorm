(ns soundstorm.core
  (:require [soundstorm.view :as view]
            [soundstorm.user :as user]
            [clojure.tools.logging :as log]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [compojure.core :refer [GET ANY defroutes]]
            [hiccup.middleware :refer [wrap-base-url]]
            [cemerick.friend :as friend]
            [cemerick.friend.workflows :as workflows]
            [cemerick.friend.credentials :as creds]))

(defroutes main-routes
  (GET "/" req (view/index-page req))
  (GET "/login" req (view/login-page req))
  (GET "/status" req
       (view/status-page req))
  (GET "/tracks" req
       (friend/authorize
        #{:soundstorm.user/user :soundstorm.user/admin}
        (view/tracks-page req)))
  (friend/logout (ANY "/logout" req (ring.util.response/redirect "/")))
  (route/resources "/")
  (route/not-found "Page not found"))

(defn log-requests [handler]
  (fn [request]
    (let [response (handler request)]
      (log/info (:request-method request) (:uri request) (:params request))
      response)))

(def secured-app
  (-> (handler/site
       (friend/authenticate
        main-routes
        {:allow-anon? true
         :login-uri "/login"
         :default-landing-uri "/"
         :credential-fn (partial creds/bcrypt-credential-fn user/fetch)
         :workflows [(workflows/interactive-form)]}))
      (log-requests)
      (wrap-base-url)))
