(ns jerzywie.csv-test
  (:require [jerzywie.csv :as sut]
            [clojure.test :refer :all]))

(def test-csv-file "resources/test-transactions.csv")
(def expected-keys '(:accinfo :txns))
(def expected-txns-count 58)

(deftest keywordise-headers-converts-to-correct-headers
  (testing "keywordise-headers converts to correct headers."
    (is (= '(:date :type :desc :out :in :bal) (sut/keywordise-headers ["Date" "Transaction type" "Description" "Paid out" "Paid in" "Balance"])))))

(deftest format-amount-tests
  (testing "format-amount handles conversion correctly."
    (is (= 610.89 (sut/format-amount "610.89")))
    (is (= 610.89 (sut/format-amount "�610.89")))
    (is (= 15.00 (sut/format-amount "£15.00")))
    (is (nil? (sut/format-amount "")))))

(deftest format-transaction-tests
  (testing "format-transaction handles transformations correctly."
    (let [raw-transaction {:date "12 May 2021", :type "Bank credit Billy Holiday", :desc "Bank credit Billy Holiday", :out "", :in "�100.00", :bal "�640.56"}
          fmt-transaction (sut/format-transaction raw-transaction)
          expected {:date (java.time.LocalDate/of 2021 05 12) :type "Bank credit Billy Holiday", :desc "Bank credit Billy Holiday", :out nil, :in 100.00, :bal 640.56}]
      (is (= fmt-transaction) expected))))

(deftest get-statement-data-tests
  (let [sd (sut/get-statement-data {:filename test-csv-file})]
    (is (= (count (keys sd)) (count expected-keys)))
    (is (= (keys sd) expected-keys))
    (is (= (count (:txns sd)) expected-txns-count))))
