(ns hermes.persistent.conf
  (:import (org.apache.commons.io FileUtils)))

(def conf {:storage {:backend "embeddedcassandra"
                     :hostname "127.0.0.1"
                     :keyspace "hermestest"
                     :cassandra-config-dir
                     ;;TODO: make this work from root of hermes
                     "file:///Users/zackmaril/Projects/aurelius-stack/hermes/resources/test-cassandra.yaml"}})

(defn clear-db []
  (FileUtils/deleteDirectory (java.io.File. "/tmp/hermes-test")))
