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