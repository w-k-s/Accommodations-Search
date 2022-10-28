(ns accommodations.db
  (:use [hikari-cp.core :as hk]))

(def datasource-options {:adapter "postgresql"
                         :url     (or (System/getenv "DB_URL")
                                      "jdbc:postgresql://localhost:5432/overlook")})

(defonce datasource (hk/make-datasource datasource-options))