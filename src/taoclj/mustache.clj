(ns taoclj.mustache
  (:require [clojure.string :as string]
            [clojure.tools.reader.edn :as edn]
            [me.shenfeng.mustache :as m]
            [taoclj.mustache.paths :as paths]))


(defn parse [raw]
  (if-not (.startsWith raw "<%{")
    {:config nil
     :content raw}
    (let [start (+ 2 (.indexOf raw "<%{"))
          end (+ 2 (.indexOf raw "}%>"))
          raw-config (.substring raw start end)]
      {:config (edn/read-string raw-config)
       :content (.substring raw (+ 1 end))})))


(defn load-hierarchy [template-path]
  (let [template (parse (slurp template-path))
        parent-path (-> template :config :parent)
        content (template :content)]
    (if-not parent-path content
      (string/replace
       (load-hierarchy
        (paths/resolve-file-relative-path template-path
                                          parent-path))
       "{{{ child-content }}}"
       content))))



(def ^:dynamic *file-ext* "tpl")
(def ^:dynamic *templates-directory* "ui")

(defn load-template [name]
  (load-hierarchy
     (paths/infer-template-path *templates-directory*
                                name
                                *file-ext*)))


(defn render [name data]
  (let [template (m/mk-template (load-template name))]
    (m/to-html template data)))


;; generate function that returns fn that
;; either caches or does not cache the constructed templates
;; to handle the dev/production mode issues?














