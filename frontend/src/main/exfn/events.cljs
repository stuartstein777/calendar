(ns exfn.events
  (:require [re-frame.core :as rf]
            [ajax.core :as ajax]
            [clojure.set :as set]
            [cljs.reader :as rdr]
            [clojure.walk :as wk]
            ["moment" :as moment]
            [day8.re-frame.http-fx]))

(rf/reg-event-db
 :initialize
 (fn [_ _]
   {:current-date (.startOf (moment) "month")
    :current-view :month}))

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
 :update-view
 (fn [db [_ view]]
   (assoc db :current-view view)))