(defproject zmaril/hermes "0.2.9"
  :description "Embedded Titan Graph"
  :url "https://github.com/zmaril/hermes"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :repositories [["typesafe/snapshots" "http://repo.typesafe.com/typesafe/snapshots/"]
                 ["apache" "http://repository.apache.org/content/repositories/releases/"]
                 ["sonatype" {:url "http://oss.sonatype.org/content/repositories/snapshots"}]
                 ["oracle" "http://download.oracle.com/maven/"]]  
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [commons-io/commons-io "2.2"]
                 [zmaril/ogre "0.0.1"]
                 [zmaril/archimedes "0.0.4"]
                 [com.thinkaurelius.titan/titan "0.2.0"]]
  :test-paths ["test" "src/test/hermes"]
  :plugins [[lein-test-bang-bang "0.2.0"]]
  :profiles {:dev {:plugins [[lein-kibit "0.0.7"]]}}
  :aliases {"test!" ["do" "clean," "deps," "test!!"]})

