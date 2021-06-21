(ns jerzywie.ffa-account-analysis
  (:require [jerzywie.csv :as csv])
  (:gen-class))

(defn -main
  "Analyse transactions in downloaded transactions csv-file."
  [& args]
  (let [filename "/Users/jerzy/Downloads/Statement Download 2021-May-25 10-10-29.csv"
        local-file "resources/test-transactions.csv"
        statement-data (csv/get-statement-data {:filename local-file})]
    (prn statement-data)))
