(defproject soundstorm "0.1.0-SNAPSHOT"
  :description "Soundstorm - Awesomize SoundCloud"
  :url "http://ockhamsolutions.de/soundstorm"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/tools.logging "0.2.6"]
                 [ch.qos.logback/logback-classic "1.0.10"]
                 [org.slf4j/jcl-over-slf4j "1.7.5"]
                 [com.cemerick/friend "0.1.5"]
                 [friend-oauth2 "0.0.4"]
                 [ring "1.2.0"]
                 [ring-server "0.2.8"]
                 [ring/ring-jetty-adapter "1.1.8"]
                 [clj-http "0.7.6"]
                 [cheshire "5.2.0"]
                 [compojure "1.1.5"]
                 [hiccup "1.0.0"]
                 [com.taoensso/carmine "2.2.0"]]
  :plugins [[lein-ring "0.7.1"]]
  :ring {:handler soundstorm.core/secured-app})
