(ns jerzywie.analyse
  (:require [clojure.string :as s]
            [java-time :as j]
            [java-time.interval :as ji]))

(def empty-name-cache {})

(def name-cache (atom empty-name-cache))

(def empty-name {:names #{} :group nil :filterby nil})

(def bank-credit "Bank credit")
(def transfer-from "Transfer from")

(defn strip-prefix [text]
  (-> text
      (s/replace bank-credit "")
      (s/replace transfer-from "")
      s/trim))

(defn make-group [name desc-less-prefix]
  "If the name and description are the same, then group is nil
   If they are different, then try for a group id by extracting account details"
  (let [maybe-group (if (not= name desc-less-prefix) desc-less-prefix nil)]
    (if (some? maybe-group)
      (re-find #"\d{6} \d{8}" maybe-group)
      nil)))

(defn process-name [{:keys [type desc]}]
  (let [name (strip-prefix type)
        strip-desc (strip-prefix desc)
        group (make-group name strip-desc)]
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
