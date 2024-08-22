(ns exfn.app
  (:require [reagent.dom :as dom]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [exfn.subscriptions]
            [exfn.events]
            ["moment" :as moment]))

(defn calendar-days [year month]
  [[1 2 3 4 5 6 7]
   [8 9 10 11 12 13 14]
   [15 16 17 18 19 20 21]
   [22 23 24 25 26 27 28]
   [29 30 31]])

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
      #_(for [day day-initials]
        [:div.day-initial
         {:style {:flex       "1"
                  :text-align "center"
                  :padding    "10px"}} day])]
     #_[:div.day-numbers
      {:style {:display        "flex"
               :flex-direction "column"}}
      (for [row (calendar-days 2024 8)]
        [:div.week-row
         {:style {:display "flex"
                          :flex-direction "row"}}
         (for [day row]
           [:div.day-number
            {:style {:flex "1"
                     :text-align "center"
                     :padding "10px"
                     :border "1px solid #ddd"
                     :box-sizing "border-box"
                     :min-height "50px"}} day])])]]))

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