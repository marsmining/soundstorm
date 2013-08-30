(ns soundstorm.view
  (:require
   [hiccup.core :as hc]
   [hiccup.page :as hp]
   [hiccup.form :as hf]
   [cemerick.friend :as friend]))

(declare row col whois)

(defmacro base-page [req & body]
  `(hp/html5
    {:lang "en"}
    [:head
     [:title "soundstorm"]
     [:meta {:charset "utf-8"}]
     [:link {:rel "shortcut icon" :type "image/x-icon" :href "/img/favicon.ico"}]
     (hp/include-css "/css/lib/bootstrap.min.css")
     (hp/include-css "/css/base.css")]
    [:body
     [:div.container
      ~@body]
     (hp/include-js "/js/lib/jquery.js")
     (hp/include-js "/js/lib/underscore.js")
     (hp/include-js "/js/lib/bootstrap.js")
     (hp/include-js "/js/global.js")]))

(defn jumbo [req]
  (row 12 [:div.jumbotron
           [:h1 "soundstorm " [:span.ss-sub.glyphicon.glyphicon-forward]]
           [:h2 "Tinkering with SoundCloud API"]
           (if-let [identity (friend/identity req)]
             nil
             [:div [:a {:href "sc-redirect-uri"}
                    [:img {:src "img/sc-btn-connect-l.png" :class "ss-sc-button"}]]])]))

(defn misc [title]
  (list
   (row 12 [:div.jumbotron
            [:h1 "soundstorm " [:span.ss-sub.glyphicon.glyphicon-forward]]
            [:h2 "Trying SoundCloud API"]])
   (row 12 [:div.well
            [:h2 title]])))

(defn login-form [req]
  (hf/form-to
   {:class "form-horizontal"} [:post "login"]
   [:div.form-group
    [:label.col-lg-2.control-label "Email"]
    [:div.col-lg-10
     (hf/email-field {:class "form-control" :placeholder "Email"}
                     "username" (-> req :params :username))]]
   [:div.form-group
    [:label.col-lg-2.control-label "Password"]
    [:div.col-lg-10
     (hf/password-field {:class "form-control" :placeholder "Password"} "password")]]
   [:div.form-group
    [:div.col-lg-offset-2.col-lg-10
     (hf/submit-button {:class "btn btn-default"} "Sign in")]]))

(defn whois [req]
  [:div.panel.panel-default
   [:div.panel-heading [:h4 "Authentication Status"]]
   [:div.panel-body
    (if-let [identity (friend/identity req)]
      (let [id-str (apply str "Logged in, with these roles: "
                          (-> identity friend/current-authentication :roles))]
        [:div
         [:p id-str]
         [:a {:href "logout"} "Logout"]])
      "Anonymous user")]])

(defn track [{:keys [title waveform_url permalink_url playback_count]}]
  [:li.list-group-item.ss-waveform
   {:style (str "background-image: url('" waveform_url "');")}
   [:a {:href permalink_url} title]
   [:span.badge playback_count]])

(defn profile [p t]
  [:div.panel.panel-default
   [:div.panel-heading [:h4 (:username p)]]
   [:div.panel-body
    (col 2 [:img.img-thumbnail {:src (:avatar_url p)}])
    (col 6 [:ul.list-group
            (map track t)])]])

(defn index-page  [r] (base-page r (jumbo r)))
(defn main-page   [r p t] (base-page r (jumbo r) (row (profile p t)) (row (whois r))))
(defn login-page  [r] (base-page r (misc "login") (row 6 (login-form r)) (row (whois r))))

(defn row
  ([body] (row 12 body))
  ([n body]
     [:div.row
      (col n body)]))

(defn col [n body]
  [(keyword (str "div.col-lg-" n))
   body])