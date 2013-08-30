(defproject soundstorm "0.1.0-SNAPSHOT"
  :description "Soundstorm - Awesomize SoundCloud"
  :url "http://ockhamsolutions.de/soundstorm"
  :dependencies [[org.clojure/clojure "1.5.1"]

                 [org.clojure/tools.logging "0.2.6"]
                 [ch.qos.logback/logback-classic "1.0.13"]
                 [org.slf4j/jcl-over-slf4j "1.7.5"]

                 [org.clojure/tools.reader "0.7.6"]
                 [org.clojure/tools.macro "0.1.2"]
                 [org.clojure/core.incubator "0.1.3"]

                 [slingshot "0.10.3"]
                 [commons-codec "1.8"]
                 [org.apache.httpcomponents/httpclient "4.2.5"]

                 [ring/ring-core "1.2.0"]
                 [ring/ring-devel "1.2.0"]
                 [ring/ring-servlet "1.2.0"]
                 [ring/ring-jetty-adapter "1.2.0"]
                 [ring-server "0.3.0"]

                 [com.cemerick/friend "0.1.5"]
                 [friend-oauth2 "0.0.4"]
                 [clj-http "0.7.6"]
                 [cheshire "5.2.0"]
                 [compojure "1.1.5"]
                 [hiccup "1.0.4"]
                 [com.taoensso/carmine "2.2.0"]]
  :plugins [[lein-ring "0.7.1"]]
  :ring {:handler soundstorm.core/secured-app}
  :profiles {:dev {:resource-paths ["config/dev"]}
             :prod {:resource-paths ["config/prod"]}})
