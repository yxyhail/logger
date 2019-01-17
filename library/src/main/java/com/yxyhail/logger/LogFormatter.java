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

import android.text.TextUtils;
import android.util.Log;


public class LogFormatter {

    private static final char VERTICAL_LINE = '│';

    private static final char LEFT_TOP_BORDER = '┌';

    private static final char LEFT_MIDDLE_BORDER = '├';

    private static final char LEFT_BOTTOM_BORDER = '└';

    private static final String MIDDLE_DIVIDER = "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄";

    private static final String OUTSIDE_DIVIDER = "────────────────────────────────────────────────────────";

    private static final String TOP_BORDER = LEFT_TOP_BORDER + OUTSIDE_DIVIDER + OUTSIDE_DIVIDER;

    private static final String BOTTOM_BORDER = LEFT_BOTTOM_BORDER + OUTSIDE_DIVIDER + OUTSIDE_DIVIDER;

    private static final String MIDDLE_BORDER = LEFT_MIDDLE_BORDER + MIDDLE_DIVIDER + MIDDLE_DIVIDER;

    private static final int BLOCK_SIZE = 4000;

    private static final int MIN_STACK_OFFSET = 5;

    private final String globalTag;

    private final boolean isLogEnable;

    private final int extraMethodOffset;

    private final int showMethodCount;

    private final boolean showThreadName;


    private LogFormatter(Builder builder) {
        globalTag = builder.globalTag;
        isLogEnable = builder.isLogEnable;

        showThreadName = builder.showThreadName;

        showMethodCount = builder.showMethodCount;
        extraMethodOffset = builder.extraMethodOffset;
    }

    boolean isLoggable() {
        return isLogEnable;
    }

    void log(int priority, String blockTag, String onceTag, String msg) {
        String tag = formatTag(blockTag, onceTag);
        printTopBorder(priority, tag);
        printStackTrace(priority, tag, showMethodCount);
        byte[] bytes = msg.getBytes();
        int length = bytes.length;
        if (length <= BLOCK_SIZE) {
            if (showMethodCount > 0) {
                printDivider(priority, tag);
            }
            printBody(priority, tag, msg);
            printBottomBorder(priority, tag);
            return;
        }
        if (showMethodCount > 0) {
            printDivider(priority, tag);
        }
        for (int i = 0; i < length; i += BLOCK_SIZE) {
            int count = Math.min(length - i, BLOCK_SIZE);
            printBody(priority, tag, new String(bytes, i, count));
        }
        printBottomBorder(priority, tag);
    }


    private void printTopBorder(int priority, String tag) {
        println(priority, tag, TOP_BORDER);
    }

    private void printStackTrace(int priority, String tag, int methodCount) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        if (showThreadName) {
            println(priority, tag, VERTICAL_LINE + " Thread: " + Thread.currentThread().getName());
            printDivider(priority, tag);
        }
        //方法行 在显示上缩进
        StringBuilder methodIndent = new StringBuilder();

        int stackOffset = getStackOffset(trace) + extraMethodOffset;

        if (methodCount + stackOffset > trace.length) {
            methodCount = trace.length - stackOffset - 1;
        }

        for (int i = methodCount; i > 0; i--) {
            int stackIndex = i + stackOffset;
            if (stackIndex >= trace.length) {
                continue;
            }
            StringBuilder builder = new StringBuilder();
            builder.append(VERTICAL_LINE)
//                    .append('')
                    .append(methodIndent)
                    .append(" (")
                    .append(trace[stackIndex].getFileName())
                    .append(":")
                    .append(trace[stackIndex].getLineNumber())
                    .append(")")
                    .append(" ")
                    .append(getSimpleClassName(trace[stackIndex].getClassName()))
                    .append(".")
                    .append(trace[stackIndex].getMethodName());
            methodIndent.append("   ");
            println(priority, tag, builder.toString());
        }
    }


    private void printDivider(int priority, String tag) {
        println(priority, tag, MIDDLE_BORDER);
    }


    private void printBody(int priority, String tag, String blockMsg) {
        String lineSep = System.getProperty("line.separator");
        if (lineSep == null || TextUtils.isEmpty(lineSep)) {
            lineSep = "\n";
        }
        String[] linesMsg = blockMsg.split(lineSep);
        for (String msg : linesMsg) {
            println(priority, tag, VERTICAL_LINE + " " + msg);
        }
    }

    private String formatTag(String blockTag, String onceTag) {
        String finalTag = this.globalTag;
        if (!TextUtils.isEmpty(blockTag) && !TextUtils.equals(this.globalTag, blockTag)) {
            finalTag += "-" + blockTag;
        }
        if (!TextUtils.isEmpty(onceTag) && !TextUtils.equals(this.globalTag, onceTag)) {
            finalTag += "-" + onceTag;
        }
        return finalTag;
    }

    private void printBottomBorder(int priority, String tag) {
        println(priority, tag, BOTTOM_BORDER);
    }

    private int getStackOffset(StackTraceElement[] trace) {
        for (int i = MIN_STACK_OFFSET; i < trace.length; i++) {
            String name = trace[i].getClassName();
            if (!name.equals(LogConsoleImpl.class.getName()) && !name.equals(Logger.class.getName())) {
                return --i;
            }
        }
        return -1;
    }

    private void println(int priority, String tag, String msg) {
        if (TextUtils.isEmpty(tag)) tag = "Logger";
        Log.println(priority, tag, msg);
    }

    private String getSimpleClassName(String name) {
        int lastIndex = name.lastIndexOf(".");
        return name.substring(lastIndex + 1);
    }

    public static Builder onBuilder() {
        return new Builder();
    }

    public static class Builder {
        boolean isLogEnable = true;

        String globalTag = "Logger";

        int showMethodCount = 1;

        int extraMethodOffset = 0;

        boolean showThreadName = false;

        private Builder() {
        }

        public LogFormatter build() {
            return new LogFormatter(this);
        }

        public Builder isLogEnable(boolean isLogEnable) {
            this.isLogEnable = isLogEnable;
            return this;
        }

        public Builder setGlobalTag(String globalTag) {
            this.globalTag = globalTag;
            return this;
        }

        public Builder extraMethodOffset(int extraOffset) {
            extraMethodOffset = extraOffset;
            return this;
        }

        public Builder showMethodCount(int count) {
            showMethodCount = count;
            return this;
        }

        public Builder showThreadName(boolean isShow) {
            showThreadName = isShow;
            return this;
        }

    }
}
