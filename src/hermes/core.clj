(ns hermes.core
  (:require [archimedes.core :as g]
            [archimedes.util :as u])
  (:import (com.thinkaurelius.titan.core TitanFactory)
           (com.tinkerpop.blueprints Element TransactionalGraph TransactionalGraph$Conclusion)
           (com.thinkaurelius.titan.graphdb.blueprints TitanInMemoryBlueprintsGraph)
           (com.thinkaurelius.titan.graphdb.transaction StandardPersistTitanTx)
           (org.apache.commons.configuration BaseConfiguration)))

(defn convert-config-map [m]
  (let [conf (BaseConfiguration.)]
    (doseq [[k1 v1] m]
            (if (string? v1)
              (.setProperty conf (name k1) v1)
              (doseq [[k2 v2] v1]
                (.setProperty conf (str (name k1) "." (name k2)) v2))))
    conf))

(defn open
  "Open a graph.  If no configuration is supplied an in-memory graph is opened."  
  [m]
  (g/set-graph! (if (string? m)
                  (TitanFactory/open m)
                  (TitanFactory/open (convert-config-map m)))))

(defn ensure-graph-is-transaction-safe
  "Ensure that we are either in a transaction or using an in-memory graph."
  []
  (when-not (#{TitanInMemoryBlueprintsGraph StandardPersistTitanTx}
              (type g/*graph*))
    (throw
      (Throwable.
       "All actions on a persistent graph must be wrapped in transact! "))))

(g/set-pre-fn! ensure-graph-is-transaction-safe)

(u/immigrate 'archimedes.core)