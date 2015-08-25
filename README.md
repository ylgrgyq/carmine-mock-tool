# carmine-mock-tool

(com.taoensso/carmine)[https://github.com/ptaoussanis/carmine] is a great redis client. Easy to use and have has great docs. But it has only one defect that it is difficult to test. You always need to start a redis server on your local machine and use it to ensure your code is correct. If you have a CI for your project, and want to test your code 

A Clojure library designed to mock com.taoensso/carmine (a famous redis client written in clojure).

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

For pipeline, 
```clojure
(defn foo [key value]
  (wcar*
    (car/sadd key value)
    (car/expire key 1800)))

(deftest test-foo
  (let [key "dummy-key"
        value "dummy-value"
        ret ["OK" "OK"]]
    (mock-carmine-redis-client ret
      [car/sadd (fn [k v]
                 (is (= key k))
                 (is (= value v)))
       car/expire (fn [k v]
                    ; do some check
		    )]
      (is (= ["OK" "OK"] (foo key value))))))
```

## License

Copyright © 2015 ylgrgyq

Distributed under the Eclipse Public License either version 1.0.
