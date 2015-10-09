(ns crawl.core
 (:require
           [net.cgrand.enlive-html :as html]))


(def lawyers-url "https://lawyers.law.cornell.edu/lawyers/injury-accident-law/")
(def lawyer-profile-url "https://lawyers.law.cornel.edu")

(def states ["vermont", "washington"])

(def OUTPUT-FILE "output.txt")


(defn grab-resource
  ([url]
   (html/html-resource (java.net.URL. (str url))))
  ([state num]
   (html/html-resource (java.net.URL. (str lawyers-url state "?page=" num)))))


;; this set stores the pages for the crawler's second pass
(def pages-for-second-pass-atom (atom #{}))


;; html/select returns an empty list if the call fails
(defn last-page? [full-page]
  (if (empty? (html/select full-page [:span.next]))
    true
    false))


(defn format-entry
  "Add entry to file"
  [details-map]
  (spit OUTPUT-FILE
        (str "\n"
       "Lawyer Name: " (:l-name details-map) "\n"
       "Lawyer Site: " (:l-site details-map) "\n"
       "Lawyer Phone: " (:l-phone details-map) "\n"
       "Lawyer Address: " (:l-address details-map) "\n"
       "Lawyer URL: " (:l-url details-map) "\n"
       "Lawyer State: " (:l-state details-map) "\n"
        "\n\n") :append true))


(defn format-address
  "Construct address string"
  [page]
  (let [street (first (get-in (first (html/select page [:div.street-address])) [:content] '("None Listed")))
        town (first (get-in (first (html/select page [:span.locality])) [:content] '("None Listed")))
        state (first (get-in (first (html/select page [:span.region])) [:content] '("None Listed")))
        zip (first (get-in (first (html/select page [:span.postal-code])) [:content] '("None Listed")))
        country (first (get-in (first (html/select page [:span.country])) [:content] '("None Listed")))]

  (str street " " town " " state " " zip " " country)))


; name phone, address, state, site

(defn detail-page
  "Scrape detail pages"
  [url]
  (let [page (grab-resource (str lawyer-profile-url url))
        name (first (get (first (html/select page [:h3.fn])) :content '("None Listed")))
        site (get-in (first (html/select page [:a.favicon--website])) [:attrs :href] "None Listed")
        phone (first (get-in (first  (html/select
                                       page [:table.profile_key_info :strong]))
                             [:content] '("None Listed")))
        state (first (get-in (first (html/select page [:nav#breadcrumbs
                                                       [:a (html/nth-of-type 2)]])) [:content]
                             '("None Listed")))
        address (format-address page)
        full-details {:l-name name :l-site site :l-phone phone :l-state state :l-address address :l-url url}]

    (format-entry full-details)))


;; actual crawling
(defn crawl
  "Crawl pages and use atom to store urls we should visit next"
  []
  (spit OUTPUT-FILE "") ;; truncate file for new crawl
  (doseq [state states] ;; for each state we want to look at
    (println "Crawling " state "...")
    (spit OUTPUT-FILE (str "-----STATE: " state "-------\n\n") :append true)

    (loop [pagenum 1
           full-page (grab-resource state pagenum)]

      (let [lawyer-divs (html/select-nodes* full-page [:div.l_item])]


        ;; grab info from each lawyer div

        (doseq [cur-div lawyer-divs] ;; for each lawyer profile in the current page
          ;; extract data from div
          (let [new-url (get-in (first (html/select cur-div [:a.url])) [:attrs :href])]

             (swap! pages-for-second-pass-atom conj new-url)))

        ;; are we on the last page of this state's results?

        (if (last-page? full-page)
          (do
            (println (str "Finished with " state))
            (println "Set Size: " (count @pages-for-second-pass-atom))
            (doall (map detail-page @pages-for-second-pass-atom))
            (reset! pages-for-second-pass-atom #{}))
          (do (Thread/sleep 1000) (recur (inc pagenum) (grab-resource state (inc pagenum)))))))))




(defn -main
  "Entry point for lein run"
  [& args]
  (println "crawling...")
  (crawl)
  (println "crawl complete."))