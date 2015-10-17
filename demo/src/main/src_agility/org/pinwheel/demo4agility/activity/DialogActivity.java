package org.pinwheel.demo4agility.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import org.pinwheel.agility.dialog.SimpleProgressDialog;
import org.pinwheel.agility.dialog.SweetDialog;
import org.pinwheel.demo4agility.R;

public class DialogActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        this.setContentView(R.layout.dialog);
        this.init();
    }

    private void init() {
        findViewById(R.id.button).setOnClickListener(this);

        SweetDialog.setDefaultAnimation(R.style.sweet_anim);
        SweetDialog.setDefaultLayout(R.layout.dialog_layout_confirm);
    }

    @Override
    public void onClick(View v) {
        new SweetDialog(this)
                .setSweetMessage(R.string.app_name)
                .setSweetPositive(R.string.app_name, new SweetDialog.OnClickToDismissListener() {
                    @Override
                    public void onClickAfterDismiss(View v) {
                        final SweetDialog progress = new SweetDialog(DialogActivity.this, R.layout.dialog_layout_progress)
                                .setSweetMessage(R.string.app_name)
                                .setSweetDelay(5000l)
                                .setSweetCancelable(true)
                                .setAttributeById(R.id.sweet_tips, "test progress", R.drawable.holo_btn_av_fast_forward, null);
                        progress.show();

                        progress.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                SimpleProgressDialog.create(DialogActivity.this, "正在加载...").show();
                            }
                        });

                    }
                })
                .setSweetNegativeGone()
                .setSweetCancelable(true)
                .show();
    }

}