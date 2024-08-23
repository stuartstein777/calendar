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

(defn get-days-in-month [year month]
  (let [first-day (time/day-of-week (time/date-time year month 1))
        days-in-month (days-in-month year month)
        to-pad-start (dec first-day)
        to-pad-end (- 35 days-in-month to-pad-start)]
    (->> (concat
          (repeat to-pad-start 0)
          (range 1 (inc days-in-month))
          (repeat to-pad-end 0))
         (partition-all 7))))

(defn month-component [year month]
  (let [days (get-days-in-month year month)]
    [:div.calendar-month
     {:style {:display        "flex"
              :flex-direction "column"
              :padding-left   10
              :width          "fit-content"}}
     [:div
      {:style {:display "inline"
               :text-align "center"}}
      (str (fmt/unparse (fmt/formatter "MMMM") (time/date-time year month 1)))]
     [:div.day-initials
      {:style {:display        "flex"
               :flex-direction "row"
               :font-weight    "bold"
               :margin-top     "5px"
               :background     "#f0f0f0"
               :color          "#000000"
               :border-bottom  "1px solid #ddd"}}
      (for [day ["M" "T" "W" "T" "F" "S" "S"]]
        [:div {:style {:flex "1"
                       :text-align "center"}} day])]
     (for [week days]
       [:div.week
        {:style {:display        "flex"
                 :flex-direction "row"
                 :border-bottom  "1px solid #ddd"}}
        (for [day week]
          [:div.day
           {:style {:flex "1"
                    :border-left "1px solid #ddd"
                    :min-width "50px"
                    :max-width "50px"
                    :border-right "1px solid #ddd"
                    :border-top "1px solid #ddd"
                    :text-align "center"
                    :padding "5px"}}
           (if (= 0 day)
             ""
             day)])])]))

(defn display-year []
  (let [current-date @(rf/subscribe [:current-date])
        ;current-year (time/year current-date)
        ]
    ;; layout a flex box grid 3x4 with a month-component for each month in the year
    [:div.calendar-year
     {:style {:display        "grid"
              :grid-template-columns "repeat(4, 1fr)"
              :grid-template-rows    "repeat(3, 1fr)"
              :gap "10px"}}
     (for [month (range 1 13)]
       [month-component 2024 month])]))

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