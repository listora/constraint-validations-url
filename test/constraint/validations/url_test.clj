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
   "http://user@example.com"
   "http://localhost:3050/images/53736c8f-307a-47a3-a479-ca99afcdeb0c"])

(def ^:private valid-https
  (map #(.replace % "http://" "https://") valid-http))

(def ^:private invalid-syntax
  [":"
   "^"
   "example.com/file[/].html"
   "http://"
   "http://www.example.com/file[/].html"])

(deftest test-with-valid-scheme
  (let [validator (url ["http"])]
    (doseq [url valid-http]
      (is (valid? validator url url-coercions))
      (is (instance? java.net.URI (coerce validator url url-coercions))
          "transforms the URL string into a java.net.URI")))
  (let [validator (url ["http" "https"])]
    (doseq [url (concat valid-http valid-https)]
      (is (valid? validator url url-coercions))
      (is (instance? java.net.URI (coerce validator url url-coercions))
          "transforms the URL string into a java.net.URI"))))

(deftest test-with-invalid-scheme
  (let [validator (url ["http"])]
    (doseq [url valid-https]
      (is (not (valid? validator url url-coercions)))
      (is (= (validate validator url url-coercions)
             [{:error :invalid-scheme
               :message "Expected URL with scheme \"http\""
               :found (URI. url)}])))))

(deftest test-with-invalid-syntax
  (doseq [url invalid-syntax]
    (is (not (valid? URI url url-coercions)))
    (is (= (validate URI url url-coercions)
           [{:error :invalid-url
             :message "Invalid URL syntax"
             :found url}]))))

(deftest test-with-path-only
  (doseq [url ["/" "/a/b/c"]]
    (is (valid? URI url url-coercions))
    (is (empty? (validate URI url url-coercions)))))

(deftest test-with-missing-authority
  (let [validator (url ["http"])]
    (doseq [url ["http:///"]]
      (is (not (valid? validator url url-coercions)))
      (is (= (validate validator url url-coercions)
             [{:error :url-missing-authority
               :message "URL requires an authority component"}])))))

(deftest test-with-valid-coercions
  (let [schemes ["http" "https"]
        validator (url schemes)]
    (doseq [url (concat valid-http valid-https)
            :let [url (URI. url)]]
      (is (valid? URI (str url) url-coercions))
      (is (= (coerce URI (str url) url-coercions) url)))))

(deftest test-with-invalid-coercions
  (let [schemes ["http" "https"]
        validator (url schemes)]
    (testing "no scheme"
      (is (= [{:error :invalid-scheme
               :message "Expected URL with scheme \"http\", or \"https\""
               :found (URI. "example.com")}
              {:error :url-missing-authority
               :message "URL requires an authority component"}]
             (validate validator "example.com" url-coercions))))
    (testing "invalid authority"
      (is (= [{:error :url-invalid-authority
               :message "Invalid authority component"
               :found ":example.com"}]
             (validate validator "http://:example.com" url-coercions))))))
