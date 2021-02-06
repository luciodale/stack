(ns stack.core)

(defn- add-action
  [store action store-with-prev]
  (if store-with-prev
    (let [head (list (first store))
          new-head (cons action head)]
      (cons new-head (rest store)))
    (cons action store)))

(comment
  (-> nil
      (add-action 4 false)
      (add-action 3 false)
      (add-action 2 false)
      (add-action 1 true)
      ))

(defn store-action
  [db action & [{:keys [path limit store-with-prev]}]]
  (let [path (or path :undo)
        limit (or limit 25)]
    (update db path
            (fn [store]
              (let [new-store (add-action store action store-with-prev)]
                (if (> (count new-store) limit)
                  (butlast new-store)
                  new-store))))))

(-> {}
    (store-action 1)
    (store-action 2)
    (store-action 3))

(defn undo [])

(defn redo [])
