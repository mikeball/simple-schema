(ns simple-schema.checks-test
  (:require [clojure.test :refer :all]
            [simple-schema.checks :refer :all]))




;; (deftest params-are-checked
;;   (are [given expected]
;;        (= (apply check-param given) expected)

;;        '[java.lang.String "+s"]
;;        '+s nil
;;        '[() (+person +nil)]

;;        '[+s +i => +person +nil]
;;        '[(+s +i) (+person +nil)]

;;        ))





(comment
  (run-tests *ns*))



