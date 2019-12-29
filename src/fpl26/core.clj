(ns fpl26.core)
(require '[clojure.core.async :as async :refer [<! >! go go-loop chan >!! <!! alts!]])

(defn makeChan [n]
  (take n (repeatedly #(async/chan 10))))

(def entry (async/chan 10))

(defn solve [chan n]
  (let [result (reduce conj [] (makeChan n))]
    (async/go-loop []
      (when-some [val (<! chan)]
        (>! (result (mod val n)) val)
        (println "Число " val " записано в канал для остатка " (mod val n))
        (recur)))
    result))

(def result (solve entry 10))

(defn printChan [actChan]
  (go-loop [printVal []]
    (let [[val _] (alts! [actChan] :default :complete)]
      (if (= val :complete)
        (print "Chan:" printVal)
        (recur (conj printVal val))))))

(defn printResult [result]
  (Thread/sleep 500)
  (println)
  (when-some [val (first result)]
    (printChan val)
    (recur (rest result))))


