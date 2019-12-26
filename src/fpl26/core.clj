(ns fpl26.core)
(require '[clojure.core.async :as async :refer [<! >! go go-loop chan]])

(defn v [n r]
  (take n (repeatedly #(rand-int r))))

(defn cv [n]
  (take n (repeatedly #(async/chan 10))))

(def ar (v 10 50))
(println ar)

(def ich (async/chan 10))
(async/onto-chan ich ar)

(defn solve [chan n]
  (let [out (reduce conj [](cv n))]
    (async/go-loop []
      (when-some [val (<! chan)]
        (println val "mod " n " = " (mod val n))
        (>! (out (mod val n)) val)
        (recur)))
    out))

(def outs (solve ich 4))

(defn post [f]
  (print "Chan: ")
  (async/go-loop []
    (when-some [val (<! f)]
      (print val "; ")
      (recur))))

(defn res [outs]
  (Thread/sleep 500)
  (println)
  (when-some [val (first outs)]
    (post val)
    (recur (rest outs))))

(res outs)
