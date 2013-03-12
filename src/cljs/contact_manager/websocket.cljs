(ns contact-manager.websocket
  (:refer-clojure :exclude [assoc! map filter remove])
  (:require [contact-manager.utils :refer [assoc! update-in! map filter remove]]
            [goog.events :as events]
            [goog.net.WebSocket :as websocket]
            [goog.net.WebSocket.EventType :as websocket-event]
            [goog.net.WebSocket.MessageEvent :as websocket-message]))

;; WebSocket
(defn create []
  (goog.net.WebSocket.))

(defn register-handler! [socket message-handler]
  (events/listen socket websocket-event/OPENED
                   #(.send socket "register"))
  (events/listen socket websocket-event/MESSAGE
                 #(let [payload (:message %)
                        [_ cmd body] (re-matches #"([^ ]+) (.*)" payload)
                        body (.parse js/JSON body)]
                    (message-handler cmd body)))
  socket)

(defn connect!
  "Connects WebSocket"
  [socket url]
  (try
   (.open socket url)
   socket
   (catch js/Error e
     (.log js/console "No WebSocket supported, get a decent browser."))))

(defn close!
  "Closes WebSocket"
  [socket]
  (.close socket))

(defn emit!
  "Sends a command to server, optionally with message."
  ([socket cmd]
     (emit! socket cmd nil))
  ([socket cmd msg]
     (let [packet (str cmd (when msg (str " " (.stringify js/JSON msg))))]
       (.send socket packet))))
