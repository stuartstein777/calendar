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
  {"Poker Night"    "#317a28"
   "Night Out"      :red
   "Social"         :yellow
   "Pool"           :blue
   "Club"           :orange
   "Gig"            :pink
   "Climbing"       :brown
   "Hike"           :magenta})

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

(defn month-component [year month]
  (let [weeks (get-days-in-month year month)] 
    [:div.calendar-month
     {:style {:display        "flex"
              :flex-direction "column"
              :padding-left   10
              :padding-top    20
              :width          "fit-content"}}
     [:div
      {:style {:display "inline"
               :text-align "center"}}
      (str (fmt/unparse (fmt/formatter "MMMM") (time/date-time year month 1)))]
     [:div.day-initials
      {:style {:display        "flex"
               :flex-direction "row"
               :font-weight    "bold"
               :font-size      "0.8em"
               :margin-top     "5px"
               :background     "#f0f0f0"
               :color          "#000000"
               :border-bottom  "1px solid #f3f3f3"}}
      (for [day ["M" "T" "W" "T" "F" "S" "S"]]
        [:div {:style {:flex "1"
                       :text-align "center"}} day])]
     (for [week weeks]
       [:div.week
        {:style {:display        "flex"
                 :flex-direction "row"
                 :border-left  "1px solid #f3f3f3"
                 :border-right  "1px solid #f3f3f3"
                 :border-bottom  (get-bottom-border week weeks)}}
        (for [day week]
          [:div.day
           {:style {:flex "1"
                    :min-width "40px"
                    :max-width "40px"
                    :min-height "25px"
                    :max-height "25px"
                    :font-weight "0.8em"
                    :text-align "center"
                    :padding "2px"}}
           (if (= 0 day)
                ""
             day)])])]))

(defn pad-zero [num]
  (if (< num 10)
    (str "0" num)
    (str num)))

(defn display-year []
  (let [current-date @(rf/subscribe [:current-date])
        events @(rf/subscribe [:calendar-events])
        curent-month-events (lgc/events-for-month events (.month current-date))
        current-year (js/Number (.format current-date "YYYY"))]
    
    ;; display the calendar month grids
    
    [:div
     {:style {:display               "flex" 
              :grid-template-columns 1
              :grid-template-rows    1
              :gap                   "20px"}} 
     [:div.calendar-year
      {:style {:flex                  "1"
               :display               "grid"
               :grid-template-columns "repeat(4, 1fr)"
               :grid-template-rows    "repeat(3, 1fr)"
               :gap                   "10px"}}
      (for [month (range 1 13)]
        [month-component current-year month])]
     
     
     ;; display the legend and this months events
     
     [:div

      ;; legend
      
      [:div
       {:style {:flex           "0 0 auto"
                :display        "flex"
                :min-width      250
                :padding-top    20
                :flex-direction "column"}}

       (for [entry event-type-legend]
         [:div
          {:style {:flex "0 0 auto"
                   :gap  "0px"}}
          [:div {:style {:display     "flex"
                         :align-items "center"
                         :margin-top  "5px"
                         :margin-left "10px"}}
           [:div
            {:style {:width            "20px"
                     :height           "20px"
                     :background-color (val entry) ;; Circle color from key
                     :border-radius    "50%"
                     :margin-right     "10px"}}]
           [:div
            (key entry)]]])]
      
      ;; this months events
      [:div
       {:style {:padding-top 20
                :text-align "center"
                :display        "flex"
                :flex-direction "column"}}
       [:h4 (str (.format (moment) "MMMM") " events")]
      (for [event curent-month-events]
        [:div
         {:style {:display "flex"
                  :flex "0 0 auto"
                  :gap  "1px"
                  :padding "5px 0"}}
         [:div 
          {:style {:flex "0 0 auto"}}
          (str (lgc/day-of-week-short (.day (:date event))) " " (pad-zero (.format (:date event) "D")))]
         [:div 
          {:style {:flex "0 0 auto"
                   :margin-left "10px"}}
          (:name event)]]
        )]]]))

(defn format-date [date current-view]
  (case current-view
    :month (.format date "MMMM YYYY")
    :year (.format date "YYYY")
    :list (.format date "MMMM YYYY")))

(defn calendar-header []                                  ;; calendar header
  (let [current-date @(rf/subscribe [:current-date])
        current-view @(rf/subscribe [:current-view])]
    [:div.calendar-header
     
     [:span.chevron-left                                   ;; left chevron <
      [:i.fas.fa-chevron-left.ptr
       {:on-click 
        (fn []
           (if (= current-view :year)
            (rf/dispatch-sync [:prev-year])
            (rf/dispatch-sync [:prev-month])))}]]
     
     [:span.calendar-header-month                          ;; month
      (str (format-date current-date current-view))]
     
     [:span.chevron-right                                  ;; right chevron >
      [:i.fas.fa-chevron-right.ptr
        {:on-click 
         (fn []
           (if (= current-view :year)
             (rf/dispatch-sync [:next-year])
             (rf/dispatch-sync [:next-month])))}]]
     
     [:span
      {:style {:float "right"
               :padding-right 50}}
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
     (if (= :year current-view)
       [display-year]
       [:div])]))

(defn ^:dev/after-load start []
  (dom/render [app]
              (.getElementById js/document "app"))) 

(defn ^:export init []
  (js/console.log "Initializing app")
  (start))

(defonce initialize (rf/dispatch-sync [:initialize]))