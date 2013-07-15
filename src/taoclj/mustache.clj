(ns taoclj.mustache
  (:require [clojure.string :as string]
            [clojure.tools.reader.edn :as edn]
            [me.shenfeng.mustache :as m])
  (:import [java.net URI]))

(def ^:dynamic *file-ext* ".tpl")
(def ^:dynamic *templates-directory* "ui")


(defn parse [raw]
  (if-not (.startsWith raw "<%{")
    {:config nil
     :content raw}
    (let [start (+ 2 (.indexOf raw "<%{"))
          end (+ 2 (.indexOf raw "}%>"))
          raw-config (.substring raw start end)]
      {:config (edn/read-string raw-config)
       :content (.substring raw (+ 1 end))})))


(defn- get-path 
  "Given a keyword, returns the file path as a sequence"
  [t]
  (concat (string/split *templates-directory* #"/")
          (string/split (name t) #"\.")))

(defn- normalize-path [path]
  (-> (URI. path)
      (.normalize)
      (.getPath)))

(defn- get-file-path 
  "Returns the file path given a sequence based path including file extension"
  [path]
  (str (string/join "/" path) 
       (if-not (.endsWith (last path) *file-ext*) *file-ext*)))

(defn- load-hierarchy [path]
  (let [template (parse (slurp (get-file-path path)))
        parent (-> template :config :parent)
        content (template :content)]
    (if-not parent
      content
      (let [num (if (.startsWith parent "../")
                       (- (count path) 2)
                       (- (count path) 1))]
        (string/replace (load-hierarchy (concat (take num path) [(string/replace parent "../" "")]))
                       "{{{ child-content }}}" 
                       content)))))

(defn load-template [name]
  (load-hierarchy (get-path name)))


(defn render [name data]
  (let [template (m/mk-template (load-template name))]
    (m/to-html template data)))







;; this belongs in lein-mock I believe!
;; (defn get-keyword-from-path [path]
;;   (let [full-path (if (.endsWith path "/") (str path "index") path)]
;;     (keyword (subs (string/replace full-path "/" ".") 1))))









