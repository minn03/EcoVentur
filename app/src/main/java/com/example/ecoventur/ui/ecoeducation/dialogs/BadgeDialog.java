package com.example.ecoventur.ui.ecoeducation.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.example.ecoventur.R;
import com.example.ecoventur.ui.ecoeducation.models.Tips;

public class BadgeDialog extends Dialog {
    private Tips tip;
    private String descriptor;
    public BadgeDialog(Context context, String descriptor) {
        super(context);
        this.descriptor = descriptor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        TextView name = findViewById(R.id.name);
        TextView desc = findViewById(R.id.desc);

        desc.setText(descriptor);
    }

}
