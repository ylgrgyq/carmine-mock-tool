(ns carmine-mock-tool.core
  (:require [taoensso.carmine.connections :as conn]
            [taoensso.carmine.protocol :as protocol])
  (:import (java.net Socket)
           (taoensso.carmine.connections NonPooledConnectionPool Connection)
           (taoensso.carmine.connections NonPooledConnectionPool)))

(def ^:dynamic *counter* nil)

(defn fake-pool
  [_]
  (let [socket (Socket.)
        pool (NonPooledConnectionPool.)
        connect (Connection. socket "12323" 123 23)]
    [pool connect]))

(defn fake-replies [replyGen body-fn as-pipeline?]
  (let [start @*counter*
        _ (body-fn)
        reply (if (fn? replyGen)
                (replyGen)
                (take (- @*counter* start) (second (split-at start replyGen))))]
    (if (and as-pipeline? (not (vector? reply)))
      (vec reply)
      (if (and (seq? reply) (= 1 (count reply)))
        (first reply)
        reply))))

(defmacro mock-funs [mocked-funs & body]
  `(with-redefs ~(list* (reduce (fn [ret next]
                                  (let [[command checker] next] (conj ret command checker)))
                          []
                          (map (fn [command checker] [command checker]) (take-nth 2 mocked-funs) (take-nth 2 (next mocked-funs)))))
     ~@body))

(defmacro mock-funs-new [mocked-funs & body]
  `(with-redefs ~(list* (reduce (fn [ret next]
                                  (let [[command checker] next] (conj ret command checker)))
                          []
                          (map (fn [command checker]
                                 [command `(fn [& args#]
                                             (swap! *counter* inc)
                                             (apply ~checker args#))])
                            (take-nth 2 mocked-funs) (take-nth 2 (next mocked-funs)))))
     ~@body))

(defmacro mock-carmine-redis-client [reply-gen mocked-commands & body]
  `(binding [*counter* (atom 0)]
     (let [reply# ~reply-gen]
       (with-redefs [conn/pooled-conn fake-pool
                     protocol/with-replies* (partial fake-replies reply#)]
         (mock-funs-new [~@mocked-commands] ~@body)
         (reset! *counter* 0)))))
