package com.example.ecoventur.ui.greenspace;

import android.content.Context;
import android.net.Uri;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.browser.customtabs.CustomTabsIntent;

public class customTabLauncher {
    public static SpannableString makeTextSpannable (String text, String URL) {
        SpannableString spannableVenue = new SpannableString(text);
        ClickableSpan clickableSpanVenue = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                openUrl(view.getContext(),URL);
            }
        };
        spannableVenue.setSpan(clickableSpanVenue, 0, text.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableVenue;
    }
    public static void openUrl (Context context, String URL) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(context, Uri.parse(URL));
    }
}
