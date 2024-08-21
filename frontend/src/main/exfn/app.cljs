(ns exfn.app
  (:require [reagent.dom :as dom]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [exfn.subscriptions]
            [exfn.events]
            ["moment" :as moment]))

(defn calendar-header []                                      ;; calendar header
  (let [current-date @(rf/subscribe [:current-date])]
    [:div.calendar-header
     
     [:span.chevron-left                                   ;; left chevron <
      [:i.fas.fa-chevron-left
       {:on-click #(rf/dispatch-sync [:prev-month])}]]
     
     [:span.calendar-header-month                          ;; month
      (str (.format current-date "MMMM YYYY"))]
     
     [:span.chevron-right                                  ;; right chevron >
      [:i.fas.fa-chevron-right
        {:on-click #(rf/dispatch-sync [:next-month])}]]
     
     [:span
      {:style {:float "right"}}
      [:i.fas.fa-list-alt.mr-9]
      [:i.fas.fa-calendar.mr-9]
      [:i.fas.fa-calendar-alt]]]))


(defn app 
  []
  [:div.container
   [calendar-header]
   [:hr]])

(defn ^:dev/after-load start []
  (dom/render [app]
              (.getElementById js/document "app"))) 

(defn ^:export init []
  (js/console.log "Initializing app")
  (start))

(defonce initialize (rf/dispatch-sync [:initialize]))
