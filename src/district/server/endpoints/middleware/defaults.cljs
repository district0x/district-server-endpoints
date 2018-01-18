(ns district.server.endpoints.middleware.defaults
  (:require
    [cljs.nodejs :as nodejs]
    [clojure.string :as string]
    [cognitect.transit :as transit]))

(def transit-reader (transit/reader :json))
(def type-is (nodejs/require "type-is"))

(def cors (nodejs/require "cors"))
(def body-parser (nodejs/require "body-parser"))

(defn transit-body-parser [req resp next]
  (when (type-is req "application/transit+json")
    (aset req "body" (transit/read transit-reader (aget req "body"))))
  (next))

(defn query-params-parser [req resp next]
  (aset req "query" (js->clj (aget req "query") :keywordize-keys true))
  (next))

(def default-middlwares
  {:middleware/cors (cors)
   :middleware/urlencoded (.urlencoded body-parser #js {:extended true})
   :middleware/text-body-parser-for-transit (.text body-parser #js {:type "application/transit+json"})
   :middleware/transit-body-parser transit-body-parser
   :middleware/query-params-parser query-params-parser})