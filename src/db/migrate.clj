(ns db.migrate
  (:require [migratus.core :as migratus]))

(defn load-config
  [datasource]
  {:store                :database
   :migration-dir        "migrations/"
   :init-in-transaction? false
   :migration-table-name "migrations"
   :db {:datasource datasource}})

(defn migrate
  [datasource]
  (let [config (load-config datasource)]
  (migratus/init config)
  (migratus/migrate config)))