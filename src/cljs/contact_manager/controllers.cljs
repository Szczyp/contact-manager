(ns contact-manager.controllers
  (:refer-clojure :exclude [assoc! map filter remove])
  (:require [contact-manager.utils :refer [assoc! update-in! map filter remove]]))

(->
 (.module js/angular "contact-manager.controllers" (array "contact-manager.services"))
 (.controller "AppCtrl"
              (fn [$scope socket]
                $scope))
 (.controller "ContactsCtrl"
              (fn [$scope $http]
                (-> (.get $http "/contacts/all")
                    (.success #(assoc! $scope :contacts %)))
                (.$on $scope "contact:add" #(conj! (:contacts $scope) %2))))
 (.controller "ContactFormCtrl"
              (fn [$scope $http]
                (let [reset-contact! (fn []
                                       (assoc! $scope
                                               :contact (js-obj "name" ""
                                                                "email" ""
                                                                "phone" "")))]
                  (assoc! $scope
                          :addContact (fn []
                                        (let [contact (:contact $scope)]
                                          (.post $http "/contacts" contact)
                                          (reset-contact!))))
                  (reset-contact!)))))
