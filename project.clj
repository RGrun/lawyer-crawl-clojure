(defproject crawl "1.0"
  :description "Scrapes personal injury lawyer data from Cornell's lawyer database."
  :url "https://github.com/RGrun/lawyer-crawl"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [enlive "1.1.1"]]
  :main ^:skip-aot crawl.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
