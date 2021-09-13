(ns jerzywie.util
  (:require [java-time :as j]))

(defn md
  "Helper function to make local-date from year month day array."
  [[y m d]]
  (j/local-date y m d))

