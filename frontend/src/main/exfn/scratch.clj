(ns exfn.scratch
  (:require [cljs-time.core :as time]
            [cljs-time.format :as fmt]
           ))


;; October
;; M  T  W  T  F  S  S
;;    1  2  3  4  5  6
;; 7  8  9 10 11 12 13
;;14 15 16 17 18 19 20
;;21 22 23 24 25 26 27
;;28 29 30 31

; need to get the first day of the month
; need to get the number of days in the month
; pad the start with the number of days between the first day and the first day of the week
; pad the end with the number of days between the last day and the last day of the week
; partition by 7

(defn first-day-of-week-for-month [year month]
  (let [first-day-of-month (time/date-time year month 1)
        day-of-week (time/day-of-week first-day-of-month)
        days-to-subtract (mod (- day-of-week 1) 7)]
    (time/minus first-day-of-month (time/days days-to-subtract))))

(comment
  
  (let [first-day (first-day-of-week-for-month 2024 8)]
    (fmt/unparse (fmt/formatters :date) first-day))
  
  (+ 1 1)
  )


;; (defn days-in-month [year month]
;;   (time/month-length year month))

;; (defn first-day-of-month [year month]
;;   (time/day-of-week (time/local-date year month 1)))

;; (defn calendar-days [year month]
;;   (let [days (days-in-month year month)
;;         first-day (first-day-of-month year month)
;;         total-days (inc days)
;;         day-start (mod (- first-day 1) 7)
;;         days-list (concat (repeat day-start " ") (map str (range 1 (inc days))))
;;         rows (partition 7 (concat days-list (repeat " "))) ; Fill the rest with empty spaces
;;         rows (map (partial take 7) rows)] ; Ensure each row is exactly 7 days
;;     rows))

;; (defn calendar-month [year month]
;;   [:div.calendar-month
;;    {:style {:display "flex"
;;             :flex-direction "column"
;;             :width "fit-content"}}
;;    [:div.day-initials
;;     {:style {:display "flex"
;;              :flex-direction "row"
;;              :font-weight "bold"
;;              :background "#f0f0f0"
;;              :border-bottom "1px solid #ddd"}}
;;     (for [day day-initials]
;;       [:div.day-initial
;;        {:style {:flex "1"
;;                 :text-align "center"
;;                 :padding "10px"}} day])]
;;    [:div.day-numbers
;;     {:style {:display "flex"
;;              :flex-direction "column"}}
;;     (for [row (calendar-days year month)]
;;       [:div.week-row
;;        {:style {:display "flex"
;;                 :flex-direction "row"}}
;;        (for [day row]
;;          [:div.day-number
;;           {:style {:flex "1"
;;                    :text-align "center"
;;                    :padding "10px"
;;                    :border "1px solid #ddd"
;;                    :box-sizing "border-box"
;;                    :min-height "50px"}} day])])]])