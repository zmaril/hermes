(ns hermes.vertex
  (:refer-clojure :exclude [keys vals assoc! dissoc! get])
  (:require [archimedes.util :refer (immigrate)]))

(immigrate 'archimedes.vertex)