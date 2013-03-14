(ns hermes.edge
  (:import (com.tinkerpop.blueprints Edge Direction)
           (com.tinkerpop.gremlin.java GremlinPipeline))
  (:require [hermes.vertex :as v]
            [hermes.type   :as t]
            [ogre.core :as q])  
  (:use [hermes.core :only (*graph* transact! ensure-graph-is-transaction-safe)]
        [hermes.util :only (immigrate)]))

(immigrate 'hermes.element)

;;
;;Information getters
;;
(defn find-by-id
  "Retrieves nodes by id from the graph."
  [& ids]
  (ensure-graph-is-transaction-safe)
  (if (= 1 (count ids))
    (.getEdge *graph* (first ids))
    (seq (for [id ids] (.getEdge *graph* id)))))


(defn get-label
  "Get the label of the edge"
  [edge]
  (keyword (.. edge getTitanLabel getName)))

(defn prop-map
  "Get the property map of the edge"
  [edge]
  (into {:__id__ (get-id edge)
         :__label__ (get-label edge)}
        (map #(vector (keyword %) (get-property edge %)) (get-keys edge))))

(defn endpoints
  "Returns the endpoints of the edge in array with the order [starting-node,ending-node]."
  [this]
  (ensure-graph-is-transaction-safe)
  [(.getVertex this Direction/OUT)
   (.getVertex this Direction/IN)])

(defn edges-between
  "Returns a set of the edges between two vertices, direction considered."
  ([v1 v2] (edges-between v1 v2 nil))
  ([v1 v2 label]
     (ensure-graph-is-transaction-safe)
     ;; Source for these edge queries:
     ;; https://groups.google.com/forum/?fromgroups=#!topic/gremlin-users/R2RJxJc1BHI
     (let [edges-set (q/query v1
                              (q/--E> label)
                              q/in-vertex
                              (q/has "id" (.getId v2))
                              (q/back 2)
                              (q/into-vec))]
       (when (not (empty? edges-set))
         edges-set))))

(defn connected?
  "Returns whether or not two vertices are connected. Optional third
   arguement specifying the label of the edge."
  ([v1 v2] (connected? v1 v2 nil))  
  ([v1 v2 label]     
     (ensure-graph-is-transaction-safe)
     (not (empty? (edges-between v1 v2 label)))))
;;
;;Transaction management
;;

(defn refresh
  "Goes and grabs the edge from the graph again. Useful for \"refreshing\" stale edges."
  [edge]
  (ensure-graph-is-transaction-safe)
  (.getEdge *graph* (.getId edge)))

;;
;;Creation methods
;;

(defn connect!
  "Connects two vertices with the given label, and, optionally, with the given properties."
  ([v1 label v2] (connect! v1 (name label) v2 {}))
  ([v1 label v2 data]
     (ensure-graph-is-transaction-safe)
     (let [edge (.addEdge *graph* v1 v2 (name label))]
       (set-properties! edge data)
       edge)))

(defn upconnect!
  "Upconnect takes all the edges between the given vertices with the
   given label and, if the data is provided, merges the data with the
   current properties of the edge. If no such edge exists, then an
   edge is created with the given data."
  ([v1 label v2] (upconnect! v1 (name label) v2 {}))
  ([v1 label v2 data]
     (ensure-graph-is-transaction-safe)
     (if-let [edges (edges-between v1 v2 (name label))]
       (do
         (doseq [edge edges] (set-properties! edge data))
         edges)
       #{(connect! v1 (name label) v2 data)})))

(defn unique-upconnect!
  "Like upconnect!, but throws an error when more than element is returned."
  [& args]
  (let [upconnected (apply upconnect! args)]
    (if (= 1 (count upconnected))
      (first upconnected)
      (throw (Throwable.
              (str
               "Don't call unique-upconnect! when there is more than one element returned.\n"
               "There were " (count upconnected) " edges returned.\n"
               "The arguments were: " args "\n"))))))

;;
;;Deletion methods
;;

(defn delete!
  "Delete an edge."
  [edge]
  (.removeEdge *graph* edge ))
