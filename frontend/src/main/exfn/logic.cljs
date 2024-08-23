(ns exfn.logic
  (:require ["moment" :as moment]))

(defn events-for-month [events month-number]
  (filter #(= month-number (.month (get % :date))) events))

(def day-of-week-short
  {1 "MON"
   2 "TUE"
   3 "WED"
   4 "THU"
   5 "FRI"
   6 "SAT"
   7 "SUN"})
   