(ns jerzywie.analyse-test
  (:require [jerzywie.analyse :as sut]
            [jerzywie.test-util :as util]
            [clojure.test :refer :all]
            [java-time :as j]))

(def test-txns "resources/txns.edn")

(defn md
  "Helper function to make local-date from year month day array."
  [[y m d]]
  (j/local-date y m d))

(use-fixtures :each util/start-with-empty-cache)

(deftest deduce-period-tests
  (are [d1 d2 result] (= (sut/deduce-period (md d1) (md d2)) result)
    [2021  8  3] [2021  8 10] :weekly
    [2021  1 15] [2021  2 15] :monthly
    [2021  2 15] [2021  3 15] :monthly
    [2021  4 15] [2021  5 15] :monthly
    [2020  2 15] [2020  3 15] :monthly
    [2021  8 29] [2021  9  5] :weekly
    [2021  8  3] [2021  8  9] :approx-weekly
    [2021  8  3] [2021  8 11] :approx-weekly
    [2021  8  3] [2021  8  8] :irregular
    [2021  8  3] [2021  8 12] :irregular))

(deftest analyse-donations-tests
  ())
