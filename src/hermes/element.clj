(ns hermes.element
  (:import (com.tinkerpop.blueprints Element))
  (require [hermes.kryo :as kryo]))

(defn get-keys [this]
          (set (map keyword (.getPropertyKeys this))))

(defn get-id [this]
        (.getId this))

(defn set-property! [this key value]
               ;;Avoids changing keys that shouldn't be changed.
               ;;Important when using types. You aren't ever going to change a
  ;;user's id for example.
  (println this key value)
               (.setProperty this (name key) value)

               ;; (let [previous-val (get-property this (name key))]
               ;;   (when (not= val)) (.removeProperty this (name key)

               ;;                                      )

               ;;   )
               ;; (when (not= value
               )



(defn set-properties!  [this data]
                  (doseq [[k v] data] (set-property! this (name k) v))
                  this)

(defn get-property [this key]
              (kryo/revert (.getProperty this (name key))))

(defn remove-property! [this key]
                  (.removeProperty this (name key)))