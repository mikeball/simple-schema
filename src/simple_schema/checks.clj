(ns simple-schema.checks
  (:require [clojure.string :as string]
            [simple-schema.types :as types])
  (:import [simple_schema.types.Any]))


(defprotocol ValueChecks
  (value-check [value constraints]))

(extend-protocol ValueChecks
  java.lang.String
  (value-check [value constraints]
    (if-not constraints true
      (->> constraints
           (map (fn [c] (cond (= c :required)
                              (not (string/blank? value)))))
           (not-any? false?)))))

;; (value-check "x" [:required])
;; (value-check "" [:required [:len 3 5]])

;; syntax ideas...
;; (value-check "" required+)
;; (value-check "" (len+ 3 5))
;; (value-check 1 positive+)
;; (value-check 1 (+range 3 5) (+in 1 2 5))


; move value to first parameter to match other functions??
(defn check-prop [prop-schema value]
  (let [prop-seq        (sequential? prop-schema)
        prop-type       (if prop-seq (first prop-schema) prop-schema)
        prop-contraints (if (sequential? prop-schema) (seq (rest prop-schema)))
        value-type      (class value)]
    (and (= prop-type value-type)
         (if-not prop-contraints true
           (value-check value prop-contraints)))))

;; (check-prop +s "")
;; (check-prop [+s :required] "x")
;; (check-prop [+s :required] 1)

;; syntax ideas
;; (check-prop +s "")
;; (check-prop (+s +required) "x")
;; (check-prop (+s +required) 1)



(defprotocol SchemaCheck
  (check [schema value]))

(extend-protocol SchemaCheck

  nil
  (check [schema value]
         (= value nil))

  java.lang.Class
  (check [schema value]
         (or (= schema (class value))
             (= schema simple_schema.types.Any)))

  clojure.lang.PersistentArrayMap
  (check [schema value]
    (let [schema-keys (keys schema)
          schemas (map schema schema-keys)
          values (map value schema-keys)]
      (->> values
           (map check-prop schemas)
           (not-any? false?))))

  )

;; (check {:first-name +s    :last-name [+s :required] }
;;        {:first-name "bob" :last-name ""})
;; ;; (check +s "bob")
;; ;; (check +s 3)


;; (check {:first-name +s    :last-name (+s +required) }
;;        {:first-name "bob" :last-name "xxx"})
;; (check +s "bob")
;; (check java.lang.String "x")

;; (some true? (map #(check % "") [+s +b]))





;; ***************************************************


(defn check-param [ptype pname pvalue]
  (cond (check ptype pvalue) true

        :default
        (throw (IllegalArgumentException.
            (str "Parameter " pname
                 " with value " pvalue
                 " is not valid.")))))


