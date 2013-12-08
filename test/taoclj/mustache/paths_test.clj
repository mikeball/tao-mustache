(ns taoclj.mustache.paths-test
  (:require [clojure.test :refer [deftest is are]]
            [taoclj.mustache.paths :as paths]))


(deftest current-directory-extracted-from-path
  (are [given-path expected-directory]
       (= (paths/get-directory given-path)
          expected-directory)
       nil         ""
       ""          ""
       "a"         ""
       "a.txt"     ""
       "a/b"       "a"
       "a/b/c"     "a/b"
       "a/b/c.txt" "a/b"))


(deftest file-relative-paths-are-resolved
  (are [current relative expected]
       (= (paths/resolve-file-relative-path current
                                            relative)
          expected)
       nil nil nil
       "a" "b" "b"
       "a/b.txt" "c.txt" "a/c.txt"
       "a/b.txt" "../c.txt" "c.txt"
       "a/b/c" "../d" "a/d"
       "a/b/c" "../../d" "d"
       "a/b/c/d.tpl" "../../../e.tpl" "e.tpl"
       "a/b/c/d.tpl" "../../e.tpl" "a/e.tpl"))


(deftest keyword-paths-are-infered
  (are [base keyword extension expected]
       (= (paths/infer-template-path base keyword extension)
          expected)
       "a" nil "tpl" nil
       "a" :b "tpl" "a/b.tpl"
       "a/b" :c "tpl" "a/b/c.tpl"

       )
  )


