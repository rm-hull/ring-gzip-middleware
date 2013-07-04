(ns ring.middleware.gzip
  (:require [clojure.java.io :as io])
  (:import (java.util.zip GZIPOutputStream)
           (java.io InputStream
                    Closeable
                    File
                    PipedInputStream
                    PipedOutputStream)))

(defn- close [^Closeable stream]
  (.close stream))

(defn piped-gzipped-input-stream [in]
  (let [pipe-in (PipedInputStream.)
        pipe-out (PipedOutputStream. pipe-in)]
    (future                  ; new thread to prevent blocking deadlock
      (with-open [out (GZIPOutputStream. pipe-out)]
        (if (seq? in)
          (doseq [string in] (io/copy (str string) out))
          (io/copy in out)))
      (when (instance? Closeable in)
        (close in)))
    pipe-in))

(defn gzipped-response [resp]
  (-> resp
      (update-in [:headers]
                 #(-> %
                      (assoc "content-encoding" "gzip")
                      (dissoc "content-length" "Content-Length")))
      (update-in [:body] piped-gzipped-input-stream)))

(def accepted-status #{200 202})

(defn wrap-gzip [handler]
  (fn [req]
    (let [{:keys [body status] :as resp} (handler req)]
      (if (and (accepted-status status)
               (not (get-in resp [:headers "content-encoding"]))
               (or
                (and (string? body) (> (count body) 200))
                (seq? body)
                (instance? InputStream body)
                (instance? File body)))
        (let [accepts (get-in req [:headers "accept-encoding"] "")
              match (re-find #"(gzip|\*)(;q=((0|1)(.\d+)?))?" accepts)]
          (if (and match (not (contains? #{"0" "0.0" "0.00" "0.000"}
                                         (match 3))))
            (gzipped-response resp)
            resp))
        resp))))
