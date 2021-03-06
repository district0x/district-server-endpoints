(defproject district0x/district-server-endpoints "1.0.3"
  :description "district0x server component for setting up API endpoints"
  :url "https://github.com/district0x/district-server-endpoints"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[com.cognitect/transit-cljs "0.8.243"]
                 [district0x/district-server-config "1.0.1"]
                 [mount "0.1.11"]
                 [org.clojure/clojurescript "1.9.946"]]

  :npm {:dependencies [[express "4.15.3"]
                       [cors "2.8.4"]
                       [body-parser "1.18.2"]]
        :devDependencies [[ws "2.0.1"]
                          [xhr2 "0.1.4"]]}

  :figwheel {:server-port 4682}

  :profiles {:dev {:dependencies [[org.clojure/clojure "1.8.0"]
                                  [com.cemerick/piggieback "0.2.2"]
                                  [figwheel-sidecar "0.5.14"]
                                  [org.clojure/tools.nrepl "0.2.13"]
                                  [cljs-http "0.1.43"]
                                  [district0x/district-parsers "1.0.0"]]
                   :plugins [[lein-cljsbuild "1.1.7"]
                             [lein-figwheel "0.5.14"]
                             [lein-npm "0.6.2"]
                             [lein-doo "0.1.7"]]
                   :source-paths ["dev"]}}

  :cljsbuild {:builds [{:id "tests"
                        :source-paths ["src" "test"]
                        :figwheel {:on-jsload "tests.runner/-main"}
                        :compiler {:main "tests.runner"
                                   :output-to "tests-compiled/run-tests.js"
                                   :output-dir "tests-compiled"
                                   :target :nodejs
                                   :optimizations :none
                                   :source-map true}}]})
