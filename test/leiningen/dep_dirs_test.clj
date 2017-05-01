(ns leiningen.dep-dirs-test
  (:require [clojure.test :refer :all]
            [leiningen.dep-dirs :refer :all]))

(def deps
  {"own-ns.ns1" ["ext-ns1" "own-ns.ns2"]
   "own-ns.ns2" ["ext-ns1" "ext-ns2"]})

(def internal-ns
  ["own-ns.ns1" "own-ns.ns2"])

(deftest test-filter-external-ns
  (testing "passing true filters external namespaces"
    (is (= (filter-external-ns deps internal-ns true)
           {"own-ns.ns1" ["own-ns.ns2"]
            "own-ns.ns2" []})))
  (testing "passing false leaves external namespaces"
    (is (= (filter-external-ns deps internal-ns false)
           deps))))
