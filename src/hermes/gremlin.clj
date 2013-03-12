(ns hermes.gremlin
  (:refer-clojure :exclude [iterate count and filter step memoize next or range])
  (:use [hermes.util :only (immigrate)]))

(immigrate 'hermes.gremlin-core.util)
(immigrate 'hermes.gremlin-core.branch)
(immigrate 'hermes.gremlin-core.filter)
(immigrate 'hermes.gremlin-core.map)
(immigrate 'hermes.gremlin-core.pipe)
(immigrate 'hermes.gremlin-core.reduce)
(immigrate 'hermes.gremlin-core.traverse)
(immigrate 'hermes.gremlin-core.side-effect)

;; GremlinPipeline<S,com.tinkerpop.blueprints.Edge>	idEdge(com.tinkerpop.blueprints.Graph graph) 
;; Add an IdEdgePipe to the end of the Pipeline.

;; GremlinPipeline<S,com.tinkerpop.blueprints.Vertex>	idVertex(com.tinkerpop.blueprints.Graph graph) 
;; Add an IdVertexPipe to the end of the Pipeline.