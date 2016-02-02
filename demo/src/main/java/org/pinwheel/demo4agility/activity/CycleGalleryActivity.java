package org.pinwheel.demo4agility.activity;

import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.pinwheel.agility.adapter.SimpleArrayAdapter;
import org.pinwheel.agility.adapter.SimplePagerAdapter;
import org.pinwheel.agility.util.BaseUtils;
import org.pinwheel.agility.view.SweetCircularView;
import org.pinwheel.agility.view.SweetProgress;
import org.pinwheel.agility.view.drag.DragListView;
import org.pinwheel.demo4agility.R;
import org.pinwheel.demo4agility.test.ImageLoaderManager;


public class CycleGalleryActivity extends AbsTestActivity {
    private static final String TAG = CycleGalleryActivity.class.getSimpleName();

    private final static String[] urls = {
            "http://fujian.86516.com/forum/201201/05/1442483og4p90y34494azq.jpg",
            "http://img4.duitang.com/uploads/item/201510/01/20151001133614_wLVtH.jpeg",
            "http://g.hiphotos.baidu.com/zhidao/pic/item/d1a20cf431adcbefe8b998a6aeaf2edda2cc9f85.jpg",
            "http://cdn.duitang.com/uploads/item/201510/01/20151001133607_5AcSe.jpeg",
            "http://cdn.duitang.com/uploads/item/201510/01/20151001215638_83TKd.jpeg",
            "http://i10.topit.me/o061/1006131724f61f9271.jpg",
            "http://img4.duitang.com/uploads/item/201407/14/20140714114023_KiMLj.jpeg",
            "http://cdn.duitang.com/uploads/item/201510/01/20151001133546_uh2aR.thumb.700_0.jpeg",
            "http://cdn.image.market.hiapk.com/data/upload/2014/04_02/06/sy_20140402050511_7012.JPEG",
            "http://i5.topit.me/5/be/38/1125422661efb38be5o.jpg",
            "http://f5.topit.me/5/34/8f/1144175736add8f345o.jpg",
            "http://image.vmanhua.com/cl/2013/06/08/2735440381_E74S9DzOu_c.jpg",
            "http://img5.duitang.com/uploads/item/201401/28/20140128191858_2At3m.jpeg",
            "http://img5.duitang.com/uploads/item/201412/10/20141210201048_PwJXZ.thumb.700_0.jpeg",
            "http://image.tianjimedia.com/uploadImages/2013/191/I0CHP56O61C5.jpg",
            "http://a.hiphotos.baidu.com/zhidao/pic/item/8b13632762d0f703c935737c0afa513d2797c58f.jpg",
            "http://attachment.huaxi100.com/data/attachment/forum/201303/24/115616pbd44jkqroinjioz.jpg",
            "http://imgsrc.baidu.com/forum/w%3D580/sign=e9f54ac25143fbf2c52ca62b807cca1e/1e46b8ec08fa513d093e03143f6d55fbb0fbd9a9.jpg",
            "http://dl.bizhi.sogou.com/images/2012/05/12/14195.jpg",
            "http://img3.100bt.com/upload/ttq/20130119/1358593776153.jpg",
            "http://sy.art-child.com/uploads/allimg/140219/320-140219150ZYV.jpg",
            "http://new-img2.ol-img.com/985x695/79/173/limxb7qSJ8w.jpg",
            "http://cdn.image.market.hiapk.com/data/upload/2014/04_02/06/sy_20140402050510_7454.JPEG",
            "http://img.kejixun.com/2014/0604/20140604052120926.jpg",
            "http://attachment.huaxi100.com/data/attachment/forum/201303/24/115621yb7ibzqh33m0c3fq.jpg",
            "http://img.sootuu.com/vector/2006-4/2006420114951558.jpg",//0
            "http://img.sootuu.com/vector/200801/097/074.jpg",//1
            "http://img.sootuu.com/vector/200801/097/075.jpg",//2
            "http://img.sootuu.com/vector/200801/097/076.jpg",//3
            "http://img.sootuu.com/vector/200801/097/077.jpg",//4
            "http://img.sootuu.com/vector/200801/097/078.jpg",//5
            "http://img.sootuu.com/vector/200801/097/079.jpg",//6
            "http://www.cnr.cn/newscenter/gjxw/list/201308/W020130807387031641496.jpg",//7
            "http://www.dlxww.com/newscenter/content/images/attachement/jpg/site2/20120714/051342201886015_change_SB05B714Cb001.jpg",//8
            "http://pic8.nipic.com/20100623/636809_140233068651_2.jpg",//9
            "http://www.bodaoedu.com/10th/show/10logo.jpg",//10
            "http://d.hiphotos.baidu.com/baike/w%3D268/sign=2f38872c810a19d8cb0383030bfb82c9/fd039245d688d43f64134f6f7c1ed21b0ff43bf1.jpg",//11
            "http://hiphotos.baidu.com/zhidao/pic/item/64380cd7c47c895aa144df82.jpg",//12
            "http://p7.qhimg.com/dr/250_500_/t0124169fb4230d69f7.jpg",//13
            "http://pic.baike.soso.com/p/20140221/20140221050206-2084250523.jpg",//14
            "http://pics.sc.chinaz.com/Files/pic/icons128/5966/w3.png"
    };

    private SweetCircularView gallery;

    private SimpleArrayAdapter adapter = new SimpleArrayAdapter<Integer>() {
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            logout("Adapter: getView() position:" + position);
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_item, null);
            }
            ImageView image = BaseUtils.getViewByHolder(convertView, R.id.image);
            TextView txt = BaseUtils.getViewByHolder(convertView, R.id.text);
            txt.setText(String.valueOf(position));
            ImageLoaderManager.getInstance(parent.getContext()).setImageByImageLoader(image, urls[position % urls.length]);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(CycleGalleryActivity.this, position + "", Toast.LENGTH_SHORT).show();
                }
            });
            return convertView;
        }
    };

    @Override
    protected void onInitInCreate() {

    }

    @Override
    protected View getContentView() {
        final FrameLayout container = new FrameLayout(this);

        gallery = new SweetCircularView(this);
        gallery.setAdapter(adapter);
        gallery.setAnimatorAdapter(new GalleryAnimatorAdapter());
        gallery.setMinimumHeight(600);
        gallery.setOrientation(LinearLayout.HORIZONTAL);
//        gallery.setOrientation(LinearLayout.VERTICAL);
        gallery.setSensibility(0.2f);
        // test nested
        LinearLayout.LayoutParams gParams = new LinearLayout.LayoutParams(-1, -1);
        gParams.setMargins(0, 60, 0, 0);
        LinearLayout c1 = new LinearLayout(this);
        LinearLayout c2 = new LinearLayout(this);
        c2.addView(c1);
        c1.addView(gallery, gParams);
        ListView listView = new DragListView(this);
        listView.addHeaderView(c2);

        SimpleArrayAdapter dataAdapter = new SimpleArrayAdapter<Integer>() {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = new SweetProgress(parent.getContext());
                }
                return convertView;
            }
        };
        for (int i = 0; i < 20; i++) {
            dataAdapter.addItem(i);
        }
        listView.setAdapter(dataAdapter);
        container.addView(listView);

        gallery.setOnItemSwitchListener(new SweetCircularView.OnItemSwitchListener() {
            @Override
            public void onItemSelected(int newDataIndex, int oldDataIndex) {
                logout("Listener: onItemSelected(" + newDataIndex + ", " + oldDataIndex + ")");
            }

            @Override
            public void onItemScrolled(int dataIndex, float offset) {
                logout("Listener: onItemScrolled(" + dataIndex + ", " + offset + ")");
            }
        });

        FrameLayout.LayoutParams left = new FrameLayout.LayoutParams(-2, -2, Gravity.LEFT | Gravity.CENTER_VERTICAL);
        Button leftBtn = new Button(this);
        leftBtn.setText("<");
        container.addView(leftBtn, left);

        FrameLayout.LayoutParams right = new FrameLayout.LayoutParams(-2, -2, Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        Button rightBtn = new Button(this);
        rightBtn.setText(">");
        container.addView(rightBtn, right);

        HorizontalScrollView scrollView = new HorizontalScrollView(this);
        LinearLayout funcContainer = new LinearLayout(this);
        funcContainer.setOrientation(LinearLayout.HORIZONTAL);
        Button func1 = new Button(this);
        func1.setText("add");
        Button func2 = new Button(this);
        func2.setText("remove");
        Button func3 = new Button(this);
        func3.setText("replace");
        Button func4 = new Button(this);
        func4.setText("reSize +2");
        Button func5 = new Button(this);
        func5.setText("indent");
        funcContainer.addView(func1);
        funcContainer.addView(func2);
        funcContainer.addView(func3);
        funcContainer.addView(func4);
        funcContainer.addView(func5);
        scrollView.addView(funcContainer);
        container.addView(scrollView, new FrameLayout.LayoutParams(-1, 100));

        func1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.addItem(android.R.color.holo_red_dark);
                adapter.addItem(android.R.color.holo_orange_dark);
                adapter.addItem(android.R.color.holo_blue_dark);
                adapter.addItem(android.R.color.holo_green_dark);
                adapter.notifyDataSetChanged();
            }
        });
        func2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.removeAll();
                adapter.notifyDataSetChanged();
            }
        });
        func3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.removeAll();
                adapter.addItem(android.R.color.darker_gray);
                adapter.addItem(android.R.color.holo_purple);
                adapter.notifyDataSetChanged();
            }
        });
        func4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = gallery.getRecycleItemSize();
                gallery.setRecycleItemSize(size + 2);
            }
        });
        func5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gallery.setSpaceBetweenItems(40);
                gallery.setIndent(200, 40, 200, 40);
            }
        });
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gallery.moveNext();
            }
        });
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gallery.movePrevious();
            }
        });

        // create pager warapper
        final ViewPager viewPager = new ViewPager(this);
        SimplePagerAdapter pagerAdapter = new SimplePagerAdapter();
        viewPager.setAdapter(pagerAdapter);
        Button simpleTestBtn = new Button(this);
        simpleTestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
            }
        });
        simpleTestBtn.setText("Just test for pager");
        pagerAdapter.add(simpleTestBtn);
        pagerAdapter.add(container);
        return viewPager;
    }

    @Override
    protected void doSomethingAfterCreated() {

    }

    private class GalleryAnimatorAdapter extends SweetCircularView.AnimatorAdapter {

        @Override
        public void onItemSelected(int newItemIndex, int oldItemIndex) {
            logout("Animator: onItemSelected(" + newItemIndex + ", " + oldItemIndex + ")");
        }

        @Override
        public void onItemScrolled(int itemIndex, float offset) {
            logout("Animator: onItemScrolled(" + itemIndex + ", " + offset + ")");
        }
    }

}
