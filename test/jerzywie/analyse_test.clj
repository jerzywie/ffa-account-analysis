(ns jerzywie.analyse-test
  (:require [jerzywie.analyse :as sut]
            [clojure.test :refer :all]))

(deftest strip-prefix-tests
  (testing "strip-prefix behaves correctly"
    (are [x y] (= (sut/strip-prefix x) y)
      "Bank credit XYZ" "XYZ"
      "Transfer from PQR" "PQR"
      "Bank creditAll of You" "All of You"
      "Transfer from      Only this" "Only this"
      "Just this bit" "Just this bit")))


(deftest process-name-tests
  (testing "process-name handles name and description correctly"
    (are [map result] (= (sut/process-name map) result)
      {:date :adate :type "Bank credit COCTEAU TWINS" :desc "Bank credit COCTEAU TWINS"}
      {:name "COCTEAU TWINS" :group nil}

      {:type "Transfer from FRED BLOGGS" :desc "Transfer from FRED BLOGGS"}
      {:name "FRED BLOGGS" :group nil}

      {:type "Transfer from FRED BLOGGS" :desc "112233 78903456"}
      {:name "FRED BLOGGS" :group "112233 78903456"})))

