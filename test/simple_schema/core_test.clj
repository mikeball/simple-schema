(ns simple-schema.core-test
  (:require [clojure.test :refer :all]
            [simple-schema.core :refer :all]))


;; (deftest declared-parameters-and-returns-are-required
;;   (is (thrown? Exception (breakout-signature [])))
;;   (is (thrown? Exception (breakout-signature ['=>])))
;;   (is (thrown? Exception (breakout-signature ['+s]))))


;; (deftest signatures-are-broken-out
;;   (are [given expected]
;;        (= (breakout-signature given) expected)

;;        '[=> +person +nil]
;;        '[() (+person +nil)]

;;        '[+s +i => +person +nil]
;;        '[(+s +i) (+person +nil)]

;;        ))




;; (deftest parameters-must-begin-with-plus
;;   (is (thrown? Exception (compile-param-name ['s])))
;;   (is (thrown? Exception (compile-param-name ['+s.first.last]))))


;; (deftest parameter-names-are-compiled
;;   (are [given expected]
;;        (= (compile-param-name given) expected)

;;        '+s      ['+s 's]
;;        '+i      ['+i 'i]
;;        '+s.name ['+s 'name]
;;        '+i.age  ['+i 'age]

;;        ))



;; (run-tests *ns*)



