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
   
(defn events-types-on-date [events date]
  (let [result (->> events
                    (filter #(-> % :date (.isSame date "day")))
                    (mapv :type))]
    result))


(comment
  (let [date (moment "2024-08-01")
        events [
                {:id 1 :name "foo" :description "food" :type "Night Out" :location "loc" :date (moment "2024-08-01")}
                {:id 1 :name "bar" :description "foodb" :type "Club" :location "loc" :date (moment "2024-08-13")}
                {:id 1 :name "quax" :description "foodq" :type "Climbing" :location "loc" :date (moment "2024-08-30")}]]
    (->> events
         (filter #(-> % :date (.isSame date "day")))
         (map :type)
         (vec)))
    )
  