(ns contact-manager.server
  (:require [cemerick.friend :as friend]
            (cemerick.friend [workflows :as workflows]
                             [credentials :as creds])
            [aleph.http :as http]
            (compojure [handler :as handler]
                       [route :as route])
            [ring.middleware.resource :as resource]
            [ring.util.response :as response])
  (:use [compojure.core :as compojure :only (GET ANY defroutes)]
        [hiccup.bootstrap.middleware :only (wrap-bootstrap-resources)]
        [contact-manager views data]))

(defroutes user-routes
  (GET "/account" _ account-page))

(defroutes routes
  (compojure/context "/user" _ (friend/wrap-authorize user-routes #{:user.role/user}))
  (GET "/admin" _ (friend/authorize #{:user.role/admin} admin-page))
  (GET "/" _ index-page)
  (GET "/login" _ login-page)
  (GET "/help" _ (response/redirect "/help.html"))
  (friend/logout (ANY "/logout" request (response/redirect "/")))
  (route/not-found page-not-found))

(def app
  (-> routes
      (friend/authenticate {:credential-fn (partial creds/bcrypt-credential-fn get-user-auth)
                            :workflows [(workflows/interactive-form)]})
      (resource/wrap-resource "public")
      (wrap-bootstrap-resources)
      (handler/site)
      http/wrap-ring-handler))

(defonce server
  (http/start-http-server #'app {:port 3000}))
