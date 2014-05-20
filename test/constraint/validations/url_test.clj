(ns constraint.validations.url-test
  (:import [java.net URI])
  (:require [clojure.test :refer :all]
            [constraint.core :refer :all]
            [constraint.validations.url :refer :all]))

(def ^:private valid-http
  ["http://:@example.com"
   "http://:password@example.com"
   "http://tiger:scotch@example.com"
   "http://user:@example.com"
   "http://user@example.com"])

(def ^:private valid-https
  (map #(.replace % "http://" "https://") valid-http))

(def ^:private invalid
  ["example.com"
   "http://:example.com"
   "http://:example.com"])

(deftest test-with-scheme
  (let [validator (url ["http"])]

    (doseq [url valid-http]
      (is (valid? validator url))
      (is (instance? java.net.URI (coerce validator url))
          "transforms the URL string into a java.net.URI"))

    (doseq [url (concat invalid valid-https)]
      (is (not (valid? validator url)))
      (is (= (validate validator url)
             [{:error :invalid-url
               :message "Expected URL with scheme \"http\""
               :found url}])))))

(deftest test-with-schemes
  (let [validator (url ["http" "https"])]

    (doseq [url (concat valid-http valid-https)]
      (is (valid? validator url))
      (is (instance? java.net.URI (coerce validator url))
          "transforms the URL string into a java.net.URI"))

    (doseq [url invalid]
      (is (not (valid? validator url)))
      (is (= (validate validator url)
             [{:error :invalid-url
               :message "Expected URL with scheme \"http\", or \"https\""
               :found url}])))))

(deftest test-coercions
  (let [schemes ["http" "https"]
        validator (url schemes)
        coercions (url-coercions schemes)]

    (doseq [url (concat valid-http valid-https)
            :let [url (URI. url)]]
      (is (and (is (valid? URI (str url) coercions))
               (is (= (coerce URI (str url) coercions) url)))))

    (doseq [url invalid]
      (is (= (validate validator url)
             [{:error :invalid-url
               :message "Expected URL with scheme \"http\", or \"https\""
               :found url}])))))
