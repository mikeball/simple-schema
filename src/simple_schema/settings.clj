(ns simple-schema.settings)

(definterface IBooleanSetting
  (get ^boolean [])
  (set [^boolean b]))

(deftype BooleanSetting [^:volatile-mutable ^boolean setting] IBooleanSetting
  (get [this] setting)
  (set [this value] (set! setting value)))






