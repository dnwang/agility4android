package org.pinwheel.demo4agility.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.pinwheel.agility.util.BaseUtils;
import org.pinwheel.agility.view.MessageTipView;
import org.pinwheel.agility.view.controller.MessageTipController;
import org.pinwheel.demo4agility.R;

import java.util.HashSet;
import java.util.Set;

public class MessageTipsActivity extends AbsTesterActivity {

    private Dialog editDialog;
    private MessageTipController controller;
    private MessageTipView selectedTipsView;

    private final View.OnClickListener onClickListener = v -> {
        editDialog.show();
        selectedTipsView = (MessageTipView) v;
    };

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_message_tips);
    }

    @Override
    protected void beforeInitView() {

    }

    @Override
    protected void afterInitView() {
        showLogger(false);
        editDialog = createMessageCountEdtDialog();
        Set<MessageTipView> messageTipViewSet = new HashSet<>();
        final ViewGroup root = (ViewGroup) getWindow().getDecorView();
        findMessageTipView(root, messageTipViewSet);

        for (MessageTipView view : messageTipViewSet) {
            view.setOnClickListener(onClickListener);
            view.setCount(2);
        }

        controller = new MessageTipController(root);
    }

    private void findMessageTipView(ViewGroup root, Set<MessageTipView> nodes) {
        final int size = root.getChildCount();
        for (int i = 0; i < size; i++) {
            View view = root.getChildAt(i);
            if (view instanceof ViewGroup) {
                findMessageTipView((ViewGroup) view, nodes);
            } else if (view instanceof MessageTipView) {
                nodes.add((MessageTipView) view);
            }
        }
    }

    private Dialog createMessageCountEdtDialog() {
        final EditText countEdt = new EditText(this);
        countEdt.setInputType(InputType.TYPE_CLASS_NUMBER);
        return new AlertDialog.Builder(this)
                .setView(countEdt)
                .setPositiveButton("OK", (dialog, which) -> {
                    int count = BaseUtils.string2Int(countEdt.getText().toString(), 0);
                    onDialogPositiveClick(count);
                }).create();
    }

    private void onDialogPositiveClick(int count) {
        if (null != selectedTipsView) {
            selectedTipsView.setCount(count);
        }
    }

}