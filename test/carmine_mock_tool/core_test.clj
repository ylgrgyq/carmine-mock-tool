(ns carmine-mock-tool.core-test
  (:require [taoensso.carmine :as car]
            [clojure.test :as t]
            [carmine-mock-tool.core :as sut]))

(t/deftest test-mock-carmine-redis-client
  (let [k ::my-key
        v ::my-value]
    (sut/mock-carmine-redis-client
     (constantly v)
     [car/get (fn [k*] k*)]
     (t/is (= k (car/get k)))
     (t/is (= v (car/wcar (car/get k)))))))
