;; taoclj.simple-schema
(ns simple-schema.compilation
  (:require [clojure.string :as string]))

(defn validate-return-type [return]
  (cond (not (symbol? return))
        (throw (Exception. (str "Return type " return " must be a symbol!")))

        (not (.startsWith (str return) "+"))
        (throw (Exception. (str "Return type " return " must begin with a + sign.")))

        :default true ))


(defn breakout-signature [params]
  (let [arrow-at (.indexOf params '=>)]
    (cond (= arrow-at -1)
          (throw (Exception. "Parameter declaration must contain =>"))

          :default
          (let [input (take arrow-at params)
                result (drop (+ arrow-at 1) params)]

            (if (= 0 (count result))
              (throw (Exception. "You must declare at least 1 return type!")))

            (doall (map validate-return-type result))

            [input result]))))

(comment
  (breakout-signature ['=> '+person '+nil])
  (breakout-signature ['+s '=> '+person '+nil])
  (breakout-signature [])
  (breakout-signature [+s])  )




(defn compile-param-name [sym]
  (let [sym-string (str sym)]

    (if-not (.startsWith sym-string "+")
      (throw (IllegalArgumentException. (str "Parameter " sym-string " must begin with a plus sign."))))

    (let [name-parts (string/split sym-string #"\.")
          part-count (count name-parts)]

      (cond (= part-count 2)
            (map symbol name-parts)

            (= part-count 1)
            (let [type-name (first name-parts)]
              (map symbol
                   (conj name-parts
                         (.substring type-name 1 (.length type-name)))))

            :default
            (throw (IllegalArgumentException.
                    (str "Parameter " sym-string " is not a valid parameter name!"))))
      )))

(comment
  (compile-param-name '+s)
  (compile-param-name '+s.name)
  (compile-param-name '+i) )

