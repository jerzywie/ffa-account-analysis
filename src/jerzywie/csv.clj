(ns jerzywie.csv
  (:require [clojure.string :as s]
            [java-time :as j]
            [java-time.interval :as ji]))

(def keyword-convert {:date :date
                      :transactiontype :type
                      :description :desc
                      :paidout :out
                      :paidin :in
                      :balance :bal})

(defn keywordise-headers [header-line]
  (map (comp #(% keyword-convert)
          keyword
          s/lower-case
          (fn [item] (s/replace item #"\s" ""))) header-line))

(defn read-statement-file [filename]
  (slurp filename))

(defn get-quoted-csv-file-lines [filestring]
  (-> filestring
      (s/replace #"\"" "")
      (s/split #"\r\n")))

(defn format-transaction [{:keys [date type desc out in bal]}]
  {:date (j/local-date "dd MMM yyyy" date)
   :type type
   :desc desc
   :out out
   :in in
   :bal bal})

(defn read-statement-file-old [name]
  (let [it-lines (get-quoted-csv-file-lines {:filename name})
        split-lines (map #(s/split % #",") it-lines)
        transactions (drop 4 split-lines)
        headers (keywordise-headers (first transactions))]
    (println "===============================")
    (prn transactions)
    (println "===============================")
    (prn headers)
    (prn (map #(zipmap headers %) (rest transactions)))))


(defn get-statement-data [filename]
  (let [all-data (->> (:filename filename)
                      read-statement-file
                      get-quoted-csv-file-lines
                      (map #(s/split % #",")))
        acc-info (first all-data)
        transactions (drop 4 all-data)
        txn-headers (keywordise-headers (first transactions))
        txn-map (map #(zipmap txn-headers %) (rest transactions))]
    {:accinfo (first all-data)
     :txns (map #(format-transaction %) txn-map)}))


