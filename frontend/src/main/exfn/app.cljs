(ns exfn.app
  (:require [reagent.dom :as dom]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [exfn.subscriptions]
            [exfn.events]
            [exfn.logic :as lgc]
            [cljs-time.core :as time]
            [cljs-time.format :as fmt]
            ["moment" :as moment]
            [clojure.string :as str]))

(def event-type-legend
  {"poker night"    ["#317a28" :black]
   "night out"      ["#a02c2d" :white]
   "social"         ["#fed797" :black]
   "pool"           ["#5e96ae" :white]
   "gig"            ["#d3c0f9" :black]
   "climbing"       ["#fb8e7e" :black]
   "hike"           [:magenta  :black]
   "multiple"       ["#f3a4ed" :black]})

(defn day-detail []
  (let [selected-date @(rf/subscribe [:selected-date])
        events        @(rf/subscribe [:calendar-events])]
    [:div
     [:table.table.table-sm.table-responsive
      [:thead.thead-dark
       [:tr
        [:th.la "Day"]
        [:th.la "Name"]
        [:th.la "Type"]
        [:th.la "Location"]
        [:th.la "Description"]]]
      [:tbody
       (for [event (lgc/events-for-day events selected-date)]
         (do (prn event)
             [:tr
              [:td.la
               (str (lgc/day-of-week-short (.day (:date event))) " " (lgc/pad-zero (.format selected-date "D")))]
              [:td.la (:name event)]
              [:td.la (:type event)]
              [:td.la (:location event)]
              [:td.la (:description event)]]))]]
     [:btn.btn.btn-primary
      {:style {:float :left
               :margin-left 30}
       :on-click #(rf/dispatch [:update-view :year])}
      "Back"]]))

(defn days-in-month [year month]
  (let [last-day (time/last-day-of-the-month (time/date-time year month 1))]
    (time/day last-day)))

(defn get-days-in-month [year month]
  (let [first-day (time/day-of-week (time/date-time year month 1))
        days-in-month (days-in-month year month)
        to-pad-end (- 42 (+ days-in-month first-day))]
    (->> (concat
          (repeat (dec first-day) 0)
          (range 1 (inc days-in-month))
          (repeat to-pad-end 0))
         (partition-all 7))))

(defn get-bottom-border [week weeks]
  (when (= week (last weeks))
    "1px solid #f3f3f3"))

(defn get-event-color [events]
  (condp = (count events)
    0 "#2e3440"
    1 (get event-type-legend (str/lower-case (first events)))
    (get event-type-legend "multiple")))

;; on clicking day that has event, show a popup with the event details

(defn month-component [year month]
  (let [weeks (get-days-in-month year month)
        events @(rf/subscribe [:calendar-events])]
    [:div.calendar-month
     [:div.month-title 
      (str (fmt/unparse (fmt/formatter "MMMM") (time/date-time year month 1)))]
     
     ;; display day initials

     [:div.day-initials
      (for [[day idx] (map vector ["M" "T" "W" "T" "F" "S" "S"] (range 7))]
        ^{:key (str "idx-" idx "-DI-" day)}
        [:div.initial-day  day])]
     
     ;; display weeks

     (for [week weeks]
       
       ^{:key (str "week-" week "-month-" month)}
       [:div.week
        {:style {:border-bottom  (get-bottom-border week weeks)}}

     ;; display days for each week

        (for [[day idx] (map vector week (range 0 (count week)))]

          (let [events-for-day
                (if (not= 0 day) (lgc/events-types-on-date events (moment (str year "-" month "-" day))) [])
                holiday-day? (some #{"Holiday"} events-for-day)]
            ^{:key (str "idx-" idx "day-" day "-month-" month)}
            [:div.daybox
             {:style {:border (if holiday-day? "1px solid green" "0px solid #2e3440")
                      :background-color (if holiday-day? "#C1E1C1" "#2e3440")
                      :color (if holiday-day? "#000" "#fff")}}
             ;; display day number and circle if it has event
             [:div.day 
              (let [background-color (first (get-event-color events-for-day))
                    text-color (second (get-event-color events-for-day))]
                {:style {:background-color background-color
                         :color text-color}
                 :on-click (fn [_]
                             (let [moment (moment (lgc/build-date day month year))]
                               (rf/dispatch [:set-selected-date moment])
                               (rf/dispatch [:update-view :day])))})
              (if (= 0 day)
                ""
                day)]]))])]))

(defn display-year []
  (let [current-date @(rf/subscribe [:current-date])
        events @(rf/subscribe [:calendar-events])
        curent-month-events (lgc/events-for-month events (.month current-date))
        current-year (js/Number (.format current-date "YYYY"))
        working-days-remaining (lgc/working-days-remaining events current-year)]
    
    ;; display the calendar month grids
    [:div
     [:div.calendar-grid 
      [:div.calendar-year
       (for [month (range 1 13)]
         ^{:key (str "month-" month)}
         [month-component current-year month])]
      
     ;; display the legend and this months events
      
      [:div      ;; legend
       [:div.legend
        
        (for [entry event-type-legend]
          ^{:key (key entry)}
          [:div
           [:div.legend-entry 
            [:div.legend-key
             {:style {:background-color (first (val entry))}}]
            [:div
             (str/capitalize (key entry))]]])]
       
      ;; this months events
       
       [:div.current-months-events 
        [:h4 (str (.format (moment) "MMMM") " events")]
        (for [event curent-month-events]
          ^{:key (:id event)}
          [:div.current-months-events-entry 
           [:div.current-months-events-entry-date
            (str (lgc/day-of-week-short (.day (:date event))) " "
                 (lgc/pad-zero (.format (:date event) "D")))]
           [:div.current-months-events-entry-name 
            (:name event)]])]]]
            
            [:div
             {:style {:text-align :left
                      :padding-left 10
                      :padding-top 30}}
             [:h4
              
              (str "Working days remaining: " working-days-remaining)]]]))

(defn format-date [date current-view]
  (case current-view
    :month (.format date "MMMM YYYY")
    :year (.format date "YYYY")
    :list (.format date "MMMM YYYY")
    :day  (.format date "DD MMMM YYYY")))

(defn get-chevron-visibilty [current-view]
  (case current-view 
    :day   :none
    :inline))

(defn calendar-header []                                  ;; calendar header
  (let [current-date @(rf/subscribe [:current-date])
        current-view @(rf/subscribe [:current-view])]
    [:div.calendar-header
     
     [:span.chevron-left                                   ;; left chevron <
      {:style {:display (get-chevron-visibilty current-view)}}
      [:i.fas.fa-chevron-left.ptr
       {:on-click 
        (fn []
           (if (= current-view :year)
            (rf/dispatch-sync [:prev-year])
            (rf/dispatch-sync [:prev-month])))}]]
     
     [:span.calendar-header-month                          ;; month
      (str (format-date current-date current-view))]
     
     [:span.chevron-right                                  ;; right chevron >
      {:style {:display (get-chevron-visibilty current-view)}}
      [:i.fas.fa-chevron-right.ptr
        {:on-click 
         (fn []
           (if (= current-view :year)
             (rf/dispatch-sync [:next-year])
             (rf/dispatch-sync [:next-month])))}]]
     
     [:span.calendar-selectors
      [:i.fas.fa-list-alt.mr-9.ptr
       {:on-click #(rf/dispatch-sync [:update-view :list])}]
      [:i.fas.fa-calendar.mr-9.ptr
       {:on-click #(rf/dispatch-sync [:update-view :month])}]
      [:i.fas.fa-calendar-alt.ptr
       {:on-click #(rf/dispatch-sync [:update-view :year])}]]]))

(defn app []
  (let [current-view @(rf/subscribe [:current-view])]
    [:div
     {:style {:text-align :center}}
     [calendar-header]
     [:hr]
     (cond 
       (= :year current-view)  [display-year]
       (= :day current-view)   [day-detail]
       :else [:div])]))

(defn ^:dev/after-load start []
  (dom/render [app]
              (.getElementById js/document "app"))) 

(defn ^:export init []
  (js/console.log "Initializing app")
  (start))

(defonce initialize (rf/dispatch-sync [:initialize]))