(defproject booking "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/java.jdbc "0.7.12"]
                 [org.postgresql/postgresql "42.5.0"]
                 [ring/ring-defaults "0.3.4"]
                 [ring/ring-devel "1.9.6"]
                 [ring/ring-json "0.5.1"]
                 [compojure "1.6.3"]
                 [prismatic/schema "1.4.1"]
                 [phrase "0.3-alpha4"]
                 [http-kit "2.6.0"]]
  :repl-options {:init-ns accommodations.core})
