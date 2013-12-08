(ns taoclj.mustache.paths
  (:require [clojure.string :as string]
            [pathetic.core :as pathetic]))


(defn get-directory [path]
  (if (nil? path) ""
    (let [last (.lastIndexOf path "/")]
      (if (< last 0) ""
        (subs path 0 last)))))


(defn resolve-file-relative-path
  [current-file relative-path]
  (if (or (nil? current-file)
          (nil? relative-path))

    nil ;; throw exeption instead?

    (let [current-directory (get-directory current-file)]
      (pathetic/normalize (str current-directory
                               (if-not (= "" current-directory) "/")
                               relative-path)))))


(defn infer-template-path [base-directory keyword extension]
  (if (or (nil? base-directory) (nil? keyword) (nil? extension))
    nil
    (str base-directory
         "/"
         (string/replace (name keyword) #"\." "/")
         "." extension)))



;; this belongs in lein-mock I believe?
;; might be better here since it's with the other logic.
;; (defn get-keyword-from-path [path]
;;   (let [full-path (if (.endsWith path "/") (str path "index") path)]
;;     (keyword (subs (string/replace full-path "/" ".") 1))))


