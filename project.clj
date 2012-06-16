(defproject tanabata "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [[compojure "1.1.0"]
                 [org.clojure/clojure "1.3.0"]
                 [org.slf4j/slf4j-nop "1.6.4"]
                 [org.thymeleaf/thymeleaf "2.0.8"]
                 [ring/ring-jetty-adapter "1.1.0"]]
  :plugins [[lein-ring "0.7.1"]]
  :ring {:handler tanabata.core/handler})

