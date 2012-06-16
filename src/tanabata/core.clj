(ns tanabata.core
  (:use compojure.core)
  (:require [compojure.route :as route]
            [compojure.handler]
            [ring.adapter.jetty])
  (:import (org.thymeleaf TemplateEngine)
           (org.thymeleaf.context Context)
           (org.thymeleaf.resourceresolver FileResourceResolver)
           (org.thymeleaf.templateresolver TemplateResolver)))

(defn create-engine
  [options]
  (let [tr (doto (TemplateResolver.)
             (.setResourceResolver (FileResourceResolver.))
             (.setTemplateMode (options :template-mode))
             (.setPrefix (options :prefix))
             (.setSuffix (options :suffix)))
        engine (doto (TemplateEngine.)
                 (.setTemplateResolver tr))]
    engine))

(def engine
  (create-engine {:prefix "src/tanabata/"
                  :suffix ".html"
                  :template-mode "XHTML"}))

(defn create-context
  [params]
  (reduce (fn [c [k v]] (doto c (.setVariable (name k) v))) (Context.) params))

(defn view
  [name params]
  (.process engine name (create-context params)))

(def wishs
  (ref [{"id" "bouzuya" "wish" "hello!"}
        {"id" "emanon001" "wish" "hello!!"}]))

(defn get-all-wishs
  []
  (view "index" {:wishs @wishs}))

(defn get-wish
  [id]
  (view "detail" {:id id :wishs (filter (fn [{i "id"}] (= i id)) @wishs)}))

(defn post-wish
  [id wish]
  (dosync
    (alter
      wishs
      (fn [wishs id wish]
        (take 10
              (conj
                (remove (fn [{i "id"}] (= i id)) wishs)
                {"id" id "wish" wish})))
      id
      wish))
  (get-all-wishs))

(defroutes
  main-routes
  (GET "/:id" [id] (get-wish id))
  (POST "/:id" [id :as {{wish :wish} :params}] (post-wish id wish))
  (GET "/" [] (get-all-wishs))
  (POST "/" {{id :id wish :wish} :params} (post-wish id wish))
  (route/not-found "Page not found"))

(def handler
  (compojure.handler/site main-routes))

(defn -main [& args]
  (let [port (Integer/valueOf (first args))]
    (ring.adapter.jetty/run-jetty handler {:port port})))

