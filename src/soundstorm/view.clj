(ns soundstorm.view
  (:require
   [hiccup.core :as hc]
   [hiccup.page :as hp]
   [hiccup.form :as hf]
   [cemerick.friend :as friend]))

(defn row [n body]
  [:div.row
   [(keyword (str "div.col-lg-" n))
    body]])

(defn whois [req]
  (row 12 [:div.panel.panel-default
           [:div.panel-body
            (if-let [identity (friend/identity req)]
              (apply str "Logged in, with these roles: "
                     (-> identity friend/current-authentication :roles))
              "anonymous user")]]))

(defn base-page [req body]
  (hp/html5
   {:lang "en"}
   [:head
    [:title "Soundstorm"]
    [:meta {:charset "utf-8"}]
    (hp/include-css "/css/lib/bootstrap.min.css")
    (hp/include-css "/css/base.css")]
   [:body
    [:div.container (hc/html body)
     (hc/html (whois req))]
    (hp/include-js "/js/lib/jquery.js")
    (hp/include-js "/js/lib/underscore.js")
    (hp/include-js "/js/lib/bootstrap.js")
    (hp/include-js "/js/global.js")]))

(defn jumbo []
  (row 12 [:div.jumbotron
           [:h1 "soundstorm " [:span.ss-sub.glyphicon.glyphicon-forward]]
           [:h2 "Trying SoundCloud API"]
           [:img {:src "img/sc-btn-connect-l.png" :class "ss-sc-button"}]
           [:div [:a {:href "tracks"} "Tracks"]]]))

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

(defn index-page  [r] (base-page r (jumbo)))
(defn status-page [r] (base-page r (misc "status")))
(defn tracks-page [r] (base-page r (misc "tracks")))
(defn login-page  [r] (base-page r (concat (misc "login") [(row 6 (login-form r))])))