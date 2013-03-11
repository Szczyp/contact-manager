(ns contact-manager.data
  (:require [clojure.java.io :as io]
            [cemerick.friend.credentials :as creds])
  (:use [datomic.api :only (q db) :as d]))

(def ^:private uri "datomic:mem://contact-manager")

(d/create-database uri)

(def ^:private conn (d/connect uri))

@(d/transact conn (read-string (slurp (io/resource "schema.edn"))))

(d/transact conn
            [{:db/id (d/tempid :db.part/user)
              :user/username "admin"
              :user/password (creds/hash-bcrypt "a")
              :user/roles [:user.role/admin :user.role/user]}
             {:db/id (d/tempid :db.part/user)
              :user/username "user"
              :user/password (creds/hash-bcrypt "a")
              :user/roles :user.role/user}])

(defn get-user-auth [username]
  (let [db (db conn)
        user-auth (->> (q `[:find ?user :where [?user :user/username ~username]] db)
                       (ffirst)
                       (d/entity db))]
    {:username (:user/username user-auth)
     :password (:user/password user-auth)
     :roles (:user/roles user-auth)}))
