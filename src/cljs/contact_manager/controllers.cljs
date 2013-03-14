(ns contact-manager.controllers
  (:refer-clojure :exclude [assoc! map filter remove])
  (:require [contact-manager.utils :refer [assoc! update-in! map filter remove]])
  (:use [goog.array :only (removeIf find)]))

(->
 (.module js/angular "contact-manager.controllers" (array "contact-manager.services"))

 (.controller "AppCtrl"
              (fn [$scope socket]
                $scope))

 (.controller "ContactsCtrl"
              (fn [$scope $http]
                (-> (.get $http "/contacts/all")
                    (.success #(assoc! $scope :contacts %)))
                (assoc! $scope
                        :remove #($http (js-obj "method" "DELETE"
                                                "url" (str "/contacts/" (:id %))))
                        :edit #(.$broadcast $scope "contact:edit" %))
                (.$on $scope "contact:add" #(conj! (:contacts $scope) %2))
                (.$on $scope "contact:remove"
                      (fn [_ id] (removeIf (:contacts $scope) #(= (:id %) id))))
                (.$on $scope "contact:change"
                      (fn [_ nc]
                        (if-let [c (find (:contacts $scope) #(= (:id %) (:id nc)))]
                          (js/angular.extend c nc))))))

 (.controller "ContactFormCtrl"
              (fn [$scope $http]
                (let [empty-contact #(js-obj "name" ""
                                             "email" ""
                                             "phone" "")
                      reset-contact! #(assoc! $scope :contact (empty-contact))]
                  (assoc! $scope
                          :save #(let [contact (:contact $scope)]
                                  (.post $http "/contacts" contact)
                                  (assoc! $scope :old-contact nil)
                                  (reset-contact!))
                          :cancel (fn []
                                    (.$emit $scope "contact:change" (:old-contact $scope))
                                    (reset-contact!)))
                  (.$on $scope "contact:edit"
                        (fn [_ c]
                          (assoc! $scope :old-contact (js/angular.extend (empty-contact) c))
                          (assoc! $scope :contact c)))
                  (reset-contact!)))))
