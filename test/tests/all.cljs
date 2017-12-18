(ns tests.all
  (:require
    [cljs-http.client :as client]
    [cljs.core.async :refer [<!]]
    [cljs.nodejs :as nodejs]
    [cljs.test :refer-macros [deftest is testing use-fixtures async]]
    [district.server.endpoints :refer [reg-get! reg-post! query-params send body]]
    [mount.core :as mount])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(set! js/XMLHttpRequest (nodejs/require "xhr2"))

(use-fixtures
  :each
  {:after
   (fn []
     (mount/stop))})

(deftest test-get-endpoint
  (async done
    (go
      (reg-get! "/my-endpoint"
                (fn [req res]
                  (if (:throw? (query-params req))
                    (send res 400 "Bad Request")
                    (send res (query-params req)))))

      (-> (mount/with-args
            {:endpoints {:port 6239}})
        (mount/start))


      (is (= (:body (<! (client/get "http://localhost:6239/my-endpoint"
                                    {:headers {"Accept" "application/transit+json"}
                                     :query-params {:greeting :greeting/hi}})))
             {:greeting :greeting/hi}))

      (is (= (:status (<! (client/get "http://localhost:6239/my-endpoint"
                                      {:headers {"Accept" "application/transit+json"}
                                       :query-params {:throw? true}})))
             400))
      (done))))

(deftest test-post-endpoint
  (async done
    (go
      (reg-post! "/my-endpoint"
                 (fn [req res]
                   (if (:throw? (body req))
                     (send res 400 "Bad Request")
                     (send res (body req)))))

      (-> (mount/with-args
            {:endpoints {:port 6239}})
        (mount/start))


      (is (= (:body (<! (client/post "http://localhost:6239/my-endpoint"
                                     {:headers {"Accept" "application/transit+json"}
                                      :transit-params {:word/greeting :greeting/hi}})))
             {:word/greeting :greeting/hi}))

      (is (= (:status (<! (client/post "http://localhost:6239/my-endpoint"
                                       {:headers {"Accept" "application/transit+json"}
                                        :transit-params {:throw? true}})))
             400))
      (done))))
