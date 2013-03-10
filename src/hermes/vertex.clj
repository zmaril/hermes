(ns hermes.vertex
  (:import (com.tinkerpop.blueprints Vertex))
  (:use [hermes.core :only (*graph* transact! ensure-graph-is-transaction-safe)]
        [hermes.util :only (immigrate)]))

(immigrate 'hermes.element)

;;
;;Information getters
;;

(defn prop-map
  "Returns a Persistent map representing the edge"
  [vertex]
  (into {:__id__ (get-id vertex)}
        (map #(vector (keyword %) (get-property vertex %)) (get-keys vertex))))

(defn find-by-id
  "Retrieves nodes by id from the graph."
  [& ids]
  (ensure-graph-is-transaction-safe)
  (if (= 1 (count ids))
    (.getVertex *graph* (first ids))
    (seq (for [id ids] (.getVertex *graph* id)))))

(defn find-by-kv
  "Given a key and a value, returns the set of all vertices that
   sastify the pair."
  [k v]
  (ensure-graph-is-transaction-safe)
  (set (.getVertices *graph* (name k) v)))

;;
;; Transaction management
;;

(defn refresh
  "Gets a vertex back from the database and refreshes it to be usable again."
  [vertex]
  (ensure-graph-is-transaction-safe)
  (.getVertex *graph* vertex))

;;
;; Creation methods
;;

(defn create!  
  "Create a vertex, optionally with the given property map."
  ([] (create! {}))
  ([data]
     (ensure-graph-is-transaction-safe)
     (set-properties! (.addVertex *graph*) data)))

(defn upsert!
  "Given a key and a property map, upsert! either creates a new node
   with that property map or updates all nodes with the given key
   value pair to have the new properties specifiied by the map. Always
   returns the set of vertices that were just update or created."
  [k m]
  (ensure-graph-is-transaction-safe)
   (let [vertices (find-by-kv (name k) (k m))]
     (if (empty? vertices)
       (set [(create! m)])
       (do
         (doseq [vertex vertices] (set-properties! vertex m))
         vertices))))

(defn unique-upsert!
  "Like upsert!, but throws an error when more than one element is returned."
  [& args]
  (let [upserted (apply upsert! args)]
    (if (= 1 (count upserted))
      (first upserted)
      (throw (Throwable.
              (str
               "Don't call unique-upsert! when there is more than one element returned.\n"
               "There were " (count upserted) " vertices returned.\n"
               "The arguments were: " args "\n"))))))

;;
;; Deletion methods
;;

(defn delete!  
  "Delete a vertex."
  ([vertex]
     (ensure-graph-is-transaction-safe)
     (.removeVertex *graph* vertex)))

