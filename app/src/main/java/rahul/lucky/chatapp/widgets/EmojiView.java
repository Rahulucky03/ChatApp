

package rahul.lucky.chatapp.widgets;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import rahul.lucky.chatapp.AndroidUtilities;
import rahul.lucky.chatapp.R;


public class EmojiView extends LinearLayout {
    private ArrayList<EmojiGridAdapter> adapters = new ArrayList<EmojiGridAdapter>();
    private int[] icons = {
            R.drawable.ic_emoji_recent,
            R.drawable.ic_emoji_smile,
            R.drawable.ic_emoji_flower,
            R.drawable.ic_emoji_bell,
            R.drawable.ic_emoji_car,
            R.drawable.ic_emoji_symbol };
    private Listener listener;
    private ViewPager pager;
    private FrameLayout recentsWrap;
    private ArrayList<GridView> views = new ArrayList<GridView>();

    public EmojiView(Context paramContext) {
        super(paramContext);
        init();
    }

    public EmojiView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        init();
    }

    public EmojiView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        init();
    }

    private void addToRecent(long paramLong) {
        if (this.pager.getCurrentItem() == 0) {
            return;
        }
        ArrayList<Long> localArrayList = new ArrayList<Long>();
        long[] currentRecent = Emoji.data[0];
        boolean was = false;
        for (long aCurrentRecent : currentRecent) {
            if (paramLong == aCurrentRecent) {
                localArrayList.add(0, paramLong);
                was = true;
            } else {
                localArrayList.add(aCurrentRecent);
            }
        }
        if (!was) {
            localArrayList.add(0, paramLong);
        }
        Emoji.data[0] = new long[Math.min(localArrayList.size(), 50)];
        for (int q = 0; q < Emoji.data[0].length; q++) {
            Emoji.data[0][q] = localArrayList.get(q);
        }
        adapters.get(0).data = Emoji.data[0];
        adapters.get(0).notifyDataSetChanged();
        saveRecents();
    }

    private String convert(long paramLong) {
        String str = "";
        for (int i = 0; ; i++) {
            if (i >= 4) {
                return str;
            }
            int j = (int)(0xFFFF & paramLong >> 16 * (3 - i));
            if (j != 0) {
                str = str + (char)j;
            }
        }
    }

    private void init() {
        setOrientation(LinearLayout.VERTICAL);
        for (int i = 0; i < Emoji.data.length; i++) {
            GridView gridView = new GridView(getContext());
            //  if (AndroidUtilities.isTablet()) {
            //     gridView.setColumnWidth(AndroidUtilities.dp(60));
            // } else {
            gridView.setColumnWidth(AndroidUtilities.dp(45));
            // }
            gridView.setNumColumns(-1);
            views.add(gridView);

            EmojiGridAdapter localEmojiGridAdapter = new EmojiGridAdapter(Emoji.data[i]);
            gridView.setAdapter(localEmojiGridAdapter);
            //  AndroidUtilities.setListViewEdgeEffectColor(gridView, 0xff999999);
            adapters.add(localEmojiGridAdapter);
        }

        setBackgroundColor(0xff222222);
        pager = new ViewPager(getContext());
        pager.setAdapter(new EmojiPagesAdapter());
        PagerSlidingTabStrip tabs = new PagerSlidingTabStrip(getContext());
        tabs.setViewPager(pager);
        tabs.setShouldExpand(true);
        tabs.setIndicatorColor(0xff33b5e5);
        tabs.setIndicatorHeight(AndroidUtilities.dp(2.0f));
        tabs.setUnderlineHeight(AndroidUtilities.dp(2.0f));
        tabs.setUnderlineColor(0x66000000);
        tabs.setTabBackground(0);
        LinearLayout localLinearLayout = new LinearLayout(getContext());
        localLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        localLinearLayout.addView(tabs, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f));
        ImageView localImageView = new ImageView(getContext());
        localImageView.setImageResource(R.drawable.ic_emoji_backspace);
        localImageView.setScaleType(ImageView.ScaleType.CENTER);
        localImageView.setBackgroundResource(R.drawable.bg_emoji_bs);
        localImageView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (EmojiView.this.listener != null) {
                    EmojiView.this.listener.onBackspace();
                }
            }
        });
        localLinearLayout.addView(localImageView, new LayoutParams(AndroidUtilities.dp(61), LayoutParams.MATCH_PARENT));
        recentsWrap = new FrameLayout(getContext());
        recentsWrap.addView(views.get(0));
        TextView localTextView = new TextView(getContext());
        localTextView.setText(getContext().getString(R.string.NoRecent));
        localTextView.setTextSize(18.0f);
        localTextView.setTextColor(-7829368);
        localTextView.setGravity(17);
        recentsWrap.addView(localTextView);
        views.get(0).setEmptyView(localTextView);
        addView(localLinearLayout, new LayoutParams(-1, AndroidUtilities.dp(48.0f)));
        addView(pager);
        loadRecents();
        if (Emoji.data[0] == null || Emoji.data[0].length == 0) {
            pager.setCurrentItem(1);
        }
    }

    private void saveRecents() {
        ArrayList<Long> localArrayList = new ArrayList<Long>();
        long[] arrayOfLong = Emoji.data[0];
        int i = arrayOfLong.length;
        for (int j = 0; ; j++) {
            if (j >= i) {
                getContext().getSharedPreferences("emoji", 0).edit().putString("recents", TextUtils.join(",", localArrayList)).commit();
                return;
            }
            localArrayList.add(arrayOfLong[j]);
        }
    }

    public void loadRecents() {
        String str = getContext().getSharedPreferences("emoji", 0).getString("recents", "");
        String[] arrayOfString = null;
        if ((str != null) && (str.length() > 0)) {
            arrayOfString = str.split(",");
            Emoji.data[0] = new long[arrayOfString.length];
        }
        if (arrayOfString != null) {
            for (int i = 0; i < arrayOfString.length; i++) {
                Emoji.data[0][i] = Long.parseLong(arrayOfString[i]);
            }
            adapters.get(0).data = Emoji.data[0];
            adapters.get(0).notifyDataSetChanged();
        }
    }

    public void onMeasure(int paramInt1, int paramInt2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(paramInt1), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(paramInt2), MeasureSpec.EXACTLY));
    }

    public void setListener(Listener paramListener) {
        this.listener = paramListener;
    }

    public void invalidateViews() {
        for (GridView gridView : views) {
            if (gridView != null) {
                gridView.invalidateViews();
            }
        }
    }

    private class EmojiGridAdapter extends BaseAdapter {
        long[] data;

        public EmojiGridAdapter(long[] arg2) {
            this.data = arg2;
        }

        public int getCount() {
            return data.length;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return data[i];
        }

        public View getView(int i, View view, ViewGroup paramViewGroup) {
            ImageView imageView = (ImageView)view;
            if (imageView == null) {
                imageView = new AppCompatImageView(EmojiView.this.getContext()) {
                    public void onMeasure(int paramAnonymousInt1, int paramAnonymousInt2) {
                        setMeasuredDimension(MeasureSpec.getSize(paramAnonymousInt1), MeasureSpec.getSize(paramAnonymousInt1));
                    }
                };
                imageView.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        if (EmojiView.this.listener != null) {
                            EmojiView.this.listener.onEmojiSelected(EmojiView.this.convert((Long)view.getTag()));
                        }
                        EmojiView.this.addToRecent((Long)view.getTag());
                    }
                });
                imageView.setBackgroundResource(R.drawable.list_selector);
                imageView.setScaleType(ImageView.ScaleType.CENTER);
            }
            imageView.setImageDrawable(Emoji.getEmojiBigDrawable(data[i]));
            imageView.setTag(data[i]);
            return imageView;
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            if (observer != null) {
                super.unregisterDataSetObserver(observer);
            }
        }
    }

    private class EmojiPagesAdapter extends PagerAdapter implements PagerSlidingTabStrip.IconTabProvider {

        public void destroyItem(ViewGroup paramViewGroup, int paramInt, Object paramObject) {
            View localObject;
            if (paramInt == 0) {
                localObject = recentsWrap;
            } else {
                localObject = views.get(paramInt);
            }
            paramViewGroup.removeView(localObject);
        }

        public int getCount() {
            return views.size();
        }

        public int getPageIconResId(int paramInt) {
            return icons[paramInt];
        }

        public Object instantiateItem(ViewGroup paramViewGroup, int paramInt) {
            View localObject;
            if (paramInt == 0) {
                localObject = recentsWrap;
            } else {
                localObject = views.get(paramInt);
            }
            paramViewGroup.addView(localObject);
            return localObject;
        }

        public boolean isViewFromObject(View paramView, Object paramObject) {
            return paramView == paramObject;
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            if (observer != null) {
                super.unregisterDataSetObserver(observer);
            }
        }
    }

    public static abstract interface Listener {
        public abstract void onBackspace();
        public abstract void onEmojiSelected(String paramString);
    }
}