(ns hermes.edge
  (:refer-clojure :exclude [keys vals assoc! dissoc! get])
  (:require [archimedes.util :refer (immigrate)]))

(immigrate 'archimedes.edge)
