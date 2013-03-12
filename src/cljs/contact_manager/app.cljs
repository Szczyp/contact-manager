(ns contact-manager.app
  (:refer-clojure :exclude [assoc! map filter remove])
  (:require [contact-manager.utils :refer [assoc! update-in! map filter remove]]
            [clojure.string :refer [blank?]]))

(def contact-manager
  "Create a contact-manager module within Angular.js"
  (.module js/angular "contact-manager" (array "contact-manager.services"
                                               "contact-manager.controllers"
                                               "contact-manager.directives"
                                               "contact-manager.filters")))
