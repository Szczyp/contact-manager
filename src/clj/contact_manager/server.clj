(ns contact-manager.server
  (:require [cemerick.friend :as friend]
            (cemerick.friend [workflows :as workflows]
                             [credentials :as creds])
            (aleph [http :as http])
            (compojure [handler :as handler]
                       [route :as route])
            [ring.middleware.resource :as resource]
            [ring.util.response :as response]
            [contact-manager.data :as d])
  (:use [compojure.core :as compojure :only (GET ANY defroutes context)]
        [ring.middleware.json :only (wrap-json-body wrap-json-response)]
        [hiccup.bootstrap.middleware :only (wrap-bootstrap-resources)]
        [contact-manager views broadcast contacts]))

(defroutes routes
  (context "/contacts" [] (-> contact-routes (friend/wrap-authorize #{:user.role/user})))
  (GET "/socket" [] (-> websocket-handler http/wrap-aleph-handler))
  (GET "/login" [] login-page)
  (friend/logout (ANY "/logout" request (response/redirect "/")))
  (GET "/" [] (response/redirect "/contacts"))
  (route/not-found page-not-found))

(def app
  (-> routes
      (friend/authenticate {:credential-fn (partial creds/bcrypt-credential-fn d/get-user-auth)
                            :workflows [(workflows/interactive-form)]})
      handler/site
      (wrap-json-body {:keywords? true})
      wrap-json-response
      (resource/wrap-resource "public")
      wrap-bootstrap-resources
      http/wrap-ring-handler))

(defn -main [port]
  (http/start-http-server #'app {:port (Integer. port) :websocket true}))
