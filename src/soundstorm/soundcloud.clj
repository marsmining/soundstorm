(ns soundstorm.soundcloud
  "Concerns of SoundCloud API usage as a client"
  (require [soundstorm.util :as util]
           [clojure.tools.logging :as log]
           [clj-http.client :as http]))

(def api-root "https://api.soundcloud.com")

(def resources-partial
  {:me "/me"
   :tracks "/me/tracks"
   :track "/me/tracks/{track}"
   :track-favoriter "/me/tracks/{track}/favoriters"})

(def resources (util/update-values
                resources-partial
                #(str api-root % ".json?oauth_token={oauth-token}")))

(defn substitute [[k v] s]
  "Given a k, v pair, substitute v for '{k}' in s"
  (let [tok (format "{%s}" (name k))]
    (clojure.string/replace s tok v)))

(defn build-uri [resource-key args]
  (let [uri (resource-key resources)]
    (reduce #(substitute %2 %1) uri args)))

(defonce conn-mgr
  (clj-http.conn-mgr/make-reusable-conn-manager
   {:timeout 2 :threads 3}))

(defn sget [resource token]
  (http/get
   (build-uri resource token)
   {:as :json
    :connection-manager conn-mgr}))

(defn get-resources [resources sub-map]
  (log/info "get-resources:" resources sub-map)
  (reduce #(assoc %1 %2 (:body (sget %2 sub-map))) {} resources))
