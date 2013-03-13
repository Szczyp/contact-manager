(ns contact-manager.views
  (:use [ hiccup core page form]
        [ hiccup.bootstrap page element]))

(defn master-template [title & body]
  (html
   (doctype :html5)
   [:html {:ng-app "contact-manager"}
    [:head
     [:title title]
     (include-bootstrap)
     (include-css "/css/app.css"
                  "/css/angular-ui.css")]
    [:body {:ng-controller "AppCtrl"}
     [:div#header
      [:h2 title]]
     [:div#content.container body]
     (include-js "/js/angular.js"
                 "/js/angular-ui.js"
                 "/js/ui-bootstrap.js"
                 "/js/app.js")]]))

(def login-page
  (master-template
   "Login"
   (form-to {:class "form-signin"} [:post "/login"]
            (text-field {:class "input-block-level"
                         :placeholder "user name"}
                        "username")
            (password-field {:class "input-block-level"
                             :placeholder "password"}
                            "password")
            (submit-button {:class "btn btn-primary"}
                           "Login"))))

(def page-not-found (master-template "Page not found"))
