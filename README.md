# carmine-mock-tool

A mocking tool for [com.taoensso/carmine](https://github.com/ptaoussanis/carmine) 

## Usage

For instance, here's some code using carmine to save some value for a key into redis.
```clojure
(defmacro wcar* [& body]
  `(car/wcar
     {:pool pool
      :spec spec-server} ~@body))
	   
(defn foo [key value]
  (wcar*
    (car/set key value)))
```

And, you can mock car/set and test the parameters passed to car/set as follows:
```clojure
(deftest test-foo
  (let [key "dummy-key"
        value "dummy-value"
        ret "OK"]
    (mock-carmine-redis-client (constantly ret)
      [car/set (fn [k v]
                 ;; check parmeters
                 (is (= key k))
                 (is (= value v))
                 ;; return what redis will return
                 "OK")]
      (is (= "OK" (foo key value))))))
```

For carmine redis command called more than one times in a sigle function. 
```clojure
(defn foo [key value1 value2]
  (let [[members _] (wcar*
    		      (car/smembers key value1)
    		      (car/expire key 1800))]
    (when (empty? members)
      (wcar*
        (car/set value2))))

(deftest test-foo
  (let [key "dummy-key"
        value1 "dummy-value1"
	value2 "dummy-value2"
        ret [["OK" "OK"] "OK"]
	members [1 2 3]
    (mock-carmine-redis-client ret
      [car/smembers (fn [k v]
                 ;; check parmeters
                 (is (= key k))
                 (is (= value v))
                 ;; return what redis will return
		 members)
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
