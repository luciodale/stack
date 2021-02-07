(ns stack.core)

(defn- store-action-inner
  [store action store-with-prev]
  (if store-with-prev
    (let [head (first store)
          new-head (conj head action)]
      (conj (rest store) new-head))
    (conj store (list action))))

(defn store-action
  [db action & [{:keys [undo-path limit store-with-prev]}]]
  (let [path (or undo-path :undo)
        limit (or limit 25)]
    (update db path
            (fn [store]
              (let [new-store (store-action-inner store action store-with-prev)]
                (if (> (count new-store) limit)
                  (butlast new-store)
                  new-store))))))

(defn undo-action
  [db & [{:keys [undo-path redo-path]}]]
  (let [undo-path (or undo-path :undo)
        redo-path (or redo-path :redo)
        undo-store (get db undo-path)
        action (first undo-store)]
    {:action action
     :db (-> db
             (update undo-path rest)
             (update redo-path
                     #(if (seq undo-store)
                        (conj % action)
                        %)))}))

(defn redo-action
  [db & [{:keys [undo-path redo-path]}]]
  (let [undo-path (or undo-path :undo)
        redo-path (or redo-path :redo)
        redo-store (get db redo-path)
        action (first redo-store)]
    {:action action
     :db (-> db
             (update redo-path rest)
             (update undo-path
                     #(if (seq redo-store)
                        (conj % action)
                        %)))}))
