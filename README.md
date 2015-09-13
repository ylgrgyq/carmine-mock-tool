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
                 (is (= value v)))]
      (is (= ret (foo key value))))))
```
The first parameter passed to mock-carmine-redis-client is what you want to return from mocked redis command function and second parameter is a pair which contains the mocking command and it's corresponding mocking function just like what with-redefs do except that mocking function in mock-carmine-redis-client can't return values directly.

We want car/set in foo return "OK" so we pass (constantly ret) to mock-carmine-redis-client and ret is "OK".

For carmine redis command called more than one times in a sigle function. 
```clojure
(defn foo [key value1 value2]
  (let [[members _] (wcar*
                      (car/smembers key value1)
                      (car/expire key 1800))]
    (when (empty? members)
      (wcar*
        (car/set value2)))))

(deftest test-foo
  (let [key "dummy-key"
        value1 "dummy-value1"
        value2 "dummy-value2"
	members [1 2 3]
        ret [[members "OK"] "OK"]
    (mock-carmine-redis-client ret
      [car/smembers (fn [k v]
                      ;; check parmeters
                      (is (= key k))
                      (is (= value v)))
       car/expire (fn [k v]
                    ; do some check
                    )
       car/set (fn [k v]
                 ; do some check
                 )]
      (is (= ret (foo key value1 value2))))))
```
Please notice that the mocking function for redis command can't return value direct. The return value is passed by ret which is [[members "OK"] "OK"] in this example.

In foo, we called (wcar* ....) for two times so "ret" has two elements. First call for wcar* we'd like to return [members "OK"] and second call we'd like to just return "OK". So we set "ret" as [[members "OK"] "OK"]. 

## License

Copyright © 2015 ylgrgyq

Distributed under the Eclipse Public License either version 1.0.
