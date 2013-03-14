(ns contact-manager.contacts
  (:require [contact-manager.data :as d])
  (:use [compojure.core]
        [hiccup.form]
        [contact-manager views broadcast]))

(def contact-form
  [:form {:ng-controller "ContactFormCtrl"}
   (text-field {:class "input-block-level"
                :placeholder "name"
                :ng-model "contact.name"
                :required ""}
               "name")
   (email-field {:class "input-block-level"
                 :placeholder "email"
                 :ng-model "contact.email"}
                "email")
   (text-field {:class "input-block-level"
                :placeholder "phone"
                :ng-model "contact.phone"}
               "phone")
   [:button {:class "btn btn-primary"
             :ng-click "save()"}
    "Save"]
   [:button {:class "btn btn-primary"
             :ng-click "cancel()"}
    "Cancel"]])

(def main-page
  (master-template
   "Contacts"
   [:div {:ng-controller "ContactsCtrl"}
    contact-form
    [:ul
     [:li {:ng-repeat "contact in contacts"}
      [:span "Name: {{ contact.name }} Email: {{ contact.email }} Phone: {{ contact.phone }}"]
      [:i {:class "icon-remove"
           :ng-click "remove(contact)"}]
      [:i {:class "icon-edit"
           :ng-click "edit(contact)"}]]]]))

(defn save-contact! [contact]
  (let [cmd  (if (:id contact) "contact:change" "contact:add")]
    (->> contact
         (d/save! :contact)
         (enqueue cmd)))
  {})

(defn delete-contact! [id]
  (d/delete! id)
  (enqueue "contact:remove" id)
  {})

(defn get-contacts []
  (d/get-all '[:find ?e :where [?e :contact/name _]]))

(defroutes contact-routes
  (GET "/" [] main-page)
  (GET "/all" [] (get-contacts))
  (POST "/" {contact :body} (save-contact! contact))
  (context "/:id" [id]
           (if-let [id (Long/parseLong id)]
             (defroutes id-routes
               (GET "/" [] (d/get id))
               (DELETE "/" [] (delete-contact! id))))))
