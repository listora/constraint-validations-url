(ns constraint.validations.url
  (:import [org.apache.commons.validator.routines UrlValidator])
  (:require [clojure.string :as str]
            [constraint.core :refer [Transform]]))

(defn error-message [schemes]
  (str "Expected URL with scheme \""
       (str/join "\", or \"" schemes)
       "\""))

(defn- invalid-url [schemes value]
  {:error :invalid-url
   :message (error-message schemes)
   :found value})

(defn- valid? [schemes value]
  (.isValid (UrlValidator. (into-array String schemes)) value))

(deftype Url [schemes]
  Transform
  (transform* [_ value]
    (if-not (valid? schemes value)
      {:errors #{(invalid-url schemes value)}})))

(defn url
  "Create a URL validation, that supports an optional list of schemes to allow.

  To only allow https URLs you could do something like:

    (url [\"https\"])"
  [schemes]
  (Url. schemes))
