/*
 * Copyright 2019 yxyhail
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yxyhail.logger;


public final class Logger {

    private static final LogConsole console = new LogConsoleImpl();

    private Logger() {
    }

    public static void initFormatter(LogFormatter formatter) {
        console.initFormatter(formatter);
    }

    public static LogConsole tagOnce(String onceTag) {
        return console.tagOnce(onceTag);
    }

    public static LogConsole openBTag(String blockTag) {
        return console.openBTag(blockTag);
    }

    public static LogConsole closeBTag() {
        return console.closeBTag();
    }

    public static LogConsole log(int priority, String tag, String msg, Throwable throwable) {
        return console.log(priority, "", tag, msg, throwable);
    }

    public static LogConsole d(String msg, Object... args) {
        return console.d(msg, args);
    }


    public static LogConsole e(String msg, Object... args) {
        return console.e(null, msg, args);
    }

    public static LogConsole e(Throwable throwable, String msg, Object... args) {
        return console.e(throwable, msg, args);
    }

    public static LogConsole i(String msg, Object... args) {
        return console.i(msg, args);
    }

    public static LogConsole v(String msg, Object... args) {
        return console.v(msg, args);
    }

    public static LogConsole w(String msg, Object... args) {
        return console.w(msg, args);
    }

    public static LogConsole wtf(String msg, Object... args) {
        return console.wtf(msg, args);
    }

    public static LogConsole jsonE(String json) {
        return console.jsonE(json);
    }

    public static LogConsole jsonD(String json) {
        return console.jsonD(json);
    }

    public static LogConsole xmlE(String xml) {
        return console.xmlE(xml);
    }

    public static LogConsole xmlD(String xml) {
        return console.xmlD(xml);
    }

}
