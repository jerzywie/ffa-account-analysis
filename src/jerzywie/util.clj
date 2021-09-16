(ns jerzywie.util
  (:require [clojure.string :as s]
            [java-time :as j]))

(defn md
  "Helper function to make local-date from year month day array."
  [[y m d]]
  (j/local-date y m d))

(defn strip-last-char-if [s char-string]
  (if (s/ends-with? s char-string)
    (apply str (take (dec (count s)) s))
    s))
