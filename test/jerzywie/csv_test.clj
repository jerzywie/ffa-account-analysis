(ns jerzywie.csv-test
  (:require [jerzywie.csv :as sut]
            [clojure.test :refer [deftest is testing]]))

(def test-csv-file "resources/test-transactions.csv")
(def expected-keys '(:accinfo :txns))
(def expected-txns-count 58)
(def expected-header-keys-count 3)
(def expected-accname "accname")
(def expected-balance {:s "�123.45" :v 123.45})
(def expected-avail-balance {:s "�98.76" :v 98.76})

(deftest keywordise-headers-converts-to-correct-headers
  (testing "keywordise-headers converts to correct headers."
    (is (= '(:date :type :desc :out :in :bal)
           (sut/keywordise-transaction-headers ["Date" "Transaction type" "Description" "Paid out" "Paid in" "Balance"])))))

(deftest format-amount-tests
  (testing "format-amount handles conversion correctly."
    (is (= 610.89 (sut/format-amount "610.89")))
    (is (= 610.89 (sut/format-amount "�610.89")))
    (is (= 15.00 (sut/format-amount "£15.00")))
    (is (nil? (sut/format-amount "")))))

(def header-lines (list ["Account Name:" (:s expected-accname)]
                        ["Account Balance:" (:s expected-balance)]
                        ["Available Balance: " (:s expected-avail-balance)]))

(deftest process-header-tests
  (let [header (sut/process-header-lines header-lines)]
    (prn "header" header)
    (is (= (count (keys header)) 3))
    (is (= (:account-name header) (:v expected-accname)))
    (is (= (:account-balance header) (:v expected-balance)))
    (is (= (:avail-balance header) (:v expected-avail-balance)))))

(deftest format-transaction-tests
  (testing "format-transaction handles transformations correctly."
    (let [raw-transaction {:date "12 May 2021", :type "Bank credit Billy Holiday", :desc "Bank credit Billy Holiday", :out "", :in "�100.00", :bal "�640.56"}
          fmt-transaction (sut/format-transaction raw-transaction)
          expected {:date (java.time.LocalDate/of 2021 05 12) :type "Bank credit Billy Holiday", :desc "Bank credit Billy Holiday", :out nil, :in 100.00, :bal 640.56}]
      (is (= fmt-transaction expected)))))

(deftest get-statement-data-tests
  (let [sd (sut/get-statement-data {:filename test-csv-file})]
    (is (= (count (keys sd)) (count expected-keys)))
    (is (= (keys sd) expected-keys))
    (is (= (count (:txns sd)) expected-txns-count))
    (is (= (count (:accinfo sd)) expected-header-keys-count))))
