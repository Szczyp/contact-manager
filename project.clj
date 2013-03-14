(defproject contact-manager "0.1.0-SNAPSHOT"
  :description "Contact Manager - clojure/clojurescript research project with angularjs and websockets"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.5.0"]
                 [com.datomic/datomic-free "0.8.3826"]
                 [ring "1.1.8"]
                 [ring/ring-json "0.2.0"]
                 [aleph "0.3.0-beta11"]
                 [compojure "1.1.5"]
                 [com.cemerick/friend "0.1.3"]
                 [hiccup "1.0.2"]
                 [cheshire "5.0.2"]
                 [ccfontes/hiccup-bootstrap "0.1.3"]]
  :min-lein-version "2.0.0"
  :plugins [[lein-cljsbuild "0.3.0"]]
  :source-paths ["src/clj"]
  :cljsbuild {:builds {:main {:source-paths ["src/cljs"]
                              :compiler {:output-to "resources/public/js/app.js"
                                         :pretty-print true}
                              :jar true}}}
  :main contact-manager.server)
