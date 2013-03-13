(ns contact-manager.data
  (:require [clojure.java.io :as io]
            [cemerick.friend.credentials :as creds])
  (:use [datomic.api :only (q db) :as d]))

(def ^:private uri "datomic:mem://contact-manager")

(d/create-database uri)

(def ^:private conn (d/connect uri))

(defn get-db [] (db conn))

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

(defn- transform-keys [f m]
  (let [get-keys (fn [m]
                   (->> (keys m)
                        (map #(hash-map %1 (f %1)))
                        (reduce into)))]
    (clojure.set/rename-keys m (get-keys m))))

(def ^:private collapse-keys (partial transform-keys #(keyword (re-find #"[^/]+$" (name %)))))

(defn- expand-keys [n m]
  (transform-keys #(keyword (str (name n) "/" (name %))) m))

(defn- get-entity [db eid]
  (->> eid
       (d/entity db)
       (merge {})
       collapse-keys))

(defn get-first [db query]
  (->> query
       ffirst
       (get-entity db)))

(defn get-all [db query]
  (->> query
       (map (comp #(get-entity db %) first))))

(defn save! [n m]
  (d/transact conn
              [(merge {:db/id (d/tempid :db.part/user)}
                      (expand-keys n m))]))

(defn get-user-auth [username]
  (let [db (get-db)]
    (->> (q `[:find ?user :where [?user :user/username ~username]] db)
         (get-first db))))
