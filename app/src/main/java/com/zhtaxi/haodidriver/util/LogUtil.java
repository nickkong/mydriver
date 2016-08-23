/*
 * Copyright (C) 2016  richie (richie1990@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhtaxi.haodidriver.util;

import android.util.Log;

/**
 * Log日志输出
 * @author NickKong
 */
public final class LogUtil {
    private static String DEFAULT_TAG_PREFIX = "==HaodiDriver==";

    /**
     * 设置Log输出时TAG的前缀，建议使用项目名
     * @param tagPrefix TAG的前缀
     */
    public static void setTagPrefix(String tagPrefix){
        DEFAULT_TAG_PREFIX = tagPrefix;
    }

    /**
     * 输出{@link Log#VERBOSE}日志信息
     * @param isLog 是否输出，true输出，false不输出
     * @param tag 输出日志的标识，建议使用类名
     * @param msg 输出日志的信息
     */
    public static void v(boolean isLog, String tag, String msg){
        if (isLog) {
            Log.println(Log.VERBOSE, tag, DEFAULT_TAG_PREFIX + msg);
        }
    }

    /**
     * 输出{@link Log#VERBOSE}日志信息
     * @param isLog 是否输出，true输出，false不输出
     * @param tag 输出日志的标识，建议使用类名
     * @param msg 输出日志的信息
     * @param tr 输出日志的异常信息
     */
    public static void v(boolean isLog, String tag, String msg, Throwable tr){
        v(isLog, tag, msg + '\n' + Log.getStackTraceString(tr));
    }

    /**
     * 输出{@link Log#DEBUG}日志信息
     * @param isLog 是否输出，true输出，false不输出
     * @param tag 输出日志的标识，建议使用类名
     * @param msg 输出日志的信息
     */
    public static void d(boolean isLog, String tag, String msg){
        if (isLog) {
            Log.println(Log.DEBUG, tag, DEFAULT_TAG_PREFIX + msg);
        }
    }

    /**
     * 输出{@link Log#DEBUG}日志信息
     * @param isLog 是否输出，true输出，false不输出
     * @param tag 输出日志的标识，建议使用类名
     * @param msg 输出日志的信息
     * @param tr 输出日志的异常信息
     */
    public static void d(boolean isLog, String tag, String msg, Throwable tr){
        d(isLog, tag, msg + '\n' + Log.getStackTraceString(tr));
    }

    /**
     * 输出{@link Log#INFO}日志信息
     * @param isLog 是否输出，true输出，false不输出
     * @param tag 输出日志的标识，建议使用类名
     * @param msg 输出日志的信息
     */
    public static void i(boolean isLog, String tag, String msg){
        if (isLog) {
            Log.println(Log.INFO, tag, DEFAULT_TAG_PREFIX + msg);
        }
    }

    /**
     * 输出{@link Log#INFO}日志信息
     * @param isLog 是否输出，true输出，false不输出
     * @param tag 输出日志的标识，建议使用类名
     * @param msg 输出日志的信息
     * @param tr 输出日志的异常信息
     */
    public static void i(boolean isLog, String tag, String msg, Throwable tr){
        i(isLog, tag, msg + '\n' + Log.getStackTraceString(tr));
    }

    /**
     * 输出{@link Log#WARN}日志信息
     * @param isLog 是否输出，true输出，false不输出
     * @param tag 输出日志的标识，建议使用类名
     * @param msg 输出日志的信息
     */
    public static void w(boolean isLog, String tag, String msg){
        if (isLog) {
            Log.println(Log.WARN, tag, DEFAULT_TAG_PREFIX + msg);
        }
    }

    /**
     * 输出{@link Log#WARN}日志信息
     * @param isLog 是否输出，true输出，false不输出
     * @param tag 输出日志的标识，建议使用类名
     * @param msg 输出日志的信息
     * @param tr 输出日志的异常信息
     */
    public static void w(boolean isLog, String tag, String msg, Throwable tr){
        w(isLog, tag, msg + '\n' + Log.getStackTraceString(tr));
    }

    /**
     * 输出{@link Log#ERROR}日志信息
     * @param isLog 是否输出，true输出，false不输出
     * @param tag 输出日志的标识，建议使用类名
     * @param msg 输出日志的信息
     */
    public static void e(boolean isLog, String tag, String msg){
        if (isLog) {
            Log.println(Log.ERROR, tag, DEFAULT_TAG_PREFIX + msg);
        }
    }

    /**
     * 输出{@link Log#ERROR}日志信息
     * @param isLog 是否输出，true输出，false不输出
     * @param tag 输出日志的标识，建议使用类名
     * @param msg 输出日志的信息
     * @param tr 输出日志的异常信息
     */
    public static void e(boolean isLog, String tag, String msg, Throwable tr){
        e(isLog, tag, msg + '\n' + Log.getStackTraceString(tr));
    }
}
