(ns soundstorm.views
  (:require
   [hiccup.core :as hc]
   [hiccup.page :as hp]))

(defn index-page []
  (hp/html5
   {:lang "en"}
   [:head
    [:title "Soundstorm"]
    [:meta {:charset "utf-8"}]
    (hp/include-css "/css/lib/bootstrap.min.css")
    (hp/include-css "/css/base.css")]
   [:body
    [:div.container
     [:div.row
      [:div.col-12
       [:div.jumbotron
        [:h1 "Sound Storm " [:span..glyphicon.glyphicon-forward]]
        [:h2 "Trying SoundCloud API"]
        [:img {:src "img/sc-btn-connect-l.png" :class "ss-sc-button"}]]]]]
    [:div#app]
    (hp/include-js "/js/lib/jquery.js")
    (hp/include-js "/js/lib/underscore.js")
    (hp/include-js "/js/lib/bootstrap.js")
    (hp/include-js "/js/global.js")]))
