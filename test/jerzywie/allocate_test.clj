(ns jerzywie.allocate-test
  (:require [jerzywie.allocate :as sut]
            [jerzywie.test-util :as util]
            [jerzywie.csv :as csv]
            [clojure.test :refer [are deftest is testing use-fixtures]]))

(use-fixtures :each util/start-with-empty-cache)

(deftest strip-prefix-tests
  (testing "strip-prefix behaves correctly"
    (are [x y] (= (sut/strip-prefix x) y)
      "Bank credit XYZ" "XYZ"
      "Transfer from PQR" "PQR"
      "Bank creditAll of You" "All of You"
      "Transfer from      Only this" "Only this"
      "Just this bit" "Just this bit")))

(deftest make-group-tests
  (testing "make-group extracts a valid group id"
    (are [name desc result] (= (sut/make-group name desc) result)
      "Same" "Same" nil
      "This is a name" "This is the description" nil
      "AN Other" "Credit 24 May 2021" nil
      "Fred Bloggs" "123456 87654321" "123456 87654321")))

(deftest process-name-tests
  (testing "process-name handles name and description correctly"
    (are [map result] (= (sut/process-name map) result)
      {:date :adate :type "Bank credit COCTEAU TWINS" :desc "Bank credit COCTEAU TWINS"}
      {:name "COCTEAU TWINS" :group nil}

      {:type "Transfer from FRED BLOGGS" :desc "Transfer from FRED BLOGGS"}
      {:name "FRED BLOGGS" :group nil}

      {:type "Transfer from FRED BLOGGS" :desc "112233 78903456"}
      {:name "FRED BLOGGS" :group "112233 78903456"}

      {:type "Transfer from FRED BLOGGS &" :desc "Transfer from FRED BLOGGS &"}
      {:name "FRED BLOGGS" :group nil})))

(defn cache-two-unrelated []
  (sut/cache-name {:name "A" :group nil})
  (sut/cache-name {:name "B" :group nil}))

(defn cache-two-related []
  (sut/cache-name {:name "C" :group "c-group"})
  (sut/cache-name {:name "D" :group "c-group"}))

(deftest cache-name-tests
  (testing "cache-name caches correctly.1"
    (are [map func result] (= (func (sut/cache-name map)) result)
      {:name "UNA" :group nil}
      (fn [r] (-> (vals r) first :names (contains? "UNA")))
      true

      {:name "DUO" :group nil}
      (fn [r] (-> (vals r) count))
      2

      {:name "TRIO" :group "AGROUP"}
      (fn [r] (-> (vals r) count))
      3)))

(deftest cache-name-tests-unrelated-names
  (testing "cache-name caches unrelated names correctly"
    (let [cache (cache-two-unrelated)]
      (is (= (-> cache vals count) 2))
      (is (nil? (->> cache vals second :group)))
      (is (= (-> cache vals second :filterby) :names)))))

(deftest cache-name-tests-related-names
  (testing "cache-name caches related names correctly"
    (let [cache (cache-two-related)]
      (is (= (-> cache vals count) 1))
      (is (= (->> cache vals first :group) "c-group"))
      (is (= (-> cache vals first :filterby) :group))
      (is (= (-> cache vals first :names) #{"C" "D"})))))

(deftest cache-name-tests-related-and-unrelated
  (testing "more cache-name tests"
    (let [_ (cache-two-related)
          cache (cache-two-unrelated)
          cache2 (cache-two-unrelated)]
      (is (= (-> cache vals count) 3))
      (is (= (-> cache vals first :group) "c-group"))
      (is (= (-> cache vals first :filterby) :group))
      (is (= (-> cache vals first :names count) 2))
      (is (= (-> cache vals second :names count) 1))
      (is (nil? (-> cache vals second :group)))
      (is (= cache cache2)))))

(def test-transactions-csv {:filename "resources/test-transactions.csv"})
(def BD "BOB DYLAN")
(def SO "SONNY")
(def CH "CHER")

(defn get-test-transactions [file-map]
  (let [raw-tx (csv/get-statement-data file-map)]
    (sut/process-income (:txns raw-tx))))

(deftest process-income-tests
  (let [income-tx (vals (get-test-transactions test-transactions-csv))
        bd-tx (first income-tx)
        sc-tx (nth income-tx 2)]
    (is (= (count income-tx) 6) "Check total donors")
    (is (= (count (:txns bd-tx)) 6) "Check total BD transactions.")
    (is (= (first (:names bd-tx)) BD) "Check account name.")
    (is (= (:names bd-tx) #{BD}) "Check account name.")
    (is (= (-> bd-tx :txns first :account-name first) BD) "Check account-name is propagated to transactions.")
    (is (= (-> bd-tx :txns first :name) BD) "Check it matches the name.")
    (is (= (:names sc-tx) #{CH SO}) "Check S+C account-name.")
    (is (= (:names sc-tx) (-> sc-tx :txns first :account-name)) "Check propagation of S+C account name.")
    (is (= (count (filter #(= (:name %) CH) (:txns sc-tx))) 16) "Check number of CH txns.")
    (is (= (count (filter #(= (:name %) SO) (:txns sc-tx))) 4) "Check number of SO txns.")
    (is (= (:filterby sc-tx) :group) "Check that this is a 'group' account.")))
