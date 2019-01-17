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

package com.yxyhail.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.yxyhail.logger.LogFormatter;
import com.yxyhail.logger.Logger;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LogFormatter logFormatter = LogFormatter.onBuilder()
                .isLogEnable(BuildConfig.DEBUG)
//                .setGlobalTag("CustomTag")
//                .extraMethodOffset(2)
//                .showMethodCount(2)
//                .showThreadName(true)
                .build();
        Logger.initFormatter(logFormatter);
        Logger.d("onCreate-d");
        Logger.w("onCreate-w");
        Logger.i("onCreate-i");
        Logger.wtf("onCreate-wtf");
        Logger.jsonE("{\"test\":\"Logger\",\"array\":[{\"logger\":\"test\"},{\"logger2\":\"test2\"}]}");
        Logger.jsonD("[{\"logger\":\"test\"},{\"logger2\":\"test2\"}]");
        Logger.xmlD("<note>\n" +
                "<to>George</to>\n" +
                "<from>John</from>\n" +
                "<heading>Reminder</heading>\n" +
                "<body>Don't forget the meeting!</body>\n" +
                "</note>\n");
        Logger.tagOnce("onceTag").e("Test Once Tag");

        Logger.openBTag("BlockTag")
                .e("Test Block Tag");
        Logger.d("Test Block Tag -Block-d")
                .e("Test Block Tag Chain -Block -e");
        Logger.i("Test Block Tag -Block-i");
        Logger.tagOnce("Block/Once").e("Test Block OnceTag Tag -Block-e");
        Logger.e("Test Block Tag -Block-e");
        Logger.wtf("Test Close Block Tag-Block-wtf").closeBTag().w("Test Close Block Tag-Block-w");

        Logger.openBTag("openBTag")
                .e("Test Open Tag").w("Test Open Tag Block")
                .closeBTag()
                .d("Test Up Close Tag")
                .openBTag("SecondOpen")
                .w("Test Open Tag 2").e("Test Open Tag 2 Block")
                .closeBTag()
                .i("Block Tag Close Done");

    }
}
