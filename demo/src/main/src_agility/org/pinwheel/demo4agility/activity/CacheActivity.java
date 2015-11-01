package org.pinwheel.demo4agility.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import org.pinwheel.agility.adapter.SimpleArrayAdapter;
import org.pinwheel.agility.cache.CacheLoader;
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
            "http://p4.gexing.com/G1/M00/24/0A/rBACFFOAA7ajGe7IAAC4Zaxq.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFFOAA7ajGe7IAAC4ZaxqBB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFFOAA7ajGe7IAAC4ZaqBB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFFOAA7ajGe7IAA4ZaxqBB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFFOAA7ajGe7IAC4ZaxqBB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFFOAA7ajGeIAAC4ZaxqBB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFFOAA7aje7IAAC4ZaxqBB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFFOAA7jGe7IAAC4ZaxqBB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFFOA7ajGe7IAAC4ZaxqBB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFFAA7ajGe7IAAC4ZaxqBB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFOAA7ajGe7IAAC4ZaxqBB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBCFFOAA7ajGe7IAAC4ZaxqBB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/BACFFOAA7ajGe7IAAC4ZaxqBB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0/rBACFFOAA7ajGe7IAAC4ZaxqBB8605.jpg",
            "http://p4.gexing.com/G1/M00/240A/rBACFFOAA7ajGe7IAAC4ZaxqBB8605.jpg",
            "http://p4.gexing.com/G1/M0/4/0A/rBACFFOAA7ajGe7IAAC4ZaxqBB8605.jpg",
            "http://p4.gexing.com/G1/00/24/0A/rBACFFOAA7ajGe7IAAC4ZaxqBB8605.jpg",
            "http://p4.gexing.com/G/M00/24/0A/rBACFFOAA7ajGe7IAAC4ZaxqBB8605.jpg",
            "http://p4.gexing.com/G/M00/24/0A/rBACFFOAA7ajGe7IAAC4ZaxqBB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFFOAA7ajGe7IAAC4ZaxqBB.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFFOAA7ajGe7IAAC48605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFFOAA7ajGe7IA4ZaxqBB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFFOAA7ajGe7IAAqBB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFFOAA7ae7IAAC4ZaxqBB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFFA7ajGe7IAAC4ZaxqBB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFajGe7IAAC4ZaxqBB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFFOAAGe7IAAC4ZaxqBB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACF7ajGe7IAAC4ZaxqBB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFFOAA7Ge7IAAC4ZaxqBB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFA7ajGe7IAAC4ZaxqBB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFFOA7IAAC4ZaxqBB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBAA7ajGe7IAAC4ZaxqBB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFA7ajGe7IAAC4ZaxqBB8605.jpg",
            "http://p4.gexing.com/G1/M00/2ACFFOAA7ajGe7IAAC4ZaxqBB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACA7ajGe7IAAC4ZaxqBB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0ACFFOAA7ajGe7IAAC4ZaxqBB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFFOAA7e7IAAC4ZaxqBB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFFOAA7ajGe7IAAZaxqBB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFFOAA7ajGIAC4ZaxqBB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFFOAA7ajIAAC4ZaxqBB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFF7ajGe7IAAC4Zaxq8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBFOAA7ajGAAC4ZaxqBB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFFOAA7ae7IAAC4ZaB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFFOAA7ajGe7IC4ZaxqBB5.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFFOAA7ajGe7IAACZaxqBB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFFOA7ajGeIAAC4ZaxBB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFOA7ajGeAAC4xqBB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFOAA7ajeAC4Zaxq8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFFOAAjIACxqBB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFFOAxqBB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFFOA7AC4axqBB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFFOAA7jGe7IAC4Z.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/FFOajGe7IAAC4ZaB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBFOAA7ajG7IA4Zaxq605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFFOGZxqBB805.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFFOAA7ajGIAAC4ZxqB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFFOAA7ajGeAAC4axqB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFFOAA7aj7IAAC4Zaxq8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFFOAA7Ge7IAAC4ZaB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBAOAAjGe7IAAC4ZaxBB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/rBACFFA7aj7I4ZaxqB8605.jpg",
            "http://p4.gexing.com/G1/M00/24/0A/OAA7jGe7IAC4ZaBB8605.jpg",
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
    };

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
        gridView.setNumColumns(6);
        gridView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        gridView.setVerticalScrollBarEnabled(false);
        return gridView;
    }

    @Override
    protected void doTest() {
        for (int i = 0; i < 10; i++) {
            for (String url : urls) {
                adapter.addItem(url);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private class Adapter extends SimpleArrayAdapter<String> {

        private int viewCount;

        private Context context;
        private LayoutInflater inflater;

        public Adapter(Context context) {
            //Volley init
            BitmapLoader.init(context);
            //ImageLoader init
            DisplayImageOptions options;
            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.holo_btn_av_download) //设置图片在下载期间显示的图片
                    .showImageForEmptyUri(R.drawable.holo_btn_alerts_and_states_error)//设置图片Uri为空或是错误的时候显示的图片
                    .showImageOnFail(R.drawable.holo_btn_av_download)  //设置图片加载/解码过程中错误时候显示的图片
                    .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                    .cacheOnDisc(true)//设置下载的图片是否缓存在SD卡中
                    .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
                    .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
                    .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
                    .displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少
                    .displayer(new FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间
                    .build();//构建完成

            ImageLoaderConfiguration config = new ImageLoaderConfiguration
                    .Builder(context)
                    .defaultDisplayImageOptions(options)
                    .memoryCacheExtraOptions(480, 480) // max width, max height，即保存的每个缓存文件的最大长宽
                    .threadPoolSize(6)//线程池内加载的数量
                    .threadPriority(Thread.NORM_PRIORITY - 2)
                    .denyCacheImageMultipleSizesInMemory()
                    .memoryCacheSize(CacheLoader.DEFAULT_MAX_MEMORY_CACHE)
                    .discCacheSize(CacheLoader.DEFAULT_MAX_DISK_CACHE)
                    .discCacheFileNameGenerator(new Md5FileNameGenerator())//将保存的时候的URI名称用MD5 加密
                    .imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
                    .writeDebugLogs() // Remove for release app
                    .build();//开始构建
            ImageLoader.getInstance().init(config);
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
            desc.setText("View:" + convertView.getId() + "\n" +
                    "IMG:" + String.valueOf(position % urls.length));

            // By Agility
            ImageView imageView = BaseUtils.getViewByHolder(convertView, R.id.image);
            ImageLoaderManager.getImageLoader(context).setImage(imageView, getItem(position));

            // By ImageLoader
//            ImageLoader.getInstance().displayImage(getItem(position), imageView);

            // By Volley
//            NetworkImageView imageView = BaseUtils.getViewByHolder(convertView, R.id.image);
//            BitmapLoader.getInstance().setImageFromNetwork(imageView, getItem(position));

            return convertView;
        }
    }

}
