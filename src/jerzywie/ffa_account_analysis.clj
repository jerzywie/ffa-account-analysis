(ns jerzywie.ffa-account-analysis
  (:require [jerzywie
             [csv :as csv]
             [allocate :as alloc]
             [analyse :as anal]
             [report :as rep]
             [util :as u]]
            [java-time :as j]
            [clojure.pprint :as pp]
            [clojure.string :as s])
  (:gen-class))

(def local-file "resources/test-transactions.csv")
(def help-arg "-h")

(defn process-transactions [date transactions]
  (->> transactions
       :txns
       alloc/process-income
       (anal/analyse-donations date)))

(defn print-help []
  (->> ["FFA Bank account transaction analysis."
        ""
        "Usage: transactions-csv-file date(in format yyyy mm dd)"
        ""
        "Options:"
        " -h prints this message."
        " transactions-csv-file = '_' defaults to test transactions file."
        ""
        "If no date is supplied the default is today."]
       (s/join \newline)
       (println)))

(defn process-args [args]
  (let [nargs (count args)
        first-arg (if (> nargs 0) (first args) help-arg)]
    (if (= first-arg help-arg)
      (do
        (print-help)
        {:help-given true})
      (let [file-name (if (= first-arg "_") local-file first-arg)
            y (if (> nargs 1) (Integer/parseInt (nth args 1)) 0)
            m (if (> nargs 2) (Integer/parseInt (nth args 2)) 0)
            d (if (> nargs 3) (Integer/parseInt (nth args 3)) 0)
            date (if (> (+ y m d) 3) (j/local-date y m d) (j/local-date))]
        {:file-name file-name :date date}))))

(defn -main
  "Analyse transactions in downloaded transactions csv-file."
  [& args]
  (let [{:keys [file-name date help-given]} (process-args args)]
    (if (not help-given)
      (let [processed-txns (->> {:filename file-name}
                                (csv/get-statement-data)
                                (process-transactions date))]

        (println (format "Donations at %s" (j/format "YYYY-MM-dd" date)))
        (println (format "Transactions file '%s'" file-name))
        (println "\nCurrent donations")
        (pp/pprint (filter #(contains? % :current) processed-txns))
        (println "\nOne-offs")
        (pp/pprint (filter #(contains? (:freq %) :one-off) processed-txns))
        (println "\nAll donors")
        (pp/pprint (rep/donor-report))))))
