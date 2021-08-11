(ns jerzywie.ffa-account-analysis
  (:require [jerzywie.csv :as csv])
  (:gen-class))

(defn -main
  "Analyse transactions in downloaded transactions csv-file."
  [& args]
  (let [file-name  "resources/Statement Download 2021-Aug-08.csv"
        local-file "resources/test-transactions.csv"
        statement-data (csv/get-statement-data {:filename file-name})]
    statement-data))
