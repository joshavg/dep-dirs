(defproject dep-dirs "0.1.0-SNAPSHOT"
  :description "Dependency direction enforcer"
  :url "https://github.com/joshavg/dep-dirs"
  :license {:name "GNU GPL v3"
            :url  "https://www.gnu.org/licenses/gpl-3.0.html"}
  :eval-in-leiningen true
  :dependencies [[org.clojure/tools.namespace "0.2.11"]]
  :dep-dirs {:allowed         {gitwatch-cloj.core [gitwatch-cloj.config]}
             :ignore-external true})
