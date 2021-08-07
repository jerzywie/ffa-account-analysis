(ns jerzywie.analyse
  (:require [clojure.string :as s]
            [java-time :as j]
            [java-time.interval :as ji]))

(def name-cache (atom {}))

(def empty-name {:names #{} :group nil :filterby nil})

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

(defn make-key [{:keys [name group]}]
  (hash (if (nil? group) name group)))

(defn cache-name [{:keys [name group] :as m}]
  (let [key (make-key m)
        value (get @name-cache key empty-name)
        new-value (assoc value :names (conj (:names value) name)
                         :group group
                         :filterby (if (nil? group) :names :group))]
    (swap! name-cache assoc key new-value)))

(defn process-income [transactions]
  (let [in-txns (filter #(nil? (:out %)) transactions)]
   in-txns))
