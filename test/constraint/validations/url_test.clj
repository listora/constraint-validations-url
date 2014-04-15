(ns constraint.validations.url-test
  (:import [java.net URL])
  (:require [clojure.test :refer :all]
            [constraint.core :refer :all]
            [constraint.validations.url :refer :all]))

(def ^:private valid-http
  "http://example.com")

(def ^:private valid-https
  "https://example.com")

(def ^:private domain-only
  "example.com")

(deftest test-with-scheme
  (let [validator (url ["http"])]
    (is (valid? validator valid-http))

    (is (instance? java.net.URL (coerce validator valid-http))
        "transforms the URL string into a java.net.URL")

    (is (not (valid? validator valid-https)))
    (is (not (valid? validator domain-only)))

    (is (= (validate validator domain-only)
           [{:error :invalid-url
             :message "Expected URL with scheme \"http\""
             :found domain-only}]))))

(deftest test-with-schemes
  (let [validator (url ["http" "https"])]
    (doseq [url [valid-http valid-https]]
      (is (valid? validator url))
      (is (instance? java.net.URL (coerce validator url))
          "transforms the http URL string into a java.net.URL"))

    (is (not (valid? validator domain-only)))

    (is (= (validate validator domain-only)
           [{:error :invalid-url
             :message "Expected URL with scheme \"http\", or \"https\""
             :found domain-only}]))))

(deftest test-coercions
  (let [schemes ["http" "https"]
        validator (url schemes)
        example-url (URL. "http://example.com")
        coercions (url-coercions schemes)]
    (is (valid? URL (str example-url) coercions))
    (is (= (coerce URL (str example-url) coercions) example-url))

    (is (= (validate (url schemes) domain-only)
           [{:error :invalid-url
             :message "Expected URL with scheme \"http\", or \"https\""
             :found domain-only}]))))
