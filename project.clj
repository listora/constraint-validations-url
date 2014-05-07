(defproject listora/constraint-validations-url "0.0.5"
  :description "Adds support for validating URLs to constraint"
  :url "https://github.com/listora/constraint-validations-url"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [listora/constraint "0.0.4"]
                 [commons-validator "1.4.0"]]

  :profiles {:dev {:plugins [[lein-difftest "2.0.0"]
                             [jonase/eastwood "0.1.1"]
                             [lein-bikeshed "0.1.6"]]

                   :aliases {"ci" ["do" ["difftest"] ["lint"]]
                             "lint" ["do" ["bikeshed"] ["eastwood"]]}}})
