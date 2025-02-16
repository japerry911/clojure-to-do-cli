(ns todo-cli.core
  (:require [clojure.java.io :as io]  ;; For file handling
            [clojure.edn :as edn])    ;; For reading/writing EDN (Clojure data format)
  (:gen-class)) ;; Needed to generate a Java class when using `lein run`

(def file "tasks.edn") ;; File to store tasks

(defn load-tasks []
  (if (.exists (io/file file))  ;; Check if file exists
    (edn/read-string (slurp file)) ;; Read and parse EDN file
    [])) ;; If file doesn't exist, return an empty list

(defn save-tasks [tasks]
  (spit file (pr-str tasks))) ;; Convert tasks to EDN format and save to file

(defn add-task [task]
  (let [tasks (conj (load-tasks) {:task task :done false})]
    (save-tasks tasks)
    (println "Task added!")))

(defn list-tasks []
  (doseq [[idx {:keys [task done]}] (map-indexed vector (load-tasks))]
    (println (str idx ". " (if done "[X] " "[ ] ") task))))

(defn mark-done [index]
  (let [tasks (load-tasks)]
    (if (get tasks index) ;; Check if index is valid
      (do (save-tasks (assoc-in tasks [index :done] true))
          (println "Task marked as done!"))
      (println "Invalid task number!"))))

(defn remove-task [index]
  (let [tasks (load-tasks)]
    (if (get tasks index)
      (do (save-tasks (vec (concat (subvec tasks 0 index) (subvec tasks (inc index)))))
          (println "Task removed!"))
      (println "Invalid task number!"))))

(defn -main [& args]
  (case (first args)
    "add" (add-task (clojure.string/join " " (rest args)))
    "list" (list-tasks)
    "done" (mark-done (Integer. (second args)))
    "remove" (remove-task (Integer. (second args)))
    (println "Usage: lein run [add|list|done|remove] [task]")))
