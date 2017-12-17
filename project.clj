(defproject district0x/district-server-endpoints "1.0.0"
  :description "district0x server component for setting up API endpoints"
  :url "https://github.com/district0x/district-server-endpoints"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[com.cognitect/transit-cljs "0.8.243"]
                 [com.taoensso/timbre "4.10.0"]
                 [district0x/district-server-config "1.0.0"]
                 [mount "0.1.11"]
                 [org.clojure/clojurescript "1.9.946"]]

  :npm {:dependencies [[express "4.15.3"]]})
