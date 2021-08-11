(ns jerzywie.report
  (:require [jerzywie.cache :as nc]))

(defn donor-report []
  (let [amount-analysis (fn [txns] (map (fn [[k v]] [k (count v)]) (group-by :in txns)))
        donor-fn (fn [[k {:keys [names txns]}]] [k names (count txns) (amount-analysis txns)])]
    (map donor-fn @jerzywie.cache/name-cache)))
