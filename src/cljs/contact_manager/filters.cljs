(ns contact-manager.filters
  (:refer-clojure :exclude [assoc! map filter remove])
  (:require [contact-manager.utils :refer [assoc! update-in! map filter remove]]
            [clojure.string :refer [blank?]]))

(def filters
  (.module js/angular "contact-manager.filters" (array)))
