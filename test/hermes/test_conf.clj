(ns hermes.test-conf)

(def conf {:storage {:backend "embeddedcassandra"
                     :hostname "localhost"
                     :keyspace "test7"
                     :cassandra-config-dir "file:///Users/zackmaril/Projects/aurelius-stack/hermes/resources/test-cassandra.yaml"}})
