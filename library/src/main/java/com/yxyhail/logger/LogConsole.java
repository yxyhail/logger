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

public interface LogConsole {

    void initFormatter(LogFormatter formatter);

    LogConsole tagOnce(String tag);

    LogConsole openBTag(String tag);

    LogConsole closeBTag();

    LogConsole d(String msg, Object... args);

    LogConsole e(String msg, Object... args);

    LogConsole e(Throwable throwable, String msg, Object... args);

    LogConsole w(String msg, Object... args);

    LogConsole i(String msg, Object... args);

    LogConsole v(String msg, Object... args);

    LogConsole wtf(String msg, Object... args);

    LogConsole jsonE(String json);

    LogConsole jsonD(String json);

    LogConsole xmlE(String xml);

    LogConsole xmlD(String xml);

    LogConsole log(int priority, String blockTag, String onceTag, String msg, Throwable throwable);

}
