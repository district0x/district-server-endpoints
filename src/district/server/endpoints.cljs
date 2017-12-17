(ns district.server.endpoints
  (:require
    [cljs.nodejs :as nodejs]
    [clojure.string :as string]
    [cognitect.transit :as transit]
    [district.server.endpoints.middleware.defaults :as middleware]
    [district.server.config :refer [config]]
    [mount.core :as mount :refer [defstate]]))

(declare start)
(declare stop)

(defstate endpoints
          :start (start (merge (:endpoints @config)
                               (:endpoints (mount/args))))
          :stop (stop endpoints))

(def *registered-routes* (atom {}))

(def transit-writer (transit/writer :json))
(def express (nodejs/require "express"))

(defn reg-endpoint! [method path & args]
  (swap! *registered-routes* assoc-in [method path] args))


(def reg-get! (partial reg-endpoint! :get))
(def reg-post! (partial reg-endpoint! :post))
(def reg-put! (partial reg-endpoint! :put))
(def reg-delete! (partial reg-endpoint! :delete))


(defn status
  [response code]
  (.status response code))


(defn send
  ([res data]
   (.format res (clj->js {"application/json" #(.send res (clj->js data))
                          "application/transit+json" #(.send res (transit/write transit-writer data))
                          "default" #(.send res (clj->js data))})))
  ([res status-code data]
   (-> res
     (status status-code)
     (send data))))


(defn query-params [req]
  (aget req "query"))


(defn route-params [req]
  (js->clj (aget req "params") :keywordize-keys true))

(defn body [req]
  (aget req "body"))


(defn stop [api-server]
  (.close (:server @api-server)))


(defn- error-middleware? [f]
  (when (fn? f)
    (= (aget f "length") 4)))


(defn- install-middlewares! [app middlewares]
  (doseq [middleware middlewares]
    (.use app middleware)))


(defn- install-routes! [app routes]
  (doseq [[method routes] routes]
    (doseq [[path args] routes]
      (apply js-invoke app (name method) path args))))


(defn start [{:keys [:port :default-middlewares :middlewares]
              :or {default-middlewares (keys middleware/default-middlwares)}}]
  (let [app (express)
        middlewares (flatten middlewares)]
    (install-middlewares! app (map middleware/default-middlwares default-middlewares))
    (install-middlewares! app (remove error-middleware? middlewares))
    (install-routes! app @*registered-routes*)
    (install-middlewares! app (filter error-middleware? middlewares))
    {:app app :server (.listen app port)}))


