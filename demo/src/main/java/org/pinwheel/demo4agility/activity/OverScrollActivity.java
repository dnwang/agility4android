package org.pinwheel.demo4agility.activity;

import android.app.Activity;
import android.os.Bundle;
import org.pinwheel.agility.view.SweetScrollView;

public class OverScrollActivity extends Activity {

    SweetScrollView swipe;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        this.setContentView(org.pinwheel.demo4agility.R.layout.over_scroll);
        this.init();
    }

    private void init() {
        swipe = (SweetScrollView) findViewById(org.pinwheel.demo4agility.R.id.swipe);

        swipe.setNeedHold(150, 100);
        swipe.doSwipeToHold(true, 1000);
    }

}