(ns exfn.logic
  (:require ["moment" :as moment]))

(defn events-for-month [events month-number]
  (->> events
       (filter #(= month-number (.month (get % :date))))
       (remove #(= "Holiday" (:type %)))))

(defn events-for-day [events day]
  (->> events
       (filter #(-> % :date (.isSame day "day")))))

(def day-of-week-short
  {1 "MON"
   2 "TUE"
   3 "WED"
   4 "THU"
   5 "FRI"
   6 "SAT"
   0 "SUN"})
   
(defn events-types-on-date [events date]
  (let [result (->> events
                    (filter #(-> % :date (.isSame date "day")))
                    (mapv :type))]
    result))

(defn pad-zero [num]
  (if (< num 10)
    (str "0" num)
    (str num)))

(defn build-date [day month year]
  (str year "-" month "-" (pad-zero day)))

(defn debug [m x]
  (prn m x)
  x)

(defn weekends-between-now-and-eoy [now end-of-year]
  (let [total-days-remaining (inc (.diff end-of-year now "days"))]
    (->> (range (inc total-days-remaining))
         (map #(moment (str (-> (.clone now) (.add % "days")))))
         (filter (fn [d] 
                   (debug "day" d)
                   (or (= 0 (.day d)) (= 6 (.day d)))))
         count)))

(defn working-days-remaining [events selected-year]
  (let [end-of-year (moment (str selected-year "-12-31"))
        days-remaining (+ 2 (.diff end-of-year (.utc (moment)) "days"))
        holidays (->> events
                     (filter #(= "Holiday" (:type %)))
                     (map :date)
                     (map #(moment %))
                     (filter #(and (<= (.utc (moment)) %) (<= % end-of-year)))
                     count)
        weekends (weekends-between-now-and-eoy (.utc (moment)) end-of-year)
        ]
    (- days-remaining weekends holidays)))

(comment
  
)
  