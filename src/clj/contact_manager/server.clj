(ns contact-manager.server
  (:require [cemerick.friend :as friend]
            (cemerick.friend [workflows :as workflows]
                             [credentials :as creds])
            (aleph [http :as http]
                   [formats :as formats])
            (compojure [handler :as handler]
                       [route :as route])
            [ring.middleware.resource :as resource]
            [ring.util.response :as response])
  (:use [compojure.core :as compojure :only (GET POST ANY defroutes context)]
        [ring.middleware.json :only (wrap-json-body wrap-json-response)]
        [hiccup.bootstrap.middleware :only (wrap-bootstrap-resources)]
        [contact-manager views data]
        [lamina.core]))

(def broadcast-channel (channel))

(defn websocket-handler [channel request]
  (siphon broadcast-channel channel))

(defroutes user-routes
  (GET "/account" [] account-page))

(defroutes routes
  (context "/user" [] (friend/wrap-authorize user-routes #{:user.role/user}))
  (GET "/admin" [] (friend/authorize #{:user.role/admin} admin-page))
  (GET "/" [] index-page)
  (GET "/login" [] login-page)
  (GET "/socket" [] (http/wrap-aleph-handler websocket-handler))
  (POST "/contact" {contact :body}
        (add-contact contact)
        (enqueue broadcast-channel
                 (str "contact:add" " " (formats/encode-json->string contact)))
        {})
  (GET "/contacts" [] (get-contacts))
  (friend/logout (ANY "/logout" request (response/redirect "/")))
  (route/not-found page-not-found))

(def app
  (-> routes
      (friend/authenticate {:credential-fn (partial creds/bcrypt-credential-fn get-user-auth)
                            :workflows [(workflows/interactive-form)]})
      (resource/wrap-resource "public")
      (wrap-bootstrap-resources)
      (wrap-json-body {:keywords? true})
      (wrap-json-response)
      (handler/site)
      http/wrap-ring-handler))

(defonce server
  (http/start-http-server #'app {:port 3000 :websocket true}))
