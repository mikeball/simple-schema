(ns simple-schema.core
  (:require [clojure.string :as string]))



(defn- compile-param-name [sym]
  (let [sym-string (str sym)]
    (if-not (.startsWith sym-string "+")
      (throw (IllegalArgumentException. (str "Parameter " sym-string " must begin with a plus sign."))))
    (let [pname (.substring sym-string 1 (.length sym-string))]
      (symbol pname))))

;; (compile-param-name (symbol "+s"))
;; (compile-param-name (symbol "+i"))
;; (map compile-param-name [(symbol "+s") (symbol "+i")])



;; define base types
(def +s java.lang.String)
(def +i java.lang.Long)
(def +b java.lang.Boolean)



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
;; (value-check "" +required)
;; (value-check "" (+len 3 5))
;; (value-check 1 +positive)
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

  java.lang.Class
  (check [schema value]
         (= schema (class value)))

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
;; (check +s 3)



;; (some true? (map #(check % "") [+s +b]))


(defmacro defn+ [fn-name params returns & body]
  ; params and returns must be a vector of symbols

  (let [param-syms  (apply vector (map compile-param-name params))
        param-names (apply vector (map str param-syms))
        param-check '(fn [ptype pvalue pname]
                         (if-not (check ptype pvalue)
                           (throw (IllegalArgumentException.
                                   (str "Parameter " pname " with value " pvalue " is not valid.")))))
        result            (gensym "result")

        ]
    `(def ~fn-name
      (fn ~param-syms
        (doall (map ~param-check ~params ~param-syms ~param-names))

          (let [~result ~@body] ; todo check return type

            (if (some true? (map #(check % ~result) ~returns))
              ~result
              (throw (IllegalArgumentException.
                                   (str "Return type is not valid!")))

              ))
         ))
    ))

;; (macroexpand
;;  '(defn+ hello [+s +i][+s]
;;     (str "hi " s)))


;; (defn+ hello [+s +i][+s]
;;   (str "hi " s " number " i))

;; (hello "bob" 222)
;; (hello "bob" 333)


;; (defn+ hello2 [+s +i][+i]
;;   "444")

;; (hello2 "" 2)




;; (defn +map get-params [transfer-info +solar-lead]
;;   {"MSM_firstname" (:first-name lead-details)
;;    "MSM_lastname"  (:last-name lead-details) })



;; (def +solar-lead
;;   {:firstname [+s :required]
;;    :lastname  [+s :required]})

;; +solar-lead


;; in defining a type, should we only use predicates, a-la truss?
;; (def +solar-lead
;;   {:firstname (+s (len? 2 5))
;;    :lastname  [+s required?]})







;; (def +name [+s [:len 1 50]])

;; (def-schema +solar-lead
;;   {:firstname [+name :required]
;;    :lastname  +name})

;; (def-schema +http-status
;;   [+enum 200 404 500])

;; (def-schema +tao-response
;;   [+http-status +map +string+fn])

;; (def-schema +tao-response
;;   [+int +map +string])


;; (def-schema +int-list
;;   [+i...])

;; (def-schema +lead-list
;;   +lead*seq)
