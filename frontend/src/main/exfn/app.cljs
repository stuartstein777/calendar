(ns exfn.app
  (:require [reagent.dom :as dom]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [exfn.subscriptions]
            [exfn.events]
            [cljs-time.core :as time]
            [cljs-time.format :as fmt]
            ["moment" :as moment]
            [clojure.string :as str]))

(defn days-in-month [year month]
  (let [last-day (time/last-day-of-the-month (time/date-time year month 1))]
    (time/day last-day)))

(defn first-day-of-week-for-month [year month]
  (let [first-day-of-month (time/date-time year month 1)
        day-of-week (time/day-of-week first-day-of-month)
        days-to-subtract (mod (- day-of-week 1) 7)]
    (time/minus first-day-of-month (time/days days-to-subtract))))

(comment
  
  (defn pad-day [n]
    (cond (= n 0) "  " 
          (< n 10) (str " " n)
          :else n))
  
  (let [year 2024
        month 8
        first-day (time/day-of-week (time/date-time year month 1))
        days-in-month (days-in-month year month)
        day-titles ["M" "T" "W" "T" "F" "S" "S"]
        days (->> (concat
                   (repeat (dec first-day) 0)
                   (range 1 (inc days-in-month)))
                  (partition-all 7))]
    (println "" (str/join "   " day-titles))
    (let [formatted-days (map #(map pad-day %) days)]
      (doseq [row formatted-days]
        (println (str/join "  " row)))))
    )
  
  
  ;; M - 1,
  ;; T - 2,
  ;; W - 3,
  ;; T - 4,
  ;; F - 5,
  ;; S - 6,
  ;; S - 7
  ;; August 01 - 4 T
  ;; September 01 - 7
  ;; October 01 - 2
  ;; April 01 - 1 M

  #_(time/day-of-week (time/date-time 2024 8 1))

  

(defn month [month]
  (let [day-initials ["M" "T" "W" "T" "F" "S" "S"]]
    [:div.calendar-month
     {:style {:display        "flex"
              :flex-direction "column"
              :width          "fit-content"}}
     [:div.day-initials
      {:style {:display        "flex"
               :flex-direction "row"
               :font-weight    "bold"
               :background     "#f0f0f0"
               :border-bottom  "1px solid #ddd"}}
      (str "Test")
      ]]))

(defn format-date [date current-view]
  (case current-view
    :month (.format date "MMMM YYYY")
    :year (.format date "YYYY")
    :list (.format date "MMMM YYYY")))

(defn calendar-header []                                      ;; calendar header
  (let [current-date @(rf/subscribe [:current-date])
        current-view @(rf/subscribe [:current-view])]
    [:div.calendar-header
     
     [:span.chevron-left                                   ;; left chevron <
      [:i.fas.fa-chevron-left.ptr
       {:on-click #(rf/dispatch-sync [:prev-month])}]]
     
     [:span.calendar-header-month                          ;; month
      (str (format-date current-date current-view))]
     
     [:span.chevron-right                                  ;; right chevron >
      [:i.fas.fa-chevron-right.ptr
        {:on-click #(rf/dispatch-sync [:next-month])}]]
     
     [:span
      {:style {:float "right"}}
      [:i.fas.fa-list-alt.mr-9.ptr
       {:on-click #(rf/dispatch-sync [:update-view :list])}]
      [:i.fas.fa-calendar.mr-9.ptr
       {:on-click #(rf/dispatch-sync [:update-view :month])}]
      [:i.fas.fa-calendar-alt.ptr
       {:on-click #(rf/dispatch-sync [:update-view :year])}]]]))

(defn app
  []
  [:div.container
   [calendar-header]
   [:hr]
   [month]])

(defn ^:dev/after-load start []
  (dom/render [app]
              (.getElementById js/document "app"))) 

(defn ^:export init []
  (js/console.log "Initializing app")
  (start))

(defonce initialize (rf/dispatch-sync [:initialize]))