(ns exfn.events
  (:require [re-frame.core :as rf]
            [ajax.core :as ajax]
            [clojure.set :as set]
            [cljs.reader :as rdr]
            [clojure.walk :as wk]
            ["moment" :as moment]
            [day8.re-frame.http-fx]))

(rf/reg-event-db
  :process-events
  (fn [db [_ events]]
    (-> db
        (assoc :calendar-events events))))

(rf/reg-event-fx
 :initialize
  (fn [{:keys [db]} [_ _]]
   {:db   {:current-date (.startOf (moment) "month")
           :current-view :month}
    :http-xhrio {:method :get
                 :uri    (str "https://stuartstein777.github.io/calendar/events.json")
                 :format (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [:process-events]
                 :on-failure      [:fail]}}))

(rf/reg-event-db
 :next-month
 (fn [db _]
   (-> db
       ;(update :x inc)
       (update :current-date #(-> % .clone (.add 1 "month"))))))

(rf/reg-event-db
 :prev-month
 (fn [db _]
   (-> db
       ;(update :x inc)
       (update :current-date #(-> % .clone (.subtract 1 "month"))))))

(rf/reg-event-db
 :next-year
 (fn [db _]
   (-> db
       (update :current-date #(-> % .clone (.add 1 "year"))))))

(rf/reg-event-db
 :prev-year
 (fn [db _]
   (-> db
       (update :current-date #(-> % .clone (.subtract 1 "year"))))))

(rf/reg-event-db
 :update-view
 (fn [db [_ view]]
   (assoc db :current-view view)))

