(ns taoclj.mustache-test
  (:use clojure.test)
  (:require [taoclj.mustache :as tpl]))


(def dir "ui")
(def ext "tpl")

(deftest templates-are-parsed
  (is (= (tpl/parse "*content*")
         {:config nil :content "*content*"})))

(deftest templates-with-config-are-parsed
  (is (= (tpl/parse "<%{:parent \"../parent1.tpl\"}%>*content*")
         {:config {:parent "../parent1.tpl"} :content "*content*"})))

(deftest simple-templates-load
  (are [name expected] (= (tpl/load-template dir name ext)
                          expected)
       :simple1 "*simple1*"
       :adirectory.simple2 "*simple2*"))

(deftest child-templates-load
  (are [name expected] (= (tpl/load-template dir name ext)
                          expected)
       :child1 "<parent1>*child1*</parent1>"
       :adirectory.child2 "<parent1>*child2*</parent1>"
       :adirectory.bdirectory.child5 "<parent1>*child5*</parent1>"))


(def render (tpl/gen-renderer dir ext true))

(deftest simple-template-renders
  (is (= (render :simple1 {})
         "*simple1*")))

(deftest basic-template-renders
  (is (= (render :basic1 {:message "*hello1*"})
         "*hello1*")))

(deftest child-template-renders
  (is (= (render :child3 {:message "*hello child 3*"})
         "<parent1>*hello child 3*</parent1>")))

(deftest child-template-renders
  (is (= (render :adirectory.child4 {:message "*hello child 4*"})
         "<parent1>*hello child 4*</parent1>")))

(deftest third-level-templates-render
  (is (= (render :level3 {:message "*hi l3*"})
         "<l1><l2><l3>*hi l3*</l3></l2></l1>")))

(deftest multi-level-templates-render
  (is (= (render :adirectory.sublevel4 {:message "*hi sl4*"})
         "<l1><l2><sl3><sl4>*hi sl4*</sl4></sl3></l2></l1>")))



;; this belongs in mock!!!
;; (deftest paths-are-converted-to-keywords
;;   (are [path expected] (= (tpl/get-keyword-from-path path) expected)
;;        "/a" :a
;;        "/a/" :a.index
;;        "/a/b" :a.b))

;; (deftest file-paths-are-inferred-from-keywords
;;   (are [keyword expected] (= (tpl/get-path keyword) expected)
;;        :a '("resources" "templates" "a")
;;        :adir.b '("resources" "templates" "adir" "b")))
