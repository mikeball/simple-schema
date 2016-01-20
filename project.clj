(defproject simple-schema "0.1.0-SNAPSHOT"

  :description "FIXME: write description"

  :url "http://example.com/FIXME"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.8.0-RC4"]
                 [criterium "0.4.3"]]

  :jvm-opts ["-XX:+TieredCompilation" "-XX:TieredStopAtLevel=4"]

  )
