(ns fpl26.core)
(require '[clojure.core.async :as async :refer [<! >! go go-loop chan >!!]])

(defn cv [n]
  (take n (repeatedly #(async/chan 10))))

(def ich (async/chan 10))

(defn solve [chan n]
  (let [out (reduce conj [] (cv n))]
    (async/go-loop []
      (when-some [val (<! chan)]
        (>! (out (mod val n)) val)
        (println "Число " val " записано в канал для остатка " (mod val n))
        (recur)))
    out))

(def outs (solve ich 10))

(defn post [f]
  (print "Chan: ")
  (go (loop [to (async/timeout 100)]
        (async/alt!
          to ()
          f ([val] (print val "; ")
             (recur (async/timeout 100)))))))

(defn res [outs]
  (Thread/sleep 500)
  (println)
  (when-some [val (first outs)]
    (post val)
    (recur (rest outs))))



