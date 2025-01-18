(ns exfn.logic
  (:require ["moment" :as moment]))

(defn events-for-month [events month-number year]
  (->> events
       (filter #(and (= month-number (.month (get % :date))) (= year (.year (get % :date)))))
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
                   (or (= 0 (.day d)) (= 6 (.day d)))))
         count)))

(defn holidays-for-selected-year [events start-date end-of-year]
  (->> events
       (filter #(= "Holiday" (:type %)))
       (map :date)
       (map #(moment %))
       (filter #(and (<= start-date %) (<= % end-of-year)))
       count))

(defn working-days-remaining [events selected-year]
  (let [end-of-year (moment (str selected-year "-12-31"))
        current-year (.year (moment))
        start-date (if (= current-year selected-year)
                     (.utc (moment))
                     (moment (str selected-year "-01-01")))
        days-remaining-this-year (+ 2 (.diff end-of-year start-date "days"))
        weekends (weekends-between-now-and-eoy start-date end-of-year)
        holidays-for-selected-year (holidays-for-selected-year events start-date end-of-year)]
    (if (< selected-year current-year)
      0
      (- days-remaining-this-year weekends holidays-for-selected-year))))

(comment
  
  ;; todo - when viewing a specific day, show the day in the date field, not the current day
)
  