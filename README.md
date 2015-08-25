# carmine-mock-tool

[com.taoensso/carmine](https://github.com/ptaoussanis/carmine) is a great redis client. Easy to use and have great docs. But it has a annoying defect that it is difficult to write unit test.

## Usage

For instance, here's some code using carmine to set a value for a key to redis.
```clojure
(defmacro wcar* [& body]
  `(car/wcar
     {:pool pool
      :spec spec-server} ~@body))
	   
(defn foo [key value]
  (wcar*
    (car/set key value)))
```

And, you can mock car/set and test the parameters passed to car/set like this
```clojure
(deftest test-foo
  (let [key "dummy-key"
        value "dummy-value"
	ret "OK"]
    (mock-carmine-redis-client (constantly ret)
      [car/set (fn [k v]
      	         (is (= key k))
		 (is (= value v)))]
      (is (= "OK" (foo key value))))))
```

For carmine redis command called more than one times in a sigle function. 
```clojure
(defn foo [key value1 value2]
  (wcar*
    (car/sadd key value1)
    (car/expire key 1800))
  (wcar*
    (car/set value2)))

(deftest test-foo
  (let [key "dummy-key"
        value1 "dummy-value1"
	value2 "dummy-value2"
        ret [["OK" "OK"] "OK"]]
    (mock-carmine-redis-client ret
      [car/sadd (fn [k v]
                 (is (= key k))
                 (is (= value v)))
       car/expire (fn [k v]
                    ; do some check
		    )
       car/set (fn [k v]
                 ; do some check
		 )]
      (foo key value1 value2))))
```

## License

Copyright © 2015 ylgrgyq

Distributed under the Eclipse Public License either version 1.0.
