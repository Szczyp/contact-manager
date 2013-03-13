(ns contact-manager.broadcast
  (:require [lamina.core :as l])
  (:use [aleph.formats]))

(defonce ^:private broadcast-channel (l/permanent-channel))

(defn websocket-handler [channel request]
  (l/siphon broadcast-channel channel))

(defn enqueue
  ([cmd] (l/enqueue broadcast-channel cmd))
  ([cmd data] (l/enqueue broadcast-channel
                (str cmd " " (encode-json->string data)))))
