package org.pinwheel.demo4agility.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.pinwheel.agility.adapter.SimpleArrayAdapter;
import org.pinwheel.agility.util.BaseUtils;
import org.pinwheel.agility.util.BitmapLoader;
import org.pinwheel.demo4agility.R;
import org.pinwheel.demo4agility.test.ImageLoaderManager;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class CacheActivity extends AbsTestActivity {

    private String[] urls = {
            "http://p4.gexing.com/G1/M00/24/0A/rBACFFOAA7ajGe7IAAC4ZaxqBB8605.jpg",
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
//            "http://img.kejixun.com/2014/0604/20140604052120926.jpg",
//            "http://attachment.huaxi100.com/data/attachment/forum/201303/24/115621yb7ibzqh33m0c3fq.jpg",
    };

//    private String[] urls = {
//            "http://img.sootuu.com/vector/2006-4/2006420114951558.jpg",//0
//            "http://img.sootuu.com/vector/200801/097/074.jpg",//1
//            "http://img.sootuu.com/vector/200801/097/075.jpg",//2
//            "http://img.sootuu.com/vector/200801/097/076.jpg",//3
//            "http://img.sootuu.com/vector/200801/097/077.jpg",//4
//            "http://img.sootuu.com/vector/200801/097/078.jpg",//5
//            "http://img.sootuu.com/vector/200801/097/079.jpg",//6
//            "http://www.cnr.cn/newscenter/gjxw/list/201308/W020130807387031641496.jpg",//7
//            "http://www.dlxww.com/newscenter/content/images/attachement/jpg/site2/20120714/051342201886015_change_SB05B714Cb001.jpg",//8
//            "http://pic8.nipic.com/20100623/636809_140233068651_2.jpg",//9
//            "http://www.bodaoedu.com/10th/show/10logo.jpg",//10
//            "http://d.hiphotos.baidu.com/baike/w%3D268/sign=2f38872c810a19d8cb0383030bfb82c9/fd039245d688d43f64134f6f7c1ed21b0ff43bf1.jpg",//11
//            "http://hiphotos.baidu.com/zhidao/pic/item/64380cd7c47c895aa144df82.jpg",//12
//            "http://p7.qhimg.com/dr/250_500_/t0124169fb4230d69f7.jpg",//13
//            "http://pic.baike.soso.com/p/20140221/20140221050206-2084250523.jpg",//14
////            "http://i2.cqnews.net/cq/attachement/jpg/site82/20120218/c417feb1eac810a9d11107.jpg",//15
//    };

    private Adapter adapter;

    @Override
    protected void onInitInCreate() {
        BitmapLoader.init(this);
    }

    @Override
    protected View getContentView() {
        adapter = new Adapter(CacheActivity.this);
        GridView gridView = new GridView(this);
        gridView.setAdapter(adapter);
        gridView.setNumColumns(3);
        gridView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        gridView.setVerticalScrollBarEnabled(false);
        return gridView;
    }

    @Override
    protected void doTest() {
        for (String url : urls) {
            adapter.addItem(url);
        }
        for (String url : urls) {
            adapter.addItem(url);
        }
        for (String url : urls) {
            adapter.addItem(url);
        }
        for (String url : urls) {
            adapter.addItem(url);
        }
        adapter.notifyDataSetChanged();
    }

    private class Adapter extends SimpleArrayAdapter<String> {

        private int viewCount;

        private Context context;
        private LayoutInflater inflater;

        public Adapter(Context context) {
            this.context = context;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_image, null);
                convertView.setId(viewCount++);
            }
            TextView desc = BaseUtils.getViewByHolder(convertView, R.id.desc);
            ImageView imageView = BaseUtils.getViewByHolder(convertView, R.id.image);

            desc.setText("View:" + convertView.getId() + "\n" +
                    "IMG:" + String.valueOf(position % urls.length));

            ImageLoaderManager.getImageLoader(context).setImageByScaleType(imageView, getItem(position));

            return convertView;
        }
    }

}
