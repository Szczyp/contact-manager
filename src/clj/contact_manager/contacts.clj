(ns contact-manager.contacts
  (:use [compojure.core]
        [datomic.api :only (q)]
        [hiccup.form]
        [contact-manager data views broadcast]))

(def contact-form
  [:form {:ng-controller "ContactFormCtrl"
          :ng-submit "addContact()"}
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
   (submit-button {:class "btn btn-primary"}
                  "Add")])

(def main-page
  (master-template
   "Contacts"
   [:div {:ng-controller "ContactsCtrl"}
    contact-form
    [:ul
     [:li {:ng-repeat "contact in contacts"}
      "Name: {{ contact.name }} Email: {{ contact.email }} Phone: {{ contact.phone }}"]]]))

(defn get-contact [id] {})

(defn add-contact! [m] (save! :contact m) m)

(defn delete-contact! [id] {})

(defn get-contacts []
  (let [db (get-db)]
    (->> (q '[:find ?e :where [?e :contact/name _]] db)
         (get-all db))))

(defroutes contact-routes
  (GET "/" [] main-page)
  (GET "/all" [] (get-contacts))
  (POST "/" {contact :body}
        (->> contact
             add-contact!
             (enqueue "contact:add"))
        {})
  (context "/:id" [id]
           (defroutes id-routes
             (GET "/" [] (get-contact id))
             (DELETE "/" [] (delete-contact! id)))))
