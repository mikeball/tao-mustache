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



(defn load-template [directory name extension]
  (load-hierarchy
     (paths/infer-template-path directory
                                name
                                extension)))


(def load-cached-template (memoize load-template))


(defn gen-renderer [dir ext cached]
  (fn render [name data]
    (let [template (if cached
                     (load-cached-template dir name ext)
                     (load-template dir name ext))]

      (m/to-html (m/mk-template template)
                 data))))















