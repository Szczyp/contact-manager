(ns contact-manager.services
  (:refer-clojure :exclude [assoc! map filter remove])
  (:require [contact-manager.utils :refer [assoc! update-in! map filter remove]]
            [contact-manager.websocket :as websocket]))

(->
 (.module js/angular "contact-manager.services" (array))
 (.factory "socket"
           (fn [$rootScope]
             (let [message-handler (fn [cmd body]
                                     (.$apply $rootScope
                                              #(.$broadcast $rootScope cmd body)))
                   socket (-> (websocket/create)
                              (websocket/register-handler! message-handler)
                              (websocket/connect! (str "ws://"
                                                       js/document.location.host
                                                       "/socket")))]
               (assoc! (js-obj)
                       :emit (fn [cmd message]
                               (websocket/emit! socket cmd message)))))))
