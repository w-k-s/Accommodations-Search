(defproject booking "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/java.jdbc "0.7.12"]
                 [ring/ring-defaults "0.3.4"]
                 [ring/ring-devel "1.9.6"]
                 [ring/ring-json "0.5.1"]
                 [compojure "1.6.3"]
                 [prismatic/schema "1.4.1"]
                 [org.clojure/tools.logging "1.2.4"]
                 ; No need to specify slf4j-api, it’s required by logback
                 [ch.qos.logback/logback-classic "1.4.4"]
                 [hikari-cp "3.0.0"]
                 [expound "0.9.0"]
                 [http-kit "2.6.0"]
                 [migratus "1.4.4"]
                 [org.postgresql/postgresql "42.5.0"]
                 [com.github.seancorfield/next.jdbc "1.2.796"]]
  :repl-options {:init-ns accommodations.core}
  :plugins [[migratus-lein "0.7.3"]])
