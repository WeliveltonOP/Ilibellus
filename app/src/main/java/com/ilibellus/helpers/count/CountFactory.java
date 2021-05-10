
package com.ilibellus.helpers.count;

import com.ilibellus.Ilibellus;

import com.ilibellus.helpers.LanguageHelper;

public class CountFactory {

    private CountFactory() {
    }

    public static WordCounter getWordCounter() {
        String locale = LanguageHelper.getCurrentLocale(Ilibellus.getAppContext());
        return getCounterInstanceByLocale(locale);
    }

    static WordCounter getCounterInstanceByLocale(String locale) {
        switch (locale) {
            case "ja_JP":
                return new IdeogramsWordCounter();
            case "zh_CN":
                return new IdeogramsWordCounter();
            case "zh_TW":
                return new IdeogramsWordCounter();
            default:
                return new DefaultWordCounter();
        }
    }
}
