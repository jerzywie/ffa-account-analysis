(ns jerzywie.test-util
  (:require [jerzywie.cache :as cache]
            [clojure.edn :as edn]))


(defn java-time-LocalDate-reader [[t _ v]]
  (eval (list (symbol (str t) "parse") v)))

(defn read-edn [file-name]
  (edn/read-string
   {:default (fn [t v] (str "unknown tag found t=" t " v=" v))
    :readers {'object java-time-LocalDate-reader}}
   (slurp file-name)))

(defn start-with-empty-cache [f]
  (cache/empty-cache)
  (f))
