(ns soundstorm.core
  (:require [soundstorm.view :as view]
            [soundstorm.user :as user]
            [soundstorm.soundcloud :as sc]
            [clojure.tools.logging :as log]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [compojure.core :refer [GET ANY defroutes]]
            [hiccup.middleware :refer [wrap-base-url]]
            [cemerick.friend :as friend]
            [cemerick.friend.workflows :as workflows]
            [cemerick.friend.credentials :as creds]
            [friend-oauth2.workflow :as oauth2]
            [cheshire.core :as json]
            [clj-http.client :as http]))

;; controllers
;;

(defn home [req]
  (if-let [token (:current (friend/identity req))]
    (let [me (sc/get-resource :me token)]
      (log/info "identity:" token)
      (log/info "me:" me)
      (view/main-page req me))
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

(def client-config
  {:client-id "b35a89f510267e48d1ec0169e4d7686e"
   :client-secret "5b53d5bdcd9be8b0ccb304e23e070f3d"
   :callback {:domain "http://localhost.com:3000"
              :path "/sc-redirect-uri"}})

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
  (GET "/login" req (view/login-page req))
  (GET "/status" req
       (view/status-page req))
  (GET "/tracks" req
       (friend/authorize #{::user/user} (view/tracks-page req)))
  (friend/logout (ANY "/logout" req (ring.util.response/redirect "/")))
  (route/resources "/")
  (route/not-found "Page not found"))

(defn log-requests [handler]
  (fn [req]
    (log/info (:request-method req) (:uri req) (:params req))
    (handler req)))

(def secured-app
  (-> (handler/site
       (friend/authenticate
        main-routes
        {:allow-anon? true
         :login-uri "/login"
         :default-landing-uri "/"
         :credential-fn (partial creds/bcrypt-credential-fn user/fetch)
         :workflows [(oauth2/workflow
                      {:client-config client-config
                       :uri-config uri-config
                       :access-token-parsefn access-token-parsefn
                       :config-auth config-auth})
                     (workflows/interactive-form)]}))
      (wrap-base-url)
      (log-requests)))
