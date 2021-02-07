(ns core
  (:require
   [stack.core :as stack]
   [clojure.test :as t]))

(t/deftest store-action
  (t/testing "Test store action behaviour"
    (t/is
     (= {:undo (list '(2) '(1) '(0))}
        (reduce stack/store-action nil (range 3))))
    (t/is
     (= {:undo (list '(9 8) '(7 6) '(5 4) '(3 2) '(1 0))}
        (reduce #(stack/store-action %1 %2 {:store-with-prev (odd? %2)})
                nil
                (range 10))))
    (t/is
     (= {:undo (list '(9) '(8 7) '(6 5) '(4 3) '(2 1) '(0))}
        (reduce #(stack/store-action %1 %2 {:store-with-prev (even? %2)})
                nil
                (range 10))))))

(t/deftest undo-action
  (t/testing "Test undo action behaviour"
    (t/is
     (empty? (-> (stack/undo-action nil) :db :undo)))
    (let [{:keys [action db]} (-> nil
                                  (stack/store-action 4)
                                  (stack/undo-action))]
      (t/is (and (= '(4) action)
                 (= (list '(4)) (:redo db)))))
    (let [{:keys [action db]} (-> nil
                                  (stack/store-action 4)
                                  (stack/store-action 3 {:store-with-prev true})
                                  (stack/undo-action))]
      (t/is (and (= '(3 4) action)
                 (= (list '(3 4)) (:redo db)))))))

(t/deftest redo-action
  (t/testing "Test redo action behaviour"
    (t/is
     (empty? (-> (stack/redo-action nil) :db :redo)))
    (let [{:keys [action db]} (-> nil
                                  (stack/store-action 4)
                                  (stack/undo-action)
                                  :db
                                  (stack/redo-action))]
      (t/is (and (= '(4) action)
                 (= (list '(4)) (:undo db)))))
    (let [{:keys [action db]} (-> nil
                                  (stack/store-action 4)
                                  (stack/store-action 3 {:store-with-prev true})
                                  (stack/undo-action)
                                  :db
                                  (stack/redo-action))]
      (t/is (and (= '(3 4) action)
                 (= (list '(3 4)) (:undo db)))))))
