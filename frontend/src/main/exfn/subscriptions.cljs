(ns exfn.subscriptions
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 :current-date
 (fn [db _]
   (get-in db [:current-date])))

(rf/reg-sub
 :x
 (fn [db _]
   (-> db :x)))
