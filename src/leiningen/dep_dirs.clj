(ns leiningen.dep-dirs
  (:require [clojure.tools.namespace.file :refer [clojure-file?
                                                  add-files]]
            [clojure.java.io :refer [as-file]]
            [clojure.pprint :refer [pprint]]))

(defn- find-files [directories]
  (->> directories
       (map #(->> (as-file %)
                  (file-seq)
                  (filter clojure-file?)))
       flatten))

(defn- dependencies-allowed?
  [dependencies allowed-dirs]
  (let [[dpt dpdcs] dependencies
        allowed-deps (get allowed-dirs dpt [])]
    (reduce
      (fn [v? dep]
        (if (some #{dep} allowed-deps)
          (and v? true)
          (do
            (printf "%s has illegal dependency: %s\n" dpt dep)
            false)))
      true
      dpdcs)))

(defn- check-for-violations
  [dependencies allowed-dirs]
  (reduce (fn [violation? dep]
            (if (dependencies-allowed? dep allowed-dirs)
              (or violation? false)
              true))
          false
          dependencies))

(defn dep-dirs
  [project & args]
  (let [all-deps (->> (:source-paths project)
                      (find-files)
                      (add-files {}))
        dependencies (-> all-deps
                         :clojure.tools.namespace.track/deps
                         :dependencies)
        allowed-dirs (-> project
                         :dep-dirs
                         :allowed)]
    (if (check-for-violations dependencies allowed-dirs) 1 0)
    (flush)))
