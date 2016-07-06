(ns purchases-clojure.core
  (:require [clojure.string :as str]
            [compojure.core :as c]
            [ring.adapter.jetty :as j]
            [hiccup.core :as h]) 
  (:gen-class))

(defn read-purchases []
  (let [purchases (slurp "purchases.csv")
        purchases (str/split-lines purchases)
        purchases (map #(str/split % #",") 
                    purchases)
        header (first purchases)      ; designates the first line as a header
        purchases (rest purchases)
        purchases (map #(zipmap header %) 
                    purchases)]
        ;we can user a dummy variable if we don't want the code to stop
        ; _(println "Please Type A Category. [Furniture, Alcohol, Toiletries, Shoes, Food, Jewelry]") _ denotes *dummy  
    purchases))

(defn purchases-html [purchases]
   [:html
    [:body
     [:head [:title "Purchases"]]
     [:style "body {background-color: #92B06A}"]
     [:style "body {color: black}"]
     [:style "a {color: black}"]
     [:h3 "Please Click On A Category"]
     [:a {:href "/"} "All"] " "
     [:a {:href "/Toiletries"} "Toiletries"] " "
     [:a {:href "/Furniture"} "Furniture"] " "
     [:a {:href "/Alcohol"} "Alcohol"] " "
     [:a {:href "/Shoes"} "Shoes"] " "
     [:a {:href "/Food"} "Food"] " "
     [:a {:href "/Jewelry"} "Jewelry"]
    
     [:ol 
      (map (fn [purchase]
             [:li (str 
                    (get purchase "customer_id") " " 
                    (get purchase "date") " " 
                    (get purchase "credit_card") " " 
                    (get purchase "cvv") " " 
                    (get purchase "category"))])
           purchases)]]])

(defn filter-by-category [purchases category]
  (filter (fn [purchase]
            (= category (get purchase "category")))
      purchases))

(defonce server (atom nil)) 

(c/defroutes app
  (c/GET "/:category{.*}" [category]
    (let [purchases (read-purchases)
          purchases (if (= 0 (count category))
                      purchases
                      (filter-by-category purchases category))]
      (h/html (purchases-html purchases)))))

(defn -main []
  (if @server ;null check for the server atom to make sure we are actually stopping something
    (.stop @server)) ;stops the server so that we can rerun when we call main again
  (reset! server (j/run-jetty app {:port 3000 :join? false})))
    
                   
                   
                 
         
              