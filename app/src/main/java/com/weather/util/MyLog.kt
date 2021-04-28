package com.weather.util

import android.text.TextUtils.isEmpty
import android.util.Log
import java.io.PrintWriter
import java.io.StringWriter

class MyLog {

    companion object {

        private val TAG = "weather.log"
        private val TAG_BIG = "$TAG.big"
        private val TAG_IMG = "$TAG.img"
        private val MAX_LARGE_LENGTH = 1024

        private var isEnableLog = true

        fun setEnableLog(isEnable: Boolean) {
            isEnableLog = isEnable
        }

        fun isEnableLog(): Boolean {
            return isEnableLog
        }

        fun v(log: String) {
            print(TAG, Log.VERBOSE, log, true)
        }

        fun d(log: String) {
            print(TAG, Log.DEBUG, log, true)
        }

        fun i(log: String) {
            print(TAG, Log.INFO, log, true)
        }

        fun w(log: String) {
            print(TAG, Log.WARN, log, true)
        }

        fun e(log: String) {
            print(TAG, Log.ERROR, log, true)
        }

        fun e(throwable: Throwable) {
            printError(throwable)
        }

        fun img(log: String) {
            print(TAG_IMG, Log.INFO, log, false)
        }

        fun big(source: String) {
            if (!isEnableLog) return
            if (isEmpty(source)) return

            val length = source.length

            var i = 0
            while (i < length) {
                if (i + MAX_LARGE_LENGTH < length) {
                    print(TAG_BIG, Log.DEBUG, source.substring(i, i + MAX_LARGE_LENGTH), false)
                } else {
                    print(TAG_BIG, Log.DEBUG, source.substring(i, length), false)
                }
                i += MAX_LARGE_LENGTH
            }
        }

        fun big(preLog: String, source: String) {
            if (!isEnableLog) return
            if (isEmpty(source)) return

            if (!isEmpty(preLog)) {
                print(TAG_BIG, Log.DEBUG, preLog, true)
            }

            val length = source.length

            var i = 0
            while (i < length) {
                if (i + MAX_LARGE_LENGTH < length) {
                    print(TAG_BIG, Log.DEBUG, source.substring(i, i + MAX_LARGE_LENGTH), false)
                } else {
                    print(TAG_BIG, Log.DEBUG, source.substring(i, length), false)
                }
                i += MAX_LARGE_LENGTH
            }
        }

        private fun print(tag: String, level: Int, log: String, isShowFilename: Boolean) {
            if (!isEnableLog) return

            val MAX_FILE_SIZE = 25

            val trace = Thread.currentThread().stackTrace

            var fileName = trace[4].fileName
            if (MAX_FILE_SIZE < fileName.length) {
                fileName = fileName.substring(0, MAX_FILE_SIZE)
            }

            var format = log
            if (isShowFilename) {
                format = String.format(
                    "[%-" + MAX_FILE_SIZE + "s:%5d] %s",
                    fileName,
                    trace[4].lineNumber,
                    log
                )
            }

            when (level) {
                Log.VERBOSE -> Log.v(tag, format)
                Log.DEBUG -> Log.d(tag, format)
                Log.INFO -> Log.i(tag, format)
                Log.WARN -> Log.w(tag, format)
                Log.ERROR -> Log.e(tag, format)
                else -> Log.d(tag, format)
            }
        }

        private fun printError(throwable: Throwable?) {
            if (!isEnableLog || throwable == null) return

            val sw = StringWriter()
            throwable.printStackTrace(PrintWriter(sw))

            Log.e(TAG, sw.toString())
        }
    }
}