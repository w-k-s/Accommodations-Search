(ns accommodations.service
  (:gen-class)
  (:require [clojure.spec.alpha :as s])
  (:import (java.time LocalDate)))

(s/def :search/city string?)
(s/def :search/apartment-type #{"1bdr" "2bdr" "3bdr"})
(s/def :search/amenity #{"WiFi", "Pool", "Garden", "Tennis table", "Parking"})
(s/def :search/amenities (s/* :search/amenity))
(s/def :search/start-date (s/nilable #(instance? LocalDate %)))
(s/def :search/end-date (s/nilable #(instance? LocalDate %)))
(s/def :search/params (s/keys :req [:search/city :search/apartment-type]
                                                  :opt [:search/start-date :search/end-date]))

(defn validate-search-params-req
  [req]
  (if (and (contains? req :date) (contains? req :flexible))
    (throw (ex-info "Search can contain date and flexible, but not both" {:type :bad-request}))
    (let [params #:search{:city       (:city req)
                                    :start-date (when (contains? req :date) (LocalDate/parse (str (:start (:date req)))))
                                    :end-date (when (contains? req :date) (LocalDate/parse (str (:end (:date req)))))
                                    :apartment-type (:apartmentType req)
                                    :amenities (:amenities req)}]
      (if-not (s/valid? :search/params params)
        (throw (ex-info (str (s/explain-data :search/params params)) {:type :bad-request}))))))

(defn search-rooms-by-req
  [req]
  (validate-search-params-req req))


