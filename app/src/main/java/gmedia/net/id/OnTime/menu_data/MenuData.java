package gmedia.net.id.OnTime.menu_data;

import android.content.res.Resources;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;

import gmedia.net.id.OnTime.R;
import gmedia.net.id.OnTime.menu_data.menu_absensi.Fragment_absensi;
import gmedia.net.id.OnTime.menu_data.menu_jadwal_kerja.Fragment_jadwal_kerja;
import gmedia.net.id.OnTime.menu_data.menu_lembur.Fragment_lembur;

public class MenuData extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabLayoutViewPagerAdapterData adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Data Absensi");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(0);
        initUI();
        initAction();
    }

    private void initUI() {
        tabLayout = (TabLayout) findViewById(R.id.tabData);
        viewPager = (ViewPager) findViewById(R.id.viewPagerData);
    }

    private void initAction() {
        setupViewPager(viewPager);
//        tabLayout.setTabTextColors(Color.parseColor("#afafaf"),Color.parseColor("#FFFFFF"));
        tabLayout.setupWithViewPager(viewPager);
        final TextView tv = (TextView) LayoutInflater.from(MenuData.this).inflate(R.layout.custom_tab, null);
        //custom text size tab tablayout
        /*for (int i = 0; i < tabLayout.getTabCount(); i++) {
            int position = tabLayout.getTabAt(i).getPosition();
            if (i == 0) {
                //noinspection ConstantConditions
                TextView tv = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
                tv.setTextSize(15);
                tabLayout.getTabAt(i).setCustomView(tv);
            } else if (i == position) {
                //noinspection ConstantConditions
                TextView tv = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
                tv.setTextSize(16);
                tv.setTextColor(Color.parseColor("#FFFFFF"));
                tabLayout.getTabAt(i).setCustomView(tv);
            } else {
                TextView tv = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
                tv.setTextSize(16);
                tv.setTextColor(Color.parseColor("#000000"));
                tabLayout.getTabAt(i).setCustomView(tv);
            }

        }*/
        /*for (int i = 0; i < tabLayout.getTabCount(); i++) {
            int position = tabLayout.getTabAt(i).getPosition();
            if (i == position) {
                TextView tv = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
                tv.setTextColor(Color.parseColor("#FFFFFF"));
                tabLayout.getTabAt(i).setCustomView(tv);
            } else {
                TextView tv = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
                tv.setTextColor(Color.parseColor("#afafaf"));
                tabLayout.getTabAt(i).setCustomView(tv);
            }
        }*/
        //custom margin tab tablayout
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            if (i == 0) {
                View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
                p.setMargins(80, 0, 0, 0);
                tab.requestLayout();
            } else if (i == 1) {
                View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
                p.setMargins(0, 0, 0, 0);
                tab.requestLayout();
            } else {
                View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
                p.setMargins(0, 0, 80, 0);
                tab.requestLayout();
            }

        }


        /*viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });*/

        //call function custom margin global tablayout
        /*tabLayout.post(new Runnable() {
            @Override
            public void run() {
                setIndicator(tabLayout, 30, 30);
            }
        });*/
    }

    private void setupViewPager(final ViewPager viewPager) {
        adapter = new TabLayoutViewPagerAdapterData(getSupportFragmentManager());
        adapter.addFrag(new Fragment_jadwal_kerja());
        adapter.addFrag(new Fragment_absensi());
        adapter.addFrag(new Fragment_lembur());
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    //custom margin global tablayout
    private void setIndicator(TabLayout tabs, int leftDip, int rightDip) {
        Class<?> tabLayout = tabs.getClass();
        Field tabStrip = null;
        try {
            tabStrip = tabLayout.getDeclaredField("mTabStrip");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        tabStrip.setAccessible(true);
        LinearLayout llTab = null;
        try {
            llTab = (LinearLayout) tabStrip.get(tabs);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        int left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftDip, Resources.getSystem().getDisplayMetrics());
        int right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightDip, Resources.getSystem().getDisplayMetrics());

        for (int i = 0; i < llTab.getChildCount(); i++) {
            View child = llTab.getChildAt(i);
            child.setPadding(0, 0, 0, 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            params.leftMargin = left;
            params.rightMargin = right;
            child.setLayoutParams(params);
            child.invalidate();
        }
    }
}
