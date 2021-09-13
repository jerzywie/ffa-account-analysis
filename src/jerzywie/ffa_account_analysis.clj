(ns jerzywie.ffa-account-analysis
  (:require [jerzywie
             [csv :as csv]
             [allocate :as alloc]
             [analyse :as anal]
             [report :as rep]
             [util :as u]]
            [java-time :as j]
            [clojure.pprint :as pp])
  (:gen-class))

(defn process-transactions [date transactions]
  (->> transactions
       :txns
       alloc/process-income
       (anal/analyse-donations date)))

(defn -main
  "Analyse transactions in downloaded transactions csv-file."
  [& args]
  (let [nargs (count args)
        local-file "resources/test-transactions.csv"
        file-arg (if (> nargs 0) (first args) local-file)
        file-name (if (= file-arg "_") local-file file-arg)
        y (if (> nargs 1) (Integer/parseInt (nth args 1)) 0)
        m (if (> nargs 2) (Integer/parseInt (nth args 2)) 0)
        d (if (> nargs 3) (Integer/parseInt (nth args 3)) 0)
        date (if (> (+ y m d) 3) (j/local-date y m d) (j/local-date))
        processed-txns (->> {:filename file-name}
                            (csv/get-statement-data)
                            (process-transactions date))]
    (println "\nDonations at" date)
    (println "\nCurrent donations")
    (pp/pprint (filter #(contains? % :current) processed-txns))
    (println "\n One-offs")
    (pp/pprint (filter #(contains? (:freq %) :one-off) processed-txns))
    (println "\n All donors")
    (pp/pprint (rep/donor-report))))
