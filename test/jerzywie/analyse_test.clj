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

