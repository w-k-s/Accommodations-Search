(ns accommodations.core
  (:gen-class)
  (:use [compojure.core :only [defroutes GET POST PUT DELETE]]
        [ring.util.response :only [response]]
        [accommodations.service]
        [ring.middleware.json :only [wrap-json-params wrap-json-response wrap-json-body]]
        [ring.middleware.keyword-params :only [wrap-keyword-params]] ;this will map json keys to keywords
        [ring.middleware.defaults :refer :all]
        org.httpkit.server)
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
             (let [t (search-rooms-by-req req)]
               {:status 201 :body {:id "t"}})))

(def app
  (-> all-routes
      (wrap-json-body {:keywords? true :bigdecimals? true})
      (wrap-fallback-exception)
      (wrap-keyword-params)
      (wrap-json-params)
      (wrap-json-response)))

(defn -main [& args]
  (run-server app {:port 8080}
  (println (str "Running webserver at http:/127.0.0.1:" 8080 "/"))))
