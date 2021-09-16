(ns jerzywie.csv
  (:require [jerzywie.util :as u]
            [clojure.string :as s]
            [java-time :as j]))

(def keyword-convert {:date :date
                      :transactiontype :type
                      :description :desc
                      :paidout :out
                      :paidin :in
                      :balance :bal
                      :accountname :account-name
                      :accountbalance :account-balance
                      :availablebalance :avail-balance})

(defn convert-to-keyword [s]
  ((comp #(% keyword-convert)
      keyword
      s/lower-case
      #(u/strip-last-char-if % ":")
      (fn [item] (s/replace item #"\s" ""))) s))

(defn keywordise-transaction-headers [header-line]
  (map convert-to-keyword header-line))

(defn read-statement-file [filename]
  (slurp filename))

(defn get-quoted-csv-file-lines [filestring]
  (-> filestring
      (s/replace #"\"" "")
      (s/split #"\r\n")))

(defn format-amount [amount-string]
  (let [amount (re-find #"\d+\.\d+" amount-string)]
    (if (s/blank? amount) nil (Double/parseDouble amount))))

(defn make-header-line-map [[k v] convert-v-to-amount?]
  {(convert-to-keyword k) (if convert-v-to-amount? (format-amount v) v)})

(defn process-header-lines [header-lines]
  (let [header-maps (map make-header-line-map header-lines [false true true])]
    (apply merge header-maps)))

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
        acc-info (process-header-lines (take 3 all-data))
        transactions (drop 4 all-data)
        txn-headers (keywordise-transaction-headers (first transactions))
        txn-map (map #(zipmap txn-headers %) (rest transactions))]
    {:accinfo acc-info
     :txns (map #(format-transaction %) txn-map)}))


