(ns accommodations.core
  (:gen-class)
  (:use [accommodations.db :refer [datasource]]
        [accommodations.service]
        [compojure.core :only [DELETE GET POST PUT defroutes]]
        [db.migrate :as db]
        [org.httpkit.server]
        [ring.middleware.defaults :refer :all]
        [ring.middleware.json :only [wrap-json-body wrap-json-params wrap-json-response]] ;this will map json keys to keywords
        [ring.middleware.keyword-params :only [wrap-keyword-params]]
        [ring.util.response :only [response]])
  (:import (clojure.lang ExceptionInfo)))

(defn wrap-fallback-exception
  [handler]
  (fn [request]
    (try
      (handler request)
      (catch ExceptionInfo e
        (case (-> e ex-data :type)
          :bad-request {:status 400 :body {:error (-> e ex-message)} })))))



(defroutes all-routes
           (GET "/ping" [] (response {:data "ok"}))
           (POST "/search" {req :params}
             (let [units (search-rooms-by-req req)]
               {:status 201 :body {:units units}})))

(def app
  (-> all-routes
      (wrap-json-body {:keywords? true :bigdecimals? true})
      (wrap-fallback-exception)
      (wrap-keyword-params)
      (wrap-json-params)
      (wrap-json-response)))

(defn -main [& args]
  (db/migrate datasource)
  (run-server app {:port 8080}
  (println (str "Running webserver at http:/127.0.0.1:" 8080 "/"))))
