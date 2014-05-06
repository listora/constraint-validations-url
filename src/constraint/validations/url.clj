(ns constraint.validations.url
  (:import [java.net URL]
           [org.apache.commons.validator.routines RegexValidator UrlValidator])
  (:require [clojure.string :as str]
            [constraint.core :refer [Transform]]))

(def authority-pattern
  "^([^:]*(:[^@]*)?@)?([\\p{Alnum}\\-\\.]*)(:\\d*)?")

(def ^{:doc "Modifies the behaviour of the validator. We currently
support only the default options.

Options are managed by adding longs. Zero means use the default
validation options, which will require a list of schemes to allow,
permit use of hash fragments, and prohibit two slashes and local URLs.

See http://bit.ly/1nnUxy1 for more information."
       :private true} validator-options
       0)

(defn error-message [schemes]
  (str "Expected URL with scheme \""
       (str/join "\", or \"" schemes)
       "\""))

(defn- invalid-url [schemes value]
  {:error :invalid-url
   :message (error-message schemes)
   :found value})

(defn- valid? [schemes value]
  (.isValid (UrlValidator.
             (into-array String schemes)
             (RegexValidator. authority-pattern)
             validator-options)
            value))

(deftype Url [schemes]
  Transform
  (transform* [_ value]
    (if (valid? schemes value)
      {:value (URL. value)}
      {:errors #{(invalid-url schemes value)}})))

(defn url
  "Create a URL validation, that supports an optional list of schemes to allow.

  To only allow https URLs you could do something like:

    (url [\"https\"])"
  [schemes]
  (Url. schemes))

(defn- string->url [schemes value]
  (if (valid? schemes value)
    {:value (URL. value)}
    {:errors #{(invalid-url schemes value)}}))

(defn url-coercions
  "Defines coercion from a java.lang.String to a java.net.URL given the allowed
  schemes."
  [schemes]
  {[String URL] (partial string->url schemes)})
