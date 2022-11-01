(ns overlook.units.search
  (:gen-class)
  (:require [overlook.db :refer [datasource]]
            [clojure.spec.alpha :as s]
            [expound.alpha :as expound]
            [honey.sql]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [next.jdbc.sql :as sql]))

;; next.jdbc, by default returns each field qualified with the table name
;; e.g. the field `name` in the table person, would be returned as `person/name`.
;; The line below removes the qualifier i.e. `person/name` -> `name`
(def ds-opts (jdbc/with-options datasource {:builder-fn rs/as-unqualified-lower-maps}))

(def date-regex #"^[0-9]{4}\-[0-9]{2}-[0-9]{2}$$")
(s/def :search/city string?)
(expound/defmsg :search/city "City is required")

(s/def :search/apartment-type #{"1bdr" "2bdr" "3bdr"})
(expound/defmsg :search/apartment-type "Apartment type can be either 1bdr, 2bdr or 3bdr")

(s/def :search/amenity  #{"WiFi", "Pool", "Garden", "Tennis table", "Parking"})
(expound/defmsg :search/amenity "Unknown amenity")
(s/def :search/amenities (s/* :search/amenity))

(s/def :search/start-date #(re-matches date-regex %))
(expound/defmsg :search/start-date "Invalid date")

(s/def :search/end-date #(re-matches date-regex %))
(expound/defmsg :search/end-date "Invalid date")

(s/def :search/date (s/keys :req [:search/start-date :search/end-date]))
(expound/defmsg :search/date "start and end date are required")

(s/def :search/type #{"weekend", "week", "month"})
(expound/defmsg :search/type "Type must be `week`,`weekend` or `month`")

(s/def :search/month  #{"jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"})
(expound/defmsg :search/month "Invalid month")

(s/def :search/months (s/+ :search/month))
(expound/defmsg :search/months "A minimum of one month is required")

(s/def :search/flexible (s/keys :req [:search/type :search/months]))
(s/def :search/params (s/keys :req [:search/city :search/apartment-type :search/amenities (or :search/date :search/flexible)]))

(defn validate-search-params-req
  [req]
  (let [params (conj #:search{:city           (:city req)
                              :apartment-type (:apartmentType req)
                              :amenities      (:amenities req)}
                     (if (contains? req :date) {:search/date
                                                {:search/start-date (:start (:date req))
                                                 :search/end-date   (:end (:date req))}}
                                               {:search/flexible
                                                {:search/type   (:type (:flexible req))
                                                 :search/months (:months (:flexible req))}}))]
    (when-not (s/valid? :search/params params)
      (throw (ex-info (expound/expound-str :search/params params) {:type :bad-request})))))

(defn get-matching-property-types
  [apartment-type]
  (case apartment-type
    "1bdr" ["1bdr" "2bdr" "3bdr"]
    "2bdr" ["2bdr" "3bdr"]
    "3bdr" ["3bdr"]))

(defn find-units
  [req]
  (let [amenities (if (not-empty (:amenities req)) (clojure.string/join "," (:amenities req)) nil)
        property-types (get-matching-property-types (:apartmentType req))
        selection (conj [:and [:= [:cast :b.city :text] (:city req)]]
                          (when (not-empty (:amenities req)) [:raw (format "p.amenities @> '{%s}'" amenities )])
                          [:in [:cast :p.property-type :text] property-types])
        sqlmap {:select  [:p.id :p.title :p.property-type :b.city :a.start-date :a.end-date]
                :from    [[:test.property :p]]
                :join-by [:left [[:test.building :b] [:= :b.id :p.building-id]]
                          :left [[:test.availability :a] [:= :a.property-id :p.id]]]
                :where   selection}
        query (honey.sql/format sqlmap {:pretty true})]
    (println "query " query)
  (sql/query ds-opts query {:return-keys true})))

(defn search-rooms-by-req
  [req]
  (validate-search-params-req req)
  (find-units req))
