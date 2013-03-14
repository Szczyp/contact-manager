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

(defn- entity->map [e]
  (merge (select-keys e [:db/id]) e))

(defn- get-map [db eid]
  (->> eid
       (d/entity db)
       entity->map
       collapse-keys))

(def get (partial get-map (get-db)))

(defn get-first [query]
  (let [db (get-db)]
   (->> (q query db)
        ffirst
        (get-map db))))

(defn get-all [query]
  (let [db (get-db)]
   (->> (q query db)
        (map (comp #(get-map db %) first)))))

(defn save! [n m]
  (let [id (clojure.core/get m :id (d/tempid :db.part/user))
        tx @(d/transact conn [(merge {:db/id id} (expand-keys n (dissoc m :id)))])
        id (if-let [new-id (d/resolve-tempid (:db-after tx) (:tempids tx) id)]
             new-id
             id)]
    (assoc m :id id)))

(defn delete! [id]
  (d/transact conn [[:db.fn/retractEntity id]]))

(defn get-user-auth [username]
  (get-first `[:find ?user :where [?user :user/username ~username]]))
