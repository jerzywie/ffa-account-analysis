(ns jerzywie.analyse
  (:require [clojure.string :as s]
            [java-time :as j]
            [java-time.interval :as ji]))

(def name-cache (atom []))

(def bank-credit "Bank credit")
(def transfer-from "Transfer from")

(defn strip-prefix [text]
  (-> text
      (s/replace bank-credit "")
      (s/replace transfer-from "")
      s/trim))

(defn process-name [{:keys [type desc]}]
  (let [name (strip-prefix type)
        strip-desc (strip-prefix desc)
        group (if (not= name strip-desc) strip-desc nil)]
    {:name name :group group}))

(defn process-income [transactions]
  (let [in-txns (filter #(nil? (:out %)) transactions)]
   in-txns))
