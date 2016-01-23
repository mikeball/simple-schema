;; taoclj.simple-schema
(ns simple-schema.core
  (:require [clojure.string :as string]))




(defn breakout-signature [params]
  (let [arrow-at (.indexOf params '=>)]
    (cond (= arrow-at -1)
          (throw (Exception. "Parameter declaration must contain =>"))

          :default
          (let [input (take arrow-at params)
                result (drop (+ arrow-at 1) params)]

            (if (= 0 (count result))
              (throw (Exception. "You must declare at least 1 return type!"))

              [input result])))))


(comment
  (breakout-signature ['=> '+person '+nil])
  (breakout-signature ['+s '=> '+person '+nil])
  (breakout-signature [])
  (breakout-signature [+s])
)





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


  ; determined at def-time I think...
  ; (.get *run-schema-checks*)


  ; runtime
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

                                   ; need to list out the valid return types
                                   (str "Return type is not valid!")))

              ))
         ))
    ))

;; (macroexpand
;;  '(defn+ hello [+s +i][+s]
;;     (str "hi " s)))





;; (defn+ hello [+i >> +person]
;;   (str "hi " s " number " i))






;; (def +people (sequence-of +person))


;; (defn+ load-all [=> +people]
;;   (-> db
;;       (select :people {})))


;; (defn+ load-by-id [+i => +person +nil]
;;   (-> db
;;       (select1 :people {:id i})))


;; (defn+ load-by-mail [+email => +person +nil]
;;   (-> db
;;       (select1 :people {:email email})))








;; (defn+ hello [+s +i][+s]
;;   (str "hi " s " number " i))

;; (hello "bob" 2)
;; (hello "bob" 333)


;; (defn+ hello2 [+s +i][+i]
;;   "444")

;; (hello2 "" 2)



; requred => (fn [val] non-empty string checked, otherwise refurn truthy)
; (length 3 50) => (fn [val] checks length for string)
;



;; (def +person {:first [+s required [len 3 5]]
;;               :last  [+s required]
;;               :age    +i })


;; (defn+ lookup [+i][+person]
;;   {:first "bob" :last "barker" :age 77})

;; (lookup 1)




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
