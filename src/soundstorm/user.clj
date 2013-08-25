(ns soundstorm.user
  (:require [soundstorm.redis :as redis]
            [cemerick.friend.credentials :as creds]))

(defn user-prefix [username] (str "ss:user:" username))

(defn store [u]
  (redis/store (user-prefix (:username u)) u))

(defn fetch [u]
  (redis/fetch (user-prefix u)))

(comment

  (def demo-users [{:username "root"
                    :password (creds/hash-bcrypt "er44")
                    :roles #{::admin}}
                   {:username "foo"
                    :password (creds/hash-bcrypt "er55")
                    :roles #{::user}}])

  (map store demo-users)

  (fetch "foo")

  )