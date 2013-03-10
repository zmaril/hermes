(ns hermes.edge
  (:import (com.tinkerpop.blueprints Edge Direction)
           (com.tinkerpop.gremlin.java GremlinPipeline))
  (:require [hermes.vertex :as v]
            [hermes.type   :as t]
            [hermes.gremlin :as q])  
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


(defn get-label [edge]
  (.. edge getTitanLabel getName))

(defn prop-map [edge]
  (into {:__id__ (get-id edge)
         :__label__ (get-label edge)}
        (map #(vector (keyword %) (get-property edge %)) (get-keys edge))))

(defn endpoints [this]
  "Returns the endpoints of the edge in array with the order [starting-node,ending-node]."
  (ensure-graph-is-transaction-safe)
  [(.getVertex this Direction/OUT)
   (.getVertex this Direction/IN)])

(defn edges-between
  "Returns a set of the edges between two vertices."
  ([v1 v2] (edges-between v1 v2 nil))
  ([v1 v2 label]
     (ensure-graph-is-transaction-safe)
     ;; Source for these edge queries:
     ;; https://groups.google.com/forum/?fromgroups=#!topic/gremlin-users/R2RJxJc1BHI
     (let [set-1
           (q/query v1
                    (q/--E> label)
                    q/in-vertex
                    (q/has "id" (.getId v2))
                    (q/back 2)
                    (q/to-list))
           set-2
           (q/query v2
                    (q/--E> label)
                    q/in-vertex
                    (q/has "id" (.getId v1))
                    (q/back 2)
                    (q/to-list))]
       (println set-1 set-2)
       (set (concat set-1 set-2)))))

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
  ([v1 v2 label] (connect! v1 v2 label {}))
  ([v1 v2 label data]
     (ensure-graph-is-transaction-safe)
     (let [edge (.addEdge *graph* v1 v2 label)]
       (set-properties! edge data)
       edge)))

(defn upconnect!
  "Upconnect takes all the edges between the given vertices with the
   given label and, if the data is provided, merges the data with the
   current properties of the edge. If no such edge exists, then an
   edge is created with the given data."
  ([v1 v2 label] (upconnect! v1 v2 label {}))
  ([v1 v2 label data]
     (ensure-graph-is-transaction-safe)
     (if-let [edges (edges-between v1 v2 label)]
       (do
         (doseq [edge edges] (set-properties! edge data))
         edges)
       #{(connect! v1 v2 label data)})))

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
