(ns jerzywie.ffa-account-analysis
  (:require [jerzywie.csv :as csv])
  (:gen-class))

(defn greet
  "Callable entry point to the application."
  [data]
  (println (str "Hello, " (or (:name data) "World") "!")))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (greet {:name (first args)})
  (let [statement-data (csv/get-statement-data {:filename "/Users/jerzy/Downloads/Statement Download 2021-May-25 10-10-29.csv"})]
    (prn statement-data)))
