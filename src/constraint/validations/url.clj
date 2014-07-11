(ns constraint.validations.url
  (:import [java.net URI])
  (:require [clojure.string :as str]
            [constraint.core :refer [I Transform]]))

(def authority-pattern
  #"^([^:]*(:[^@]*)?@)?([^:/]*)(:\d*)?")

(defn- invalid-scheme [schemes value]
  {:error :invalid-scheme
   :message (str "Expected URL with scheme \""
                 (str/join "\", or \"" schemes) "\"")
   :found value})

(defn- missing-authority []
  {:error :url-missing-authority
   :message "URL requires an authority component"})

(defn- invalid-authority [authority]
  {:error :url-invalid-authority
   :message "Invalid authority component"
   :found authority})

(deftype Authority []
  Transform
  (transform* [_ data]
    (if (instance? URI data)
      (let [authority (.getAuthority data)]
        (cond
         (nil? authority)
         {:errors #{(missing-authority)}}
         (not (re-matches authority-pattern authority))
         {:errors #{(invalid-authority authority)}}
         :else {:value data})))))

(deftype Scheme [schemes]
  Transform
  (transform* [_ data]
    (if (instance? URI data)
      (if (contains? schemes (.getScheme data))
        {:value data}
        {:errors #{(invalid-scheme schemes data)}}))))

(deftype Url []
  Transform
  (transform* [_ data]
    (try
      {:value (URI. (str data))}
      (catch Exception e
        {:errors #{{:error :invalid-url
                    :message "Invalid URL syntax"
                    :found data}}}))))

(defn url-authority
  "Create a constraint that requires an authority component."
  []
  (Authority.))

(defn url-schemes
  "Create a constraint that requires a scheme component."
  [schemes]
  (Scheme. (set schemes)))

(defn url
  "Create a URL validation, that supports an optional list of schemes to allow.

  To only allow https URLs you could do something like:

    (url [\"https\"])"
  [schemes]
  (I URI (url-authority) (url-schemes schemes)))

(defn- string->url [value]
  (try
    {:value (URI. value)}
    (catch Exception e
      {:errors #{{:error :invalid-url
                  :message "Invalid URL syntax"
                  :found value}}})))

(def url-coercions
  "Defines coercion from a java.lang.String to a java.net.URI."
  {[String URI] string->url})
