(ns hermes.example
  (:require [hermes.core   :as g]
            [hermes.gremlin   :as q]
            [hermes.vertex :as v]
            [hermes.edge   :as e]
            [hermes.type   :as t]))

(defonce a (g/transact! (v/create! {:name "a"})))
(defonce b (g/transact! (v/create! {:name "b"})))

(g/transact!
  (e/upconnect! (v/refresh a)
                (v/refresh b)
              "test"))

(def blah (g/transact! (e/connected? (v/refresh a)
                                        (v/refresh b))))