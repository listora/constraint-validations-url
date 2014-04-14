(ns constraint.validations.url
  (:import [java.net URL]
           [org.apache.commons.validator.routines UrlValidator])
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
    {:errors (set (invalid-url schemes value))}))

(defn url-coercions
  "Defines coercion from a java.lang.String to a java.net.URL given the allowed
  schemes."
  [schemes]
  {[String URL] (partial string->url schemes)})
