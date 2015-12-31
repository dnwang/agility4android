package org.pinwheel.demo4agility.activity;

import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import org.pinwheel.agility.dialog.SimpleProgressDialog;
import org.pinwheel.agility.dialog.SweetDialog;
import org.pinwheel.demo4agility.R;

public class DialogActivity extends AbsTestActivity implements View.OnClickListener {

    @Override
    protected void onInitInCreate() {
        SweetDialog.setDefaultAnimation(R.style.sweet_anim);
        SweetDialog.setDefaultLayout(R.layout.dialog_layout_confirm);
    }

    @Override
    protected View getContentView() {
        Button button = new Button(this);
        button.setText("Show Dialog");
        button.setOnClickListener(this);
        return button;
    }

    @Override
    protected void doTest() {

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