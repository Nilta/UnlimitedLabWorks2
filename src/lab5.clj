(require '[clojure.set :as s1])
(defn lab5 [a1 b1 c1] (s1/union c1 (s1/difference (s1/union a1 b1) (s1/intersection a1 b1))))
;(def a #{1 2 3 4 5})
;(def b #{3 4 5 6 7})
;(def c #{4 10 15})
;(print (lab5 a b c))

