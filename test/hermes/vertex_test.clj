(ns hermes.vertex-test
  (:use [clojure.test]
        [hermes.conf :only (clear-db conf)])
  (:require [hermes.core :as g]
            [hermes.vertex :as v]
            [hermes.type :as t]))

(deftest vertex-test
  (clear-db)
  (g/open conf)
  (g/transact!    
   (t/create-vertex-key-once :name String {:indexed true})
   (t/create-vertex-key-once :age Long {:indexed true})
   (t/create-vertex-key-once :first-name String {:indexed true})
   (t/create-vertex-key-once :last-name String {:indexed true}))
  
  (testing "Deletion of vertices"    
    (g/transact!
     (let [u (v/create! {:name "uniquename"})
           u-id (v/id-of u)]
       (v/delete! u)
       (is (=  nil (v/find-by-id u-id)))
       (is (empty? (v/find-by-kv :name "uniquename"))))))
  
  (testing "Simple property mutation" 
    (g/transact!
     (let [u (v/create! {:name "v1" :a 1 :b 1})]
       (v/assoc! u :b 2)
       (v/dissoc! u :a)
       (is (= 2   (v/get u :b)))
       (is (= nil (v/get u :a))))))

  (testing "Multiple property mutation"
    (g/transact!
     (let [u (v/create! {:name "v1" :a 0 :b 2})]
       (v/merge! u {:a 1 :b 2 :c 3})
       (is (= 1   (v/get u :a)))
       (is (= 2   (v/get u :b)))
       (is (= 3   (v/get u :c))))))

  (testing "Property map"
    (g/transact!
     (let [v1 (v/create! {:name "v1" :a 1 :b 2 :c 3})
           prop-map (v/to-map v1)]
       (is (= 1 (prop-map :a)))
       (is (= 2 (prop-map :b)))
       (is (= 3 (prop-map :c))))))

  (testing "Find by single id"
    (g/transact!
     (let [v1 (v/create! {:prop 1})
           v1-id (v/id-of v1)
           v1-maybe (v/find-by-id v1-id)]
       (is (= 1 (v/get v1-maybe :prop))))))

  (testing "Find by multiple ids"
    (g/transact!
     (let [v1 (v/create! {:prop 1})
           v2 (v/create! {:prop 2})
           v3 (v/create! {:prop 3})
           ids (map v/id-of [v1 v2 v3])
           v-maybes (apply v/find-by-id ids)]
       (is (= (range 1 4) (map #(v/get % :prop) v-maybes))))))

  (testing "Find by kv"
    (g/transact!
     (let [v1 (v/create! {:age 1 :name "A"})
           v2 (v/create! {:age 2 :name "B"})
           v3 (v/create! {:age 2 :name "C"})]
       (is (= #{"A"}
              (set (map #(v/get % :name) (v/find-by-kv :age 1)))))
       (is (= #{"B" "C"}
              (set (map #(v/get % :name) (v/find-by-kv :age 2))))))))

  (testing "Upsert!"
    (g/transact!
     (let [v1-a (v/upsert! :first-name
                           {:first-name "Zack" :last-name "Maril" :age 21})
           v1-b (v/upsert! :first-name
                           {:first-name "Zack" :last-name "Maril" :age 22})
           v2   (v/upsert! :first-name
                           {:first-name "Brooke" :last-name "Maril" :age 19})]
       (is (= 22
              (v/get (v/refresh (first v1-a)) :age)
              (v/get (v/refresh (first v1-b)) :age)))
       (v/upsert! :last-name {:last-name "Maril"
                              :heritage "Some German Folks"})
       (is (= "Some German Folks"
              (v/get (v/refresh (first v1-a)) :heritage)
              (v/get (v/refresh (first v1-b)) :heritage)
              (v/get (v/refresh (first v2)) :heritage))))))

  (testing "Unique upsert!"
    (g/transact!
     (let [v1-a (v/unique-upsert! :first-name
                                  {:first-name "Zack" :last-name "Maril" :age 21})
           v1-b (v/unique-upsert! :first-name
                                  {:first-name "Zack" :last-name "Maril" :age 22})
           v2   (v/unique-upsert! :first-name
                                  {:first-name "Brooke" :last-name "Maril" :age 19})]
       (is (= 22
              (v/get (v/refresh v1-a) :age)
              (v/get (v/refresh v1-b) :age)))       
       (is (thrown? Throwable #"There were 2 vertices returned."
                    (v/unique-upsert! :last-name {:last-name "Maril"}))))))
  
  (g/shutdown)
  (clear-db))