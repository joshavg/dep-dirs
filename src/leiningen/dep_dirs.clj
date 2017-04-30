(ns leiningen.dep-dirs
  (:require [clojure.tools.namespace.file :refer [clojure-file?
                                                  add-files]]
            [clojure.java.io :refer [as-file]]
            [clojure.pprint :refer [pprint]]
            [leiningen.core.main :refer [abort]]))

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

(defn filter-external-ns
  [deps internal-ns ignore-external]
  (if ignore-external
    (reduce
      (fn [newdeps entry]
        (let [[dpt dpdcs] entry]
          (assoc
            newdeps
            dpt
            (filter #(some #{%} internal-ns) dpdcs))))
      {}
      deps)
    deps))

(defn dep-dirs
  [project & args]
  (let [ignore-external (-> project
                            :dep-dirs
                            (get :ignore-external true))
        all-deps (->> (:source-paths project)
                      (find-files)
                      (add-files {}))
        internal-ns (:clojure.tools.namespace.track/load
                      all-deps)
        dependencies (-> all-deps
                         :clojure.tools.namespace.track/deps
                         :dependencies
                         (filter-external-ns internal-ns ignore-external))
        allowed-dirs (-> project
                         :dep-dirs
                         (get :allowed {}))]
    (let [v? (check-for-violations dependencies allowed-dirs)]
      (flush)
      (when v?
        (abort)))))
