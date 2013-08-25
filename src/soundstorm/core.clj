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
            [cemerick.friend.credentials :as creds]
            [friend-oauth2.workflow :as oauth2]))

(defn access-token-parsefn
  [response]
  (log/info "attempting token parse:" response)
  (-> response
      :body
      ring.util.codec/form-decode
      clojure.walk/keywordize-keys
      :access_token))

(def config-auth {:roles #{:soundstorm.user/user}})

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
    (log/info (:request-method request) (:uri request) (:params request))
    (handler request)))

(def secured-app
  (-> (handler/site
       (friend/authenticate
        main-routes
        {:allow-anon? true
         :login-uri "/login"
         :default-landing-uri "/"
         :credential-fn (partial creds/bcrypt-credential-fn user/fetch)
         :workflows [(workflows/interactive-form)
                     (oauth2/workflow
                      {:client-config client-config
                       :uri-config uri-config
                       :access-token-parsefn access-token-parsefn
                       :config-auth config-auth})]}))
      (log-requests)
      (wrap-base-url)))
