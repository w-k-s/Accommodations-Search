(ns accommodations.service-test
  (:require [clojure.test :refer :all])
  (:require [accommodations.service :refer [validate-search-params-req]])
  (:import (clojure.lang ExceptionInfo)))

(deftest search-params-validation
  (testing "GIVEN search params WHEN city not present THEN invalid city"
    (let [req {:date {:start "2021-06-01" :end "2021-06-03"} :apartmentType "3bdr" :amenities []}]
      (is (thrown? ExceptionInfo (validate-search-params-req req)))))
  (testing "GIVEN search params WHEN apartment type not present THEN error"
    (let [req {:city "Dubai" :date {:start "2021-06-01" :end "2021-06-03"} :amenities []}]
      (is (thrown? ExceptionInfo (validate-search-params-req req)))))
  (testing "GIVEN search params WHEN apartment type not supported THEN error"
    (let [req {:city "Dubai" :date {:start "2021-06-01" :end "2021-06-03"} :apartmentType "0bdr" :amenities []}]
      (is (thrown? ExceptionInfo (validate-search-params-req req)))))
  (testing "GIVEN search params WHEN amenity invalid THEN error"
    (let [req {:city "Dubai" :date {:start "2021-06-01" :end "2021-06-03"} :apartmentType "1bdr" :amenities ["jacuzzi"]}]
      (is (thrown? ExceptionInfo  (validate-search-params-req req)))))
  (testing "GIVEN search params WHEN flexible type not present THEN error"
    (let [req {:city "Dubai" :apartmentType "1bdr" :amenities [] :flexible {}}]
      (is (thrown? ExceptionInfo (validate-search-params-req req)))))
  (testing "GIVEN search params WHEN flexible type invalid THEN error"
    (let [req {:city "Dubai" :apartmentType "1bdr" :amenities [] :flexible {:type "?"}}]
      (is (thrown? ExceptionInfo (validate-search-params-req req)))))
  (testing "GIVEN search params WHEN flexible months empty THEN error"
    (let [req {:city "Dubai" :apartmentType "1bdr" :amenities [] :flexible {:type "week" :months []}}]
      (is (thrown? ExceptionInfo (validate-search-params-req req)))))
  (testing "GIVEN search params WHEN flexible months contains invalid month THEN error"
    (let [req {:city "Dubai" :apartmentType "1bdr" :amenities [] :flexible {:type "week" :months ["bob"]}}]
      (is (thrown? ExceptionInfo (validate-search-params-req req)))))
  (testing "GIVEN search params WHEN flexible params valid THEN nil"
    (let [req {:city "Dubai" :apartmentType "1bdr" :amenities [] :flexible {:type "week" :months ["jan"]}}]
      (is (= nil (validate-search-params-req req))))))


