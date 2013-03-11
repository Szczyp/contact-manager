(ns contact-manager.views
  (:use [ hiccup core page form]
        [ hiccup.bootstrap page element]))

(defn master-template [title & body]
  (html5
   [:head
    [:title title]
    (include-bootstrap)
    (include-css "/css/page.css" "/css/angular-ui.css")]
   [:body
    [:div#header
     [:h2 title]]
    [:div#content.container body]
    (include-js "/js/app.js" "/js/angular.js" "/js/angular-ui.js" "/js/ui-bootstrap.js")]))

(def index-page (master-template "Main" [:p#clickable
                                         (icon :ok)
                                         "Click me!"]))

(def login-page
  (master-template
   "Login"
   (form-to {:class "form-signin"} [:post "/login"]
                 (text-field {:class "input-block-level" :placeholder "user name"} "username")
                 (password-field {:class "input-block-level" :placeholder "password"} "password")
                 (submit-button {:class "btn btn-primary"} "Login"))))

(def account-page (master-template "Account"))

(def admin-page (master-template "Admin"))

(def page-not-found (master-template "Page not found"))

(def contact-form
  (form-to [:post "contact"]
           (text-field {:class "input-block-level" :placeholder "name"} "name")
           (email-field {:class "input-block-level" :placeholder "email"} "email")
           (text-field {:class "input-block-level" :placeholder "phone"} "phone")
           (submit-button {:class "btn btn-primary"} "Save")))
