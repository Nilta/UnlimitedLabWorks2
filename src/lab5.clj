(defn mergeList [lst1 lst2]
  (sort (concat lst1 lst2))
  )

;Clojure 1.5.1
;user=> (load "lab5")
;nil
;user=> (mergeList '(1 3 5) '(2 4 6))
;(1 2 3 4 5 6)
