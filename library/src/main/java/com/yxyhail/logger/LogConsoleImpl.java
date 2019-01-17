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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.UnknownHostException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

class LogConsoleImpl implements LogConsole {

    private LogFormatter formatter;
    private static final int JSON_INDENT = 2;
    private final ThreadLocal<String> blockTag = new ThreadLocal<>();
    private final ThreadLocal<String> onceTag = new ThreadLocal<>();

    @Override
    public LogConsole tagOnce(String tag) {
        if (tag != null) onceTag.set(tag);
        return this;
    }

    @Override
    public LogConsole openBTag(String bTag) {
        if (bTag != null) blockTag.set(bTag);
        return this;
    }

    @Override
    public LogConsole closeBTag() {
        blockTag.remove();
        return this;
    }

    @Override
    public LogConsole d(String msg, Object... args) {
        log(Log.DEBUG, null, msg, args);
        return this;
    }

    @Override
    public LogConsole e(String msg, Object... args) {
        e(null, msg, args);
        return this;
    }

    @Override
    public LogConsole e(Throwable throwable, String msg, Object... args) {
        log(Log.ERROR, throwable, msg, args);
        return this;
    }

    @Override
    public LogConsole w(String msg, Object... args) {
        log(Log.WARN, null, msg, args);
        return this;
    }

    @Override
    public LogConsole i(String msg, Object... args) {
        log(Log.INFO, null, msg, args);
        return this;
    }

    @Override
    public LogConsole v(String msg, Object... args) {
        log(Log.VERBOSE, null, msg, args);
        return this;
    }

    @Override
    public LogConsole wtf(String msg, Object... args) {
        log(Log.ASSERT, null, msg, args);
        return this;
    }

    @Override
    public LogConsole jsonE(String json) {
        json(json, true);
        return this;
    }

    @Override
    public LogConsole jsonD(String json) {
        json(json, false);
        return this;
    }

    @Override
    public LogConsole xmlE(String xml) {
        xml(xml, true);
        return this;
    }

    @Override
    public LogConsole xmlD(String xml) {
        xml(xml, false);
        return this;
    }

    @Override
    public synchronized LogConsole log(int priority, String blockTag, String onceTag, String msg, Throwable throwable) {
        if (throwable != null && msg != null) {
            msg += " : " + getTraceToString(throwable);
        }
        if (throwable != null && msg == null) {
            msg = getTraceToString(throwable);
        }
        if (TextUtils.isEmpty(msg)) msg = "[msg is null]";

        if (formatter == null) {
            formatter = LogFormatter.onBuilder().build();
        }

        if (formatter.isLoggable()) {
            formatter.log(priority, blockTag, onceTag, msg);
        }
        return this;
    }

    @Override
    public void initFormatter(LogFormatter formatter) {
        this.formatter = formatter;
    }

    private void json(String json, boolean isJsonE) {
        if (TextUtils.isEmpty(json)) {
            String nullHint = "[json string is null]";
            if (isJsonE) {
                e(nullHint);
            } else {
                d(nullHint);
            }
            return;
        }
        try {
            json = json.trim();
            if (json.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(json);
                String msg = jsonObject.toString(JSON_INDENT);
                if (isJsonE) {
                    e(msg);
                } else {
                    d(msg);
                }
                return;
            }
            if (json.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(json);
                String msg = jsonArray.toString(JSON_INDENT);
                if (isJsonE) {
                    e(msg);
                } else {
                    d(msg);
                }
            }
        } catch (JSONException e) {
//            e.printStackTrace();
            if (isJsonE) {
                e(e.toString());
            } else {
                d(e.toString());
            }
        }
    }

    private void xml(String xml, boolean isXmlE) {
        if (TextUtils.isEmpty(xml)) {
            String nullHint = "[Xml string is null]";
            if (isXmlE) {
                e(nullHint);
            } else {
                d(nullHint);
            }
            return;
        }
        try {
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Source xmlInput = new StreamSource(new StringReader(xml));
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(xmlInput, xmlOutput);
            if (isXmlE) {
                e(xmlOutput.getWriter().toString().replaceFirst(">", ">\n"));
            } else {
                d(xmlOutput.getWriter().toString().replaceFirst(">", ">\n"));
            }
        } catch (TransformerException e) {
//            e.printStackTrace();
            if (isXmlE) {
                e(e.toString());
            } else {
                d(e.toString());
            }
        }
    }

    private String getOnceTag() {
        String tag = onceTag.get();
        if (tag != null) {
            onceTag.remove();
            return tag;
        }
        return null;
    }

    private String getBlockTag() {
        return blockTag.get();
    }

    private String createMsg(String msg, Object... args) {
        return args == null || args.length == 0 ? msg : String.format(msg, args);
    }

    private synchronized void log(int priority, Throwable throwable, String msg, Object... args) {
        String blockTag = getBlockTag();
        String onceTag = getOnceTag();
        String finalMsg = createMsg(msg, args);
        log(priority, blockTag, onceTag, finalMsg, throwable);
    }

    private String getTraceToString(Throwable tr) {
        if (tr == null) return "";

        Throwable t = tr;
        while (t != null) {
            if (t instanceof UnknownHostException) {
                return "";
            }
            t = t.getCause();
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }
}
