(defproject luciodale/stack "0.0.1"
  :description "Undo redo logic library"
  :url "https://github.com/luciodale/stack"
  :license {:name "MIT"}
  :source-paths ["src"]
  :profiles {:uberjar {:aot :all}}
  :deploy-repositories [["releases" :clojars]
                        ["snapshots" :clojars]])
