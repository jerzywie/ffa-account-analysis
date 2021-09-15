(ns jerzywie.csv
  (:require [clojure.string :as s]
            [java-time :as j]))

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

(defn format-amount [amount-string]
  (let [amount (re-find #"\d+\.\d+" amount-string)]
    (if (s/blank? amount) nil (Double/parseDouble amount))))

(defn format-transaction [{:keys [date type desc out in bal]}]
  {:date (j/local-date "dd MMM yyyy" date)
   :type type
   :desc desc
   :out (format-amount out)
   :in (format-amount in)
   :bal (format-amount bal)})

(defn get-statement-data [{:keys [filename]}]
  (let [all-data (->> filename
                      read-statement-file
                      get-quoted-csv-file-lines
                      (map #(s/split % #",")))
        acc-info (first all-data)
        transactions (drop 4 all-data)
        txn-headers (keywordise-headers (first transactions))
        txn-map (map #(zipmap txn-headers %) (rest transactions))]
    {:accinfo acc-info
     :txns (map #(format-transaction %) txn-map)}))


