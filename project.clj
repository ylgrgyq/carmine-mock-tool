(defproject carmine-mock-tool "0.3.0"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "https://github.com/ylgrgyq/carmine-mock-tool"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.taoensso/carmine "2.13.0"]]
  :target-path "target/%s"
  :uberjar-name "carmine-mock-tool.jar"
  :deploy-repositories {"releases" {:url "https://repo.clojars.org" :creds :gpg}})
