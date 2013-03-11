(defproject contact-manager "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.5.0"]
                 [com.datomic/datomic-free "0.8.3826"]
                 [ring "1.1.8"]
                 [aleph "0.3.0-beta11"]
                 [compojure "1.1.5"]
                 [com.cemerick/friend "0.1.3"]
                 [hiccup "1.0.2"]
                 [cheshire "5.0.2"]
                 [ccfontes/hiccup-bootstrap "0.1.3"]]
  :plugins [[lein-cljsbuild "0.3.0"]]
  :hooks [leiningen.cljsbuild]
  :source-paths ["src/clj"]
  :cljsbuild {:builds {:main {:source-paths ["src/cljs"]
                              :compiler {:output-to "resources/public/js/app.js"
                                         :optimizations :simple
                                         :pretty-print true}
                              :jar true}}}
  :main contact-manager.server)
