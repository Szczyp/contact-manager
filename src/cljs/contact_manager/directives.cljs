(ns contact-manager.directives
  (:refer-clojure :exclude [assoc! map filter remove])
  (:require [contact-manager.utils :refer [assoc! update-in! map filter remove]]
            [clojure.string :refer [blank?]]))

(def directives
  (.module js/angular "contact-manager.directives" (array)))
