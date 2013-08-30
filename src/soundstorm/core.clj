(ns soundstorm.core
  (:require [soundstorm.view :as view]
            [soundstorm.config :as config]
            [soundstorm.soundcloud :as sc]
            [clojure.tools.logging :as log]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [compojure.core :refer [GET ANY defroutes]]
            [hiccup.middleware :refer [wrap-base-url]]
            [cemerick.friend :as friend]
            [cemerick.friend.credentials :as creds]
            [friend-oauth2.workflow :as oauth2]
            [cheshire.core :as json]
            [clj-http.client :as http]))

;; controllers
;;

(defn home [req]
  (if-let [token (:current (friend/identity req))]
    (let [{me :me tracks :tracks}
          (sc/get-resources [:me :tracks] token)]
      (view/main-page req me tracks))
    (view/index-page req)))

;; soundcloud oauth2 config
;;

(defn access-token-parsefn
  [response]
  (log/info "attempting token parse:" response)
  (let [token (-> response
                  :body
                  (json/parse-string true)
                  :access_token)]
    (log/info "token:" token)
    token))

(def config-auth {:roles #{::user/user}})

(def client-config (config/fetch :client-config))

(def uri-config
  {:authentication-uri {:url "https://soundcloud.com/connect"
                        :query {:client_id (:client-id client-config)
                                :response_type "code"
                                :redirect_uri (oauth2/format-config-uri client-config)
                                :scope "non-expiring"}}

   :access-token-uri {:url "https://api.soundcloud.com/oauth2/token"
                      :query {:client_id (:client-id client-config)
                              :client_secret (:client-secret client-config)
                              :grant_type "authorization_code"
                              :redirect_uri (oauth2/format-config-uri client-config)
                              :code ""}}})

;; ring / compojure / friend config
;;

(defroutes main-routes
  (GET "/" req (home req))
  (friend/logout (ANY "/logout" req (ring.util.response/redirect "/")))
  (route/resources "/")
  (route/not-found "Page not found"))

(defn is-media? [str]
  (or (.endsWith str ".js")
      (.endsWith str ".css")
      (.endsWith str ".ico")
      (.endsWith str ".png")
      (.endsWith str ".jpg")
      (.endsWith str ".woff")
      (.endsWith str ".ttf")))

(defn log-requests [handler]
  (fn [req]
    (if (is-media? (:uri req))
      (handler req)
      (do
        (log/info (:request-method req) (:uri req) (:query-string req))
        (let [start (System/currentTimeMillis)
              rez (handler req)]
          (log/info {:uri (:uri req) :elapsed (- (System/currentTimeMillis) start)})
          rez)))))

(def secured-app
  (-> (handler/site
       (friend/authenticate
        main-routes
        {:allow-anon? true
         :login-uri "/login"
         :default-landing-uri "/"
         :workflows [(oauth2/workflow
                      {:client-config client-config
                       :uri-config uri-config
                       :access-token-parsefn access-token-parsefn
                       :config-auth config-auth})]}))
      (wrap-base-url)
      (log-requests)))
