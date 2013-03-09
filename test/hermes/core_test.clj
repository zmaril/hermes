(ns hermes.core-test
  (:use clojure.test
        [hermes.test-conf :only (conf)])
  (:require [hermes.core :as g]
            [hermes.type :as t]
            [hermes.vertex :as v])
  (:import  (com.thinkaurelius.titan.graphdb.vertices   StandardVertex)
            (com.thinkaurelius.titan.graphdb.database   StandardTitanGraph)))

(g/open conf)

(deftest test-opening-an-embedded-graph
   (testing "Embedded cassandra graph"
     (is (= (type g/*graph*) StandardTitanGraph))))


;; (deftest test-with-graph
;;   (testing "with-graph macro"
;;     ; Open the usual *graph*
;;     (g/open)
;;     ; Open a real graph the hard wary
;;     (let [graph (TitanFactory/openInMemoryGraph)]
;;       (g/with-graph graph
;;         (.addVertex graph))
;;       (is (= 1 (count (seq (.getVertices graph)))) "graph has the new vertex")
;;       (is (= 0 (count (seq (.getVertices g/*graph*)))) "the usual *graph* is still empty"))))

;; (deftest test-retry-transact!
;;   (testing "with backoff function"
;;     (let [sum (partial reduce +)
;;           clock (atom [])
;;           punch-clock (fn [] (swap! clock concat [(System/currentTimeMillis)]))]
;;       (is (thrown? Exception (g/retry-transact! 3 (fn [n] (* n 100))
;;                                                 (punch-clock)
;;                                                 (/ 1 0))))
;;       (let [[a,b,c] (map (fn [a b] (- a b)) (rest @clock) @clock)]
;;         (is (>= a 100))
;;         (is (>= b 200))
;;         (is (>= c 300)))))

;;   (testing "with transaction that returns nil"
;;     (g/transact!
;;      (t/create-vertex-key-once :vertex-id Long))
;;     (g/retry-transact! 3 10
;;       (v/upsert! :vertex-id {:vertex-id 20})
;;       nil)

;;     (is (= 1 (count (seq (.getVertices g/*graph*)))) "graph has the new vertex")))


