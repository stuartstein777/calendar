(ns exfn.events
  (:require [re-frame.core :as rf]
            [ajax.core :as ajax]
            [clojure.set :as set]
            [cljs.reader :as rdr]
            [clojure.walk :as wk]
            ["moment" :as moment]
            [day8.re-frame.http-fx]))

(defn parse-event [event]
  {:id (get event :Id)
   :name (get event :Name)
   :description (get event :Description)
   :type (get event :Type)
   :location (get event :Location)
   :date (moment (get event :Date))})

(rf/reg-event-db
  :process-events
  (fn [db [_ events]]
    (let [processed-events (map parse-event events)]
      (-> db
          (assoc :calendar-events processed-events)))))

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
       (update :current-date #(-> % .clone (.add 1 "month"))))))

(rf/reg-event-db
 :prev-month
 (fn [db _]
   (-> db
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

(rf/reg-event-db
 :set-selected-date
 (fn [db [_ date]]
   (assoc db :selected-date date)))

