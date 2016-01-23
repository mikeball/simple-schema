(ns simple-schema.core-test
  (:require [clojure.test :refer :all]
            [simple-schema.core :refer :all]))


(deftest declared-parameters-and-returns-are-required
  (is (thrown? Exception (breakout-signature [])))
  (is (thrown? Exception (breakout-signature ['=>])))
  (is (thrown? Exception (breakout-signature ['+s]))))


(deftest signatures-are-broken-out
  (are [given expected]
       (= (breakout-signature given) expected)

       '[=> +person +nil]
       '[() (+person +nil)]

       '[+s +i => +person +nil]
       '[(+s +i) (+person +nil)]

       ))



;; (run-tests *ns*)



