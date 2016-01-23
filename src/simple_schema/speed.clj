(ns simple-schema.speed
  (:require [simple-schema.core :as schema]
            [criterium.core :as criterium]))









(definterface IBooleanSetting
  (get ^boolean [])
  (set [^boolean b]))

(deftype BoolSetting [^:volatile-mutable ^boolean setting] IBooleanSetting
  (get [this] setting)
  (set [this value] (set! setting value)))

(def *run-schema-checks* (BoolSetting. true))

(.get *run-schema-checks*)
(.set *run-schema-checks* true)




(defn run-schema-checks! [on]
  (.set *run-schema-checks* on))

(run-schema-checks! false)


(.get *run-schema-checks*)










;; (defprotocol MyInterface
;;   (foo [this]) ; `this` is required to let the interface refer to the class
;;   (bar [this] [this x] [this x y] [this x y z])) ; multi-arity method signature defined

;; ; `deftype` dynamically generates compiled bytecode for the specified identifier (e.g. MyClass)
;; (deftype MyClass [a b c]
;;   MyInterface ; implement the specified protocol (i.e. interface)

;;     ; each function's scope is defined by
;;     ; the object provided as the first argument
;;     ; i.e. something that is of the `MyClass` type
;;     (foo [this] a)
;;     (bar [this] b)
;;     (bar [this x] (+ c x))
;;     (bar [this x y] (+ c x y))
;;     (bar [this x y z] (+ c x y z)))

;; (def obj (MyClass. 1 2 3))

;; (prn (foo obj))       ; 1
;; (prn (bar obj))       ; 2
;; (prn (bar obj 1))     ; 4 (3 + )
;; (prn (bar obj 1 2))   ; 6 (3 + 1 + 2)
;; (prn (bar obj 1 2 3)) ; 9 (3 + 1 + 2 + 3)
