(ns jerzywie.analyse
  (:require [jerzywie.cache :as nc]))

(defn deduce-period [d1 d2]
  (let [days (.. java.time.temporal.ChronoUnit/DAYS (between d1 d2))]
    (cond
      (= days 7) :weekly
      (or (= days 6) (= days 8)) :approx-weekly
      (and (> days 27) (< days 32)) :monthly
      :else :irregular)))

(defn interval-analysis [date-etc txn]
  (let [date (:date date-etc)
        results (get date-etc :results [])
        next-date (:date txn)]
    (cond
      (nil? date)
      {:date next-date
       :results (conj results (assoc txn :freq #{:new-amount}))}

      :else
      {:date next-date
       :results (conj results (assoc txn :freq  #{(deduce-period date next-date)}))})))

(defn analyse-time-intervals [txns amount]
  (let [donations (filter (fn [{:keys [in]}] (= in amount)) txns)]
    (->> donations
         (reduce interval-analysis nil)
         :results)))

(defn analyse-donations [key]
  (let [entity (nc/get-cache-value key)
        txns (:txns entity)
        amount-cache (reduce (fn [amounts {:keys [in]}] (conj amounts in)) #{} txns)]
    (map (fn [amount] (analyse-time-intervals txns amount)) amount-cache)
    ))
