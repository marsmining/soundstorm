(ns soundstorm.soundcloud
  "Concerns of SoundCloud API usage as a client"
  (require
   [clj-http.client :as http]))

(def sc-api-root "https://api.soundcloud.com")

(def sc-token-representation "{{sc-rep}}")
(def sc-token-oauth-token "{{sc-oath}}")

(def sc-resources
  (let [me (str sc-api-root "/me")
        tk (str "." sc-token-representation)
        qs (str "?oauth_token=" sc-token-oauth-token)]
    {:me (str me tk qs)
     :tracks (str me "/tracks" tk qs)}))

(defn build-uri
  ([resource token]
     (build-uri resource token "json"))
  ([resource token representation]
     (-> (sc-resources resource)
         (clojure.string/replace sc-token-representation representation)
         (clojure.string/replace sc-token-oauth-token token))))

(defonce conn-mgr
  (clj-http.conn-mgr/make-reusable-conn-manager
   {:timeout 2 :threads 3}))

(defn sget [resource token]
  (http/get
   (build-uri resource token)
   {:as :json
    :connection-manager conn-mgr}))

(defn get-resources [resources token]
  (reduce #(assoc %1 %2 (:body (sget %2 token))) {} resources))
