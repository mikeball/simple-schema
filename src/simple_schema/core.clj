(ns simple-schema.core
  (:require [clojure.string :as string]
            [simple-schema.settings :as settings]
            [simple-schema.types :as types]
            [simple-schema.checks :as checks]
            [simple-schema.compilation :as compilation])
  (:import [simple_schema.settings BooleanSetting]
           [simple_schema.types Any]))


(def ^:dynamic *run-schema-checks* (BooleanSetting. true))

(defn run-schema-checks! [on]
  (.set *run-schema-checks* on))

;; (run-schema-checks! true)
;; (.get *run-schema-checks*)


;; ***************************************************
;; base/default types

(def +nil nil)
(def +any Any)
(def +s java.lang.String)
(def +i java.lang.Long)
(def +b java.lang.Boolean)



;; ***************************************************

(defmacro defn+ [fn-name signature & body]
  (let [sig          (compilation/breakout-signature signature)
        params       (map compilation/compile-param-name (first sig))
        param-types  (apply vector (map first params))
        param-syms   (apply vector (map second params))
        param-names  (apply vector (map str param-syms))
        return-syms  (apply vector (second sig))
        return-names (string/join ", " return-syms)
        fn-sym       (vary-meta fn-name assoc :doc (str signature))
        result       (gensym "result") ]

    (if (not (.get *run-schema-checks*)) ;; enable/disable schema checks at deftime!
      ;; standard defn with zero overhead...
      `(defn ~fn-name ~param-names ~@body)

      ;; otherwise we build our enhanced schema checked defn+
      `(def ~fn-sym
         (fn ~param-syms
          ; we check the input parameters at call time
          (doall (map ~checks/check-param ~param-types ~param-names ~param-syms))

          (let [~result ~@body] ; we execute the body form
            ; we check result against return types...
            (if (some true? (map #(checks/check % ~result) ~return-syms))
              ~result ; we return as normal!
              (throw (IllegalArgumentException.
                      (str "Return type of function " ~(str fn-name) " is not valid! It must be one of " ~return-names)))

              )))))))


;; (defn+ hello [+s.msg => +s +nil]
;;   (cond (= msg "bob")   (str "hi " msg)
;;         (= msg "bill")  111
;;         :default        nil ))

;; (hello "bob")
;; (hello "xxx")
;; (hello "bill")


;; (macroexpand
;;  '(defn+ hello [+s.msg => +i]
;;     (str "hi " msg))
;;  )



;; ***************************************************

(defn- build-docstring [original additions]
  (reduce (fn [docstring addition]
            (str docstring "\n" addition))
          original
          additions))

(defmacro doc [sym & body]
  (let [v  (gensym "v")
        ds (gensym "ds")
        elements (->> body (map str) (apply vector))]
    `(let [~v  (var ~sym) ~ds (:doc (meta ~v))]
      (if-not
        (alter-meta! (var ~sym) assoc
                     :doc (build-docstring ~ds ~elements))))))

;; (doc hello
;;      "\ntest 333"
;;      (hello "bob"))

;; (macroexpand
;;  '(doc hello "xxx")
;;  )











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







; requred => (fn [val] non-empty string checked, otherwise refurn truthy)
; (length 3 50) => (fn [val] checks length for string)


;; (def +person {:first [+s required [len 3 5]]
;;               :last  [+s required]
;;               :age    +i })


;; (defn+ lookup [+i][+person]
;;   {:first "bob" :last "barker" :age 77})

;; (lookup 1)



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
