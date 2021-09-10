(ns jerzywie.analyse
  (:require [jerzywie.cache :as nc]))

(defn days-between [d1 d2]
  (.. java.time.temporal.ChronoUnit/DAYS (between d1 d2)))

(defn deduce-period [d1 d2]
  (let [days (days-between d1 d2)]
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

(defn analyse-donor [key]
  (let [entity (nc/get-cache-value key)
        txns (:txns entity)
        amount-cache (reduce (fn [amounts {:keys [in]}] (conj amounts in)) #{} txns)]
    (map (fn [amount] (analyse-time-intervals txns amount)) amount-cache)
    ))

(defn analyse-recency [donations-tranche date]
  (let [last-one (last donations-tranche)
        day-diff (days-between (:date last-one) date)
        add-recency (fn [max-day-diff _]
                      (prn "mdd" max-day-diff "dd" day-diff)
                      (let [result (if (some #{day-diff} (range (inc max-day-diff)))
                                     (assoc last-one :current true)
                                     last-one)]
                        (prn "ar-res" result)
                        result))
        vvv (-> last-one :freq first)]
    (condp = vvv
      :weekly :>> #(add-recency 7 %)
      :monthly :>> #(add-recency 31 %)
      last-one)))

(defn analyse-donations [allocated-transactions]
  (map #(analyse-donor %) (keys allocated-transactions)))
