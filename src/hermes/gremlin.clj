(ns hermes.gremlin
  (:refer-clojure :exclude [iterate count and filter step memoize next or range])
  (:import (com.tinkerpop.gremlin.java GremlinPipeline)
           (com.tinkerpop.pipes PipeFunction Pipe)
           (com.tinkerpop.gremlin Tokens$T)))



(defmacro blank-pipe [& body]
  `(-> (GremlinPipeline.)
       ~@body))

(defmacro defpipe [name & body]
  `(def ~name (blank-pipe ~@body)))

(defmacro query [xs & body]  
  `(-> (GremlinPipeline. ~xs)
       ~@body))

(defn convert-symbol-to-token [s]
  (case s
    ==   Tokens$T/eq
    !=   Tokens$T/neq  
    >=   Tokens$T/gte
    >    Tokens$T/gt
    <=   Tokens$T/lte
    <    Tokens$T/lt))

(defn keywords-to-labels [labels]
  (map name (clojure.core/filter identity labels)))

(defn f-to-pipe [f]
  (reify PipeFunction
    (compute [_ arg] (f arg))))

;; GremlinPipeline<S,E>	_() 
;; Add an IdentityPipe to the end of the Pipeline.

(defn _ [p]
  (._ p))

;; <T> GremlinPipeline<S,T>
;; add(com.tinkerpop.pipes.Pipe<?,T> pipe) 

;;(add (blank-pipe (q/-->) (q/-->)))
(defn add [p e]
  (.add p e))

;; GremlinPipeline<S,E>	aggregate() 
;; Add an AggregatePipe to the end of the Pipeline.
;; GremlinPipeline<S,E>	aggregate(Collection<E> aggregate)
;; Add an AggregatePipe to the end of the Pipeline.
;; GremlinPipeline<S,E>	aggregate(Collection aggregate, com.tinkerpop.pipes.PipeFunction<E,?> aggregateFunction) 
;; Add an AggregatePipe to the end of the Pipeline.
;; GremlinPipeline<S,E>	aggregate(com.tinkerpop.pipes.PipeFunction<E,?> aggregateFunction) 
;; Add an AggregatePipe to the end of the Pipeline.

;;TODO: What should this do? What's it's purpose?

;; GremlinPipeline<S,E>	and(com.tinkerpop.pipes.Pipe<E,?>... pipes) 
;; Add an AndFilterPipe to the end the Pipeline.

;;(and (blank-pipe (q/-->) (q/count))
;;     (blank-pipe (q/-->) (q/count)))

(defn and [p & es]
  (.and p (into-array Pipe es)))

;; GremlinPipeline<S,E>	as(String name) 
;; Wrap the previous step in an AsPipe.

(defn as [p s]
  (.as p s))

;; GremlinPipeline<S,?>	back(int numberedStep) 
;; Add a BackFilterPipe to the end of the Pipeline.
;; GremlinPipeline<S,?>	back(String namedStep) 
;; Add a BackFilterPipe to the end of the Pipeline.

(defn back [p i]
  (.back p i))

;; GremlinPipeline<S,com.tinkerpop.blueprints.Vertex>	both(String... labels) 
;; Add a BothPipe to the end of the Pipeline.

(defn both [p & labels]
  (.both p (into-array String (keywords-to-labels labels))))

(defn <-> [& args]
  (apply both args))

;; GremlinPipeline<S,com.tinkerpop.blueprints.Edge>	bothE(String... labels) 
;; Add a BothEdgesPipe to the end of the Pipeline.

(defn both-edges [p & labels]
  (.bothE p (into-array String (keywords-to-labels labels))))

(defn <E> [& args]
  (apply both-edges args))

;; GremlinPipeline<S,com.tinkerpop.blueprints.Vertex>	bothV() 
;; Add a BothVerticesPipe to the end of the Pipeline.

(defn both-vertices [p]
  (.bothV p))

;; GremlinPipeline<S,?>	cap() 
;; Add a SideEffectCapPipe to the end of the Pipeline.

(defn cap [p]
  (.cap p))

;; GremlinPipeline<S,?>	copySplit(com.tinkerpop.pipes.Pipe<E,?>... pipes) 
;; Add a CopySplitPipe to the end of the pipeline.

(defn copy-split [p & es]
  (.copySplit p (into-array Pipe es)))

;; long	count() 
;; Return the number of objects iterated through the pipeline.

(defn count [p]
  (.count p))

;; GremlinPipeline<S,E>	dedup() 
;; Add a DuplicateFilterPipe to the end of the Pipeline.
;; GremlinPipeline<S,E>	dedup(com.tinkerpop.pipes.PipeFunction<E,?> dedupFunction) 
;; Add a DuplicateFilterPipe to the end of the Pipeline.

(defn dedup
  ([p] (.dedup p))
  ([p f] (.dedup p (f-to-pipe f))))

;; GremlinPipeline<S,E>	enablePath() 

(defn enable-path [p]
  (.enablePath p))

;; GremlinPipeline<S,E>	except(Collection<E> collection) 
;; Add an ExceptFilterPipe to the end of the Pipeline.

(defn except [p xs]
  (.except p xs))

;; GremlinPipeline<S,?>	exhaustMerge() 
;; Add an ExhaustMergePipe to the end of the pipeline.

(defn exhaust-merge [p]
  (.exhaustMerge p))

;; GremlinPipeline<S,?>	fairMerge() 
;; Add a FairMergePipe to the end of the Pipeline.

(defn fair-merge [p]
  (.fairMerge p))

;; Collection<E>	fill(Collection<E> collection) 
;; Fill the provided collection with the objects in the pipeline.

;;TODO: does this make sense from within clojure? 

;; GremlinPipeline<S,E>	filter(com.tinkerpop.pipes.PipeFunction<E,Boolean> filterFunction) 
;; Add an FilterFunctionPipe to the end of the Pipeline.

(defn filter [p f]
  (.filter p (f-to-pipe f)))

;; GremlinPipeline<S,List>	gather() 
;; Add a GatherPipe to the end of the Pipeline.
;; GremlinPipeline<S,?>	gather(com.tinkerpop.pipes.PipeFunction<List,?> function) 
;; Add a GatherPipe to the end of the Pipeline.

(defn gather
  ([p] (.gather p))
  ([p f] (.gather p (f-to-pipe f))))

;; GremlinPipeline<S,E>	groupBy(Map<?,List<?>> map, com.tinkerpop.pipes.PipeFunction keyFunction, com.tinkerpop.pipes.PipeFunction valueFunction) 
;; Add a GroupByPipe to the end of the Pipeline.
;; GremlinPipeline<S,E>	groupBy(Map reduceMap, com.tinkerpop.pipes.PipeFunction keyFunction, com.tinkerpop.pipes.PipeFunction valueFunction, com.tinkerpop.pipes.PipeFunction reduceFunction) 
;; Add a GroupByReducePipe to the end of the Pipeline.
;; GremlinPipeline<S,E>	groupBy(com.tinkerpop.pipes.PipeFunction keyFunction, com.tinkerpop.pipes.PipeFunction valueFunction) 
;; Add a GroupByPipe to the end of the Pipeline.
;; GremlinPipeline<S,E>	groupBy(com.tinkerpop.pipes.PipeFunction keyFunction, com.tinkerpop.pipes.PipeFunction valueFunction, com.tinkerpop.pipes.PipeFunction reduceFunction) 
;; Add a GroupByReducePipe to the end of the Pipeline.


;; GremlinPipeline<S,E>	groupCount() 
;; Add a GroupCountPipe to the end of the Pipeline.
;; GremlinPipeline<S,E>	groupCount(Map<?,Number> map) 
;; Add a GroupCountPipe to the end of the Pipeline.
;; GremlinPipeline<S,E>	groupCount(Map<?,Number> map, com.tinkerpop.pipes.PipeFunction keyFunction) 
;; Add a GroupCountPipe or GroupCountFunctionPipe to the end of the Pipeline.
;; GremlinPipeline<S,E>	groupCount(Map<?,Number> map, com.tinkerpop.pipes.PipeFunction keyFunction, com.tinkerpop.pipes.PipeFunction<com.tinkerpop.pipes.util.structures.Pair<?,Number>,Number> valueFunction) 
;; Add a GroupCountPipe or GroupCountFunctionPipe to the end of the Pipeline.
;; GremlinPipeline<S,E>	groupCount(com.tinkerpop.pipes.PipeFunction keyFunction) 
;; Add a GroupCountPipe or GroupCountFunctionPipe to the end of the Pipeline.
;; GremlinPipeline<S,E>	groupCount(com.tinkerpop.pipes.PipeFunction keyFunction, com.tinkerpop.pipes.PipeFunction<com.tinkerpop.pipes.util.structures.Pair<?,Number>,Number> valueFunction) 
;; Add a GroupCountPipe or GroupCountFunctionPipe to the end of the Pipeline.

;;TODO: extend groupCount further 
(defn group-count [p]
  (.group-count p))

;; GremlinPipeline<S,? extends com.tinkerpop.blueprints.Element>	has(String key, Object value) 
;; Add an IdFilterPipe, LabelFilterPipe, or PropertyFilterPipe to the end of the Pipeline.
;; GremlinPipeline<S,? extends com.tinkerpop.blueprints.Element>	has(String key, Tokens.T comparison, Object value) 
;; Add an IdFilterPipe, LabelFilterPipe, or PropertyFilterPipe to the end of the Pipeline.

(defmacro has
  ([p k v] `(.has ~p ~(name k) ~v))
  ([p k c v] `(.has ~p ~(name k) (convert-symbol-to-token '~c) ~v)))


;; GremlinPipeline<S,? extends com.tinkerpop.blueprints.Element>	hasNot(String key, Object value) 
;; Add an IdFilterPipe, LabelFilterPipe, or PropertyFilterPipe to the end of the Pipeline.
;; GremlinPipeline<S,? extends com.tinkerpop.blueprints.Element>
;; hasNot(String key, Tokens.T comparison, Object value) 
;; Add an IdFilterPipe, LabelFilterPipe, or PropertyFilterPipe to the
;; end of the Pipeline.

(defmacro has-not
  ([p k v] `(.has-not ~p ~(name k) ~v))
  ([p k c v] `(.has-not ~p ~(name k) (convert-symbol-to-token '~c) ~v)))



;; GremlinPipeline<S,Object>	id() 
;; Add an IdPipe to the end of the Pipeline.

(defn id [p] (.id p))

;; GremlinPipeline<S,com.tinkerpop.blueprints.Edge>	idEdge(com.tinkerpop.blueprints.Graph graph) 
;; Add an IdEdgePipe to the end of the Pipeline.

;; GremlinPipeline<S,com.tinkerpop.blueprints.Vertex>	idVertex(com.tinkerpop.blueprints.Graph graph) 
;; Add an IdVertexPipe to the end of the Pipeline.

;; GremlinPipeline<S,?>	ifThenElse(com.tinkerpop.pipes.PipeFunction<E,Boolean> ifFunction, com.tinkerpop.pipes.PipeFunction<E,?> thenFunction, com.tinkerpop.pipes.PipeFunction<E,?> elseFunction) 
;; Add an IfThenElsePipe to the end of the Pipeline.

(defn if-then-else [p pred then else]
  (.ifThenElse p (f-to-pipe pred) (f-to-pipe then) (f-to-pipe else)))

;; GremlinPipeline<S,com.tinkerpop.blueprints.Vertex>	in(String... labels) 
;; Add a InPipe to the end of the Pipeline.

(defn in [p & labels]
  (.in p (into-array String (keywords-to-labels labels))))

(defn <-- [& args]
  (apply in args))

;; GremlinPipeline<S,com.tinkerpop.blueprints.Edge>	inE(String... labels) 
;; Add an InEdgesPipe to the end of the Pipeline.

(defn in-edges [p & labels]
  (.inE p (into-array String (keywords-to-labels labels))))

(defn <E-- [& args]
  (apply in-edges args))


;; GremlinPipeline<S,? extends com.tinkerpop.blueprints.Element>	interval(String key, Object startValue, Object endValue) 
;; Add an IntervalFilterPipe to the end of the Pipeline.

(defn interval [p key start end]
  (.intreval p (name key) start end))

;; GremlinPipeline<S,com.tinkerpop.blueprints.Vertex>	inV() 
;; Add an InVertexPipe to the end of the Pipeline.

(defn in-vertex [p & labels]
  (.inV p))

;; void	iterate() 
;; Completely drain the pipeline of its objects.

(defn iterate [p]
  (.iterate p))

;; GremlinPipeline<S,String>	label() 
;; Add an LabelPipe to the end of the Pipeline.

(defn label [p]
  (.label p))

;; GremlinPipeline<S,E>	loop(int numberedStep, com.tinkerpop.pipes.PipeFunction<com.tinkerpop.pipes.branch.LoopPipe.LoopBundle<E>,Boolean> whileFunction) 
;; Add a LoopPipe to the end of the Pipeline.
;; GremlinPipeline<S,E>	loop(int numberedStep, com.tinkerpop.pipes.PipeFunction<com.tinkerpop.pipes.branch.LoopPipe.LoopBundle<E>,Boolean> whileFunction, com.tinkerpop.pipes.PipeFunction<com.tinkerpop.pipes.branch.LoopPipe.LoopBundle<E>,Boolean> emitFunction)
;; Add a LoopPipe to the end of the Pipeline.
;; GremlinPipeline<S,E>	loop(String namedStep, com.tinkerpop.pipes.PipeFunction<com.tinkerpop.pipes.branch.LoopPipe.LoopBundle<E>,Boolean> whileFunction) 
;; Add a LoopPipe to the end of the Pipeline.
;; GremlinPipeline<S,E>	loop(String namedStep, com.tinkerpop.pipes.PipeFunction<com.tinkerpop.pipes.branch.LoopPipe.LoopBundle<E>,Boolean> whileFunction, com.tinkerpop.pipes.PipeFunction<com.tinkerpop.pipes.branch.LoopPipe.LoopBundle<E>,Boolean> emitFunction) 
;; Add a LoopPipe to the end of the Pipeline.

;; GremlinPipeline<S,Map<String,Object>>	map() 
;; Add a PropertyMapPipe to the end of the Pipeline.

;;TODO: does this make sense from within clojure?
;;TODO: convert this over to Persistent
(defn into-map [p i]
  (into {} (.map p i)))

;; GremlinPipeline<S,E>	memoize(int numberedStep) 
;; Add a MemoizePipe to the end of the Pipeline.
;; GremlinPipeline<S,E>	memoize(int numberedStep, Map map) 
;; Add a MemoizePipe to the end of the Pipeline.
;; GremlinPipeline<S,E>	memoize(String namedStep) 
;; Add a MemoizePipe to the end of the Pipeline.
;; GremlinPipeline<S,E>	memoize(String namedStep, Map map) 
;; Add a MemoizePipe to the end of the Pipeline.

(defn memoize
  ([is] (.memoize is))
  ([is m] (.memoize is m)))

;; List<E>	next(int number) 
;; Return the next X objects in the pipeline as a list.

(defn next [p i]
  (.next p i))

;; GremlinPipeline<S,E>	optimize(boolean optimize) 
;; When possible, Gremlin takes advantage of certain sequences of pipes in order to make a more concise, and generally more efficient expression.

(defn optimize [p b]
  (.optimize p b))

;; GremlinPipeline<S,?>	optional(int numberedStep) 
;; Add an OptionalPipe to the end of the Pipeline.
;; GremlinPipeline<S,?>	optional(String namedStep) 
;; Add an OptionalPipe to the end of the Pipeline.
(defn optional [p s]
  (.optional p s))

;; GremlinPipeline<S,E>	or(com.tinkerpop.pipes.Pipe<E,?>... pipes) 
;; Add an OrFilterPipe to the end the Pipeline.

(defn or [p & es]
  (.or p (into-array Pipe es)))

;; GremlinPipeline<S,E>	order() 
;; Add an OrderPipe to the end of the Pipeline.
;; GremlinPipeline<S,E>	order(com.tinkerpop.pipes.PipeFunction<com.tinkerpop.pipes.util.structures.Pair<E,E>,Integer> compareFunction) 
;; Add an OrderPipe to the end of the Pipeline.

(defn order
  ([p] (.order p))
  ([p f compare] (.order p (f-to-pipe f) (f-to-pipe compare))))

;; GremlinPipeline<S,com.tinkerpop.blueprints.Vertex>	out(String... labels) 
;; Add an OutPipe to the end of the Pipeline.

(defn out [p & labels]
  (.out p (into-array String (keywords-to-labels labels))))

(defn --> [& args]
  (apply out args))

;; GremlinPipeline<S,com.tinkerpop.blueprints.Edge>	outE(String... labels) 
;; Add an OutEdgesPipe to the end of the Pipeline.
(defn out-edges [p & labels]
  (.outE p (into-array String (keywords-to-labels labels))))

(defn --E> [& args]
  (apply out-edges args))

;; GremlinPipeline<S,com.tinkerpop.blueprints.Vertex>	outV() 
;; Add an OutVertexPipe to the end of the Pipeline.

(defn out-vertex [p & labels]
  (.outV p))

;; GremlinPipeline<S,List>	path(com.tinkerpop.pipes.PipeFunction... pathFunctions) 
;; Add a PathPipe or PathPipe to the end of the Pipeline.

(defn path [p & args]
  (.path p (into-array PipeFunction (map f-to-pipe args))))

;; GremlinPipeline<S,Object>	property(String key) 
;; Add a PropertyPipe to the end of the Pipeline.

(defn property [p prop]
  (.property p (name prop)))

;; GremlinPipeline<S,E>	random(Double bias) 
;; Add a RandomFilterPipe to the end of the Pipeline.

(defn random [p bias]
  (.random p bias))

;; GremlinPipeline<S,E>	range(int low, int high) 
;; Add a RageFilterPipe to the end of the Pipeline.

(defn range [p low high]
  (.range p low high))

;; GremlinPipeline<S,E>	retain(Collection<E> collection) 
;; Add a RetainFilterPipe to the end of the Pipeline.

(defn retain [p coll]
  (.retain p coll))

;; GremlinPipeline<S,?>	scatter() 
;; Add a ScatterPipe to the end of the Pipeline.

(defn scatter [p]
  (.scatter p))

;; GremlinPipeline<S,com.tinkerpop.pipes.util.structures.Row>	select() 
;; Add a SelectPipe to the end of the Pipeline.
;; GremlinPipeline<S,com.tinkerpop.pipes.util.structures.Row>	select(Collection<String> stepNames, com.tinkerpop.pipes.PipeFunction... columnFunctions) 
;; Add a SelectPipe to the end of the Pipeline.
;; GremlinPipeline<S,com.tinkerpop.pipes.util.structures.Row>	select(com.tinkerpop.pipes.PipeFunction... columnFunctions) 
;; Add a SelectPipe to the end of the Pipeline.

(defn select [p f]
  (.select p (into-array PipeFunction [(f-to-pipe f)])))

;; GremlinPipeline<S,E>	sideEffect(com.tinkerpop.pipes.PipeFunction<E,?> sideEffectFunction) 
;; Add a SideEffectFunctionPipe to the end of the Pipeline.

(defn side-effect [p f]
  (.sideEffect p (f-to-pipe f)))

;; GremlinPipeline<S,E>	simplePath() 
;; Add a CyclicPathFilterPipe to the end of the Pipeline.

(defn simplePath [p]
  (.simplePath p))

;; GremlinPipeline<S,S>	start(S object) 
;; Add a StartPipe to the end of the pipeline.

(defn start [p o]
  (.start p o))

;; <T> GremlinPipeline<S,T>
;; step(com.tinkerpop.pipes.Pipe<E,T> pipe) 
;; Add an arbitrary Pipe to the end of the pipeline.
;; GremlinPipeline<S,?>	step(com.tinkerpop.pipes.PipeFunction function) 
;; Add a FunctionPipe to the end of the pipeline.

(defn step [p e]
  (.step p e))

;; GremlinPipeline<S,E>	store() 
;; Add an StorePipe to the end of the Pipeline.
;; GremlinPipeline<S,E>	store(Collection<E> storage) 
;; Add a StorePipe to the end of the Pipeline.
;; GremlinPipeline<S,E>	store(Collection storage, com.tinkerpop.pipes.PipeFunction<E,?> storageFunction) 
;; Add a StorePipe to the end of the Pipeline.
;; GremlinPipeline<S,E>	store(com.tinkerpop.pipes.PipeFunction<E,?> storageFunction) 
;; Add a StorePipe to the end of the Pipeline.
;;TODO: figure out what this is supposed to do within clojure

(defn store [p]
  (.store p))

;; GremlinPipeline<S,E>	table() 
;; Add a TablePipe to the end of the Pipeline.
;; GremlinPipeline<S,E>	table(com.tinkerpop.pipes.PipeFunction... columnFunctions) 
;; Add a TablePipe to the end of the Pipeline.
;; GremlinPipeline<S,E>	table(com.tinkerpop.pipes.util.structures.Table table) 
;; Add a TablePipe to the end of the Pipeline.
;; GremlinPipeline<S,E>	table(com.tinkerpop.pipes.util.structures.Table table, Collection<String> stepNames, com.tinkerpop.pipes.PipeFunction... columnFunctions) 
;; Add a TablePipe to the end of the Pipeline.
;; GremlinPipeline<S,E>	table(com.tinkerpop.pipes.util.structures.Table table, com.tinkerpop.pipes.PipeFunction... columnFunctions) 
;; Add a TablePipe to the end of the Pipeline.
;;TODO: What does a table entail? Implement this in full
(defn table
  ([p] (.table p))
  ([p f] (.table (f-to-pipe f))))

;; List<E>	toList() 
;; Return a list of all the objects in the pipeline.

(defn into-vec [p]
  (seq (.toList p)))

(defn into-set [p]
  (set (.toList p)))

;; <T> GremlinPipeline<S,T>
;; transform(com.tinkerpop.pipes.PipeFunction<E,T> function) 
;; Add a TransformFunctionPipe to the end of the Pipeline.

(defn transform [p f]
  (.transfrom p (f-to-pipe f)))

;; GremlinPipeline<S,E>	tree(com.tinkerpop.pipes.PipeFunction... branchFunctions) 
;; Add a TreePipe to the end of the Pipeline This step maintains an internal tree representation of the paths that have flowed through the step.
;; GremlinPipeline<S,E>	tree(com.tinkerpop.pipes.util.structures.Tree tree, com.tinkerpop.pipes.PipeFunction... branchFunctions) 
;; Add a TreePipe to the end of the Pipeline This step maintains an internal tree representation of the paths that have flowed through the step.

;;TODO: tree structure?
(defn tree [p & fs]
  (.tree p (into-array PipeFunction (map f-to-pipe fs))))