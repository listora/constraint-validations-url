(ns constraint.validations.url-test
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
  (let [schemes ["http"]]
    (is (valid? (url schemes) valid-http))

    (is (not (valid? (url schemes) valid-https)))
    (is (not (valid? (url schemes) domain-only)))

    (is (= (validate (url schemes) domain-only)
           [{:error :invalid-url
             :message "Expected URL with scheme \"http\""
             :found domain-only}]))))

(deftest test-with-schemes
  (let [schemes ["http" "https"]]
    (is (valid? (url schemes) valid-http))
    (is (valid? (url schemes) valid-https))

    (is (not (valid? (url schemes) domain-only)))

    (is (= (validate (url schemes) domain-only)
           [{:error :invalid-url
             :message "Expected URL with scheme \"http\", or \"https\""
             :found domain-only}]))))
