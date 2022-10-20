(ns accommodations.service
  (:gen-class)
  (:require [clojure.spec.alpha :as s])
  (:import (java.time LocalDate)))

(def date-regex #"^[0-9]{4}\-[0-9]{2}-[0-9]{2}$$")
(s/def :search/city string?)
(s/def :search/apartment-type #{"1bdr" "2bdr" "3bdr"})
(s/def :search/amenities (s/* #{"WiFi", "Pool", "Garden", "Tennis table", "Parking"}))
(s/def :search/start-date #(re-matches date-regex %))
(s/def :search/end-date #(re-matches date-regex %))
(s/def :search/date (s/keys :req [:search/start-date :search/end-date]))
(s/def :search/type #{"weekend", "week", "month"})
(s/def :search/months #{"jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"})
(s/def :search/flexible (s/keys :req [:search/type :search/months]))
(s/def :search/params (s/keys :req [:search/city :search/apartment-type :search/amenities (or :search/date :search/flexible)]))

(defn validate-search-params-req2
  [req]
  {:pre [(s/valid?
           :search/params
           (conj #:search{:city           (:city req)
                          :apartment-type (:apartmentType req)
                          :amenities      (:amenities req)}
                 (if (contains? req :date) {:search/date
                                            {:search/start-date (:start (:date req))
                                             :search/end-date   (:end (:date req))}}
                                           {:search/flexible
                                            {:search/type   (:type (:flexible req))
                                             :search/months (:months (:flexible req))}})))]}
  ())

(defn search-rooms-by-req
  [req]
  (validate-search-params-req2 req))