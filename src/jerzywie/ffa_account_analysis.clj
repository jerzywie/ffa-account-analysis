(ns jerzywie.ffa-account-analysis
  (:require [jerzywie.csv :as csv])
  (:gen-class))

(defn -main
  "Analyse transactions in downloaded transactions csv-file."
  [& args]
  (let [local-file "resources/test-transactions.csv"
        file-name (if (> (count args) 0) (first args) local-file)
        statement-data (csv/get-statement-data {:filename file-name})]
    statement-data))
