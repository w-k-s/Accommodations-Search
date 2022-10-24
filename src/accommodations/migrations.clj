(ns accommodations.migrations
  (:require [migratus.core :as migratus]
            [hikari-cp.core :as hk]))


(def datasource-options {:adapter "postgresql"
                         :url     (or (System/getenv "DB_URL")
                                      "jdbc:postgresql://localhost:5432/overlook")})

(def config {:store                :database
             :migration-dir        "migrations/"
             :init-in-transaction? false
             :migration-table-name "migrations"
             :db {:datasource (hk/make-datasource datasource-options)}})

(defn migrate
  []
  (migratus/init config)
  (migratus/migrate config))