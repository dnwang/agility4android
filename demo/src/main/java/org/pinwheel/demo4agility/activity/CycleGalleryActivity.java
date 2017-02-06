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

import org.pinwheel.agility.adapter.SimpleArrayAdapter;
import org.pinwheel.agility.adapter.SimplePagerAdapter;
import org.pinwheel.agility.animation.SimpleCircularAnimator;
import org.pinwheel.agility.util.BaseUtils;
import org.pinwheel.agility.view.SweetCircularView;
import org.pinwheel.agility.view.SweetIndicatorView;
import org.pinwheel.agility.view.drag.DragListView;
import org.pinwheel.demo4agility.R;
import org.pinwheel.demo4agility.test.ImageLoaderManager;


public class CycleGalleryActivity extends AbsTesterActivity {
    private static final String TAG = CycleGalleryActivity.class.getSimpleName();

    private final static String[] URLS = {
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

    private final SimpleArrayAdapter<Object> adapter = new SimpleArrayAdapter<Object>() {
        @Override
        public View getView(int i, View view, ViewGroup parent) {
            if (null == view) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_item, null);
            }
            TextView text = BaseUtils.getViewByHolder(view, R.id.text);
            ImageView image = BaseUtils.getViewByHolder(view, R.id.image);
            ImageLoaderManager.getInstance(parent.getContext()).setImageByImageLoader(image, URLS[i % URLS.length]);
            text.setText(String.valueOf(i));
            // 测试内外部onClickListener事件传递
            view.setOnClickListener(v -> logout(TAG, "onClick: [" + i + "]"));
            return view;
        }
    };

    @Override
    protected void beforeInitView() {

    }

    @Override
    protected View getContentView() {
        gallery = new SweetCircularView(this);
        initGallery(gallery);
        final LinearLayout funcContainer = new LinearLayout(this);
        initTestFunctionGroup(funcContainer);
        funcContainer.setOrientation(LinearLayout.HORIZONTAL);
        final HorizontalScrollView scrollView = new HorizontalScrollView(this);
        scrollView.addView(funcContainer);
        final ListView list = new DragListView(this);
        list.setAdapter(new SimpleArrayAdapter<Object>() {
            @Override
            public View getView(int i, View view, ViewGroup parent) {
                if (null == view) {
                    view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, null);
                }
                ((TextView) view).setText("测试竖向手势冲突");
                return view;
            }
        }.addAll(new Object[10]));
        final SweetIndicatorView indicatorView = new SweetIndicatorView(this);
        gallery.setIndicator(indicatorView);
        gallery.setMinimumHeight(600);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.addView(gallery);
        linearLayout.addView(indicatorView);
        list.addHeaderView(linearLayout);
        final FrameLayout content = new FrameLayout(this);
        content.addView(list, new LinearLayout.LayoutParams(-1, -1));
        content.addView(scrollView, new FrameLayout.LayoutParams(-1, -2, Gravity.TOP));
        final ViewPager viewPager = new ViewPager(this);
        final Button simpleTestBtn = new Button(this);
        simpleTestBtn.setText("测试横向手势冲突");
        simpleTestBtn.setOnClickListener(v -> viewPager.setCurrentItem(1));
        viewPager.setAdapter(new SimplePagerAdapter().add(simpleTestBtn).add(content));
        return viewPager;
    }

    private void initGallery(SweetCircularView gallery) {
        gallery.setAdapter(adapter)
//                .setClick2Selected(false)//点击切换
//                .setDurationOnInertial(1000)
//                .setDurationOnPacking(500)
//                .setOverRatio(0.2f)//越界系数
//                .setInertialRatio(0.01f)
                .setOnItemScrolledListener((v, dataIndex, offset) -> logout(TAG, "scrolled: [" + dataIndex + ", " + offset + "]"))
                .setOnItemSelectedListener((v, dataIndex) -> logout(TAG, "selected: [" + dataIndex + "]"));
    }

    private void initTestFunctionGroup(ViewGroup parent) {
        createFunctionBtn(parent, "置0", v -> gallery.setCurrentIndex(0));
        createFunctionBtn(parent, "<", v -> gallery.moveItems(-3));
        createFunctionBtn(parent, ">", v -> gallery.moveItems(3));
        createFunctionBtn(parent, "新增数据", v -> adapter.addAll(new Object[4]).notifyDataSetChanged());
        createFunctionBtn(parent, "清空数据", v -> adapter.removeAll().notifyDataSetChanged());
        createFunctionBtn(parent, "替换数据", v -> adapter.removeAll().addAll(new Object[2]).notifyDataSetChanged());
        createFunctionBtn(parent, "-间距", v -> gallery.setSpaceBetweenItems(gallery.getSpaceBetweenItems() - 20));
        createFunctionBtn(parent, "+间距", v -> gallery.setSpaceBetweenItems(gallery.getSpaceBetweenItems() + 20));
        createFunctionBtn(parent, "动画", v -> gallery.setAnimationAdapter(new SimpleCircularAnimator().setRotation(20)));
        createFunctionBtn(parent, "垂直", v -> gallery.setOrientation(LinearLayout.VERTICAL));
        createFunctionBtn(parent, "水平", v -> gallery.setOrientation(LinearLayout.HORIZONTAL));
        createFunctionBtn(parent, "+自动滑动", v -> gallery.setAutoCycle(true, true));
        createFunctionBtn(parent, "-自动滑动", v -> gallery.setAutoCycle(false, true));
        createFunctionBtn(parent, "点击切换", v -> gallery.setClick2Selected(true));
        createFunctionBtn(parent, "取消惯性", v -> gallery.setInertialRatio(0));
        createFunctionBtn(parent, "缩进", v -> gallery.setIndent(120, 120, 120, 10));
        createFunctionBtn(parent, "缩进x2", v -> gallery.setIndent(320, 220, 320, 220));
        createFunctionBtn(parent, "+2视图", v -> gallery.setRecycleItemSize(gallery.getRecycleItemSize() + 2));
        createFunctionBtn(parent, "-2视图", v -> gallery.setRecycleItemSize(gallery.getRecycleItemSize() - 2));
    }

    private void createFunctionBtn(ViewGroup parent, String txt, View.OnClickListener listener) {
        Button btn = new Button(this);
        btn.setText(txt);
        btn.setOnClickListener(listener);
        parent.addView(btn);
    }

    @Override
    protected void afterInitView() {

    }

}
