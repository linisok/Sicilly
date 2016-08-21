package xyz.shaohui.sicilly.views.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.shaohui.sicilly.R;
import xyz.shaohui.sicilly.views.fragments.MessageFragment;
import xyz.shaohui.sicilly.views.fragments.TimelineFragment;
import xyz.shaohui.sicilly.views.fragments.UserFragment;

public class IndexActivity extends BaseActivity {

    @BindView(R.id.bottom_tab)CommonTabLayout bottomTab;

    public static final int INDEX_HOME = 0;
    public static final int INDEX_MESSAGE = 1;
    public static final int INDEX_USER = 2;

    public static final String TAG_HOME = "home";
    public static final String TAG_MESSAGE = "message";
    public static final String TAG_USER = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        ButterKnife.bind(this);

        initBottomTab();
    }

    private void initBottomTab() {
        ArrayList<CustomTabEntity> tabData = new ArrayList<>();
        tabData.add(new TabEntity(
                getString(R.string.bottom_tab_home),
                R.drawable.ic_home_selected,
                R.drawable.ic_home));
        tabData.add(new TabEntity(getString(R.string.bottom_tab_message),
                R.drawable.ic_message_selected,
                R.drawable.ic_message));
        tabData.add(new TabEntity(getString(R.string.bottom_tab_user),
                R.drawable.ic_user_selected,
                R.drawable.ic_user));
        bottomTab.setTabData(tabData);
    }

    // 切换Fragment
    private void switchFragment(int index) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        switch (index) {
            case INDEX_HOME:
                if (manager.findFragmentByTag(TAG_USER) != null) {
                    transaction.hide(manager.findFragmentByTag(TAG_USER));
                }

                if (manager.findFragmentByTag(TAG_MESSAGE) != null) {
                    transaction.hide(manager.findFragmentByTag(TAG_MESSAGE));
                }

                if (manager.findFragmentByTag(TAG_HOME) != null) {
                    transaction.show(manager.findFragmentByTag(TAG_HOME));
                } else {
                    transaction.add(R.id.main_frame,
                            TimelineFragment.newInstance(TimelineFragment.ACTION_INDEX), TAG_HOME);
                }
                break;
            case INDEX_MESSAGE:
                if (manager.findFragmentByTag(TAG_USER) != null) {
                    transaction.hide(manager.findFragmentByTag(TAG_USER));
                }

                if (manager.findFragmentByTag(TAG_HOME) != null) {
                    transaction.hide(manager.findFragmentByTag(TAG_HOME));
                }

                if (manager.findFragmentByTag(TAG_MESSAGE) != null) {
                    transaction.show(manager.findFragmentByTag(TAG_MESSAGE));
                } else {
                    transaction.add(R.id.main_frame,
                            new MessageFragment(), TAG_MESSAGE);
                }
                break;
            case INDEX_USER:
                if (manager.findFragmentByTag(TAG_HOME) != null) {
                    transaction.hide(manager.findFragmentByTag(TAG_HOME));
                }

                if (manager.findFragmentByTag(TAG_MESSAGE) != null) {
                    transaction.hide(manager.findFragmentByTag(TAG_MESSAGE));
                }

                if (manager.findFragmentByTag(TAG_USER) != null) {
                    transaction.show(manager.findFragmentByTag(TAG_USER));
                } else {
                    transaction.add(R.id.main_frame,
                            new UserFragment(), TAG_USER);
                }
                break;
        }
        transaction.commit();
    }

    class TabEntity implements CustomTabEntity {

        private String title;
        private int selected;
        private int unSelected;

        public TabEntity(String title, int selected, int unSelected) {
            this.title = title;
            this.selected = selected;
            this.unSelected = unSelected;
        }

        @Override
        public String getTabTitle() {
            return title;
        }

        @Override
        public int getTabSelectedIcon() {
            return selected;
        }

        @Override
        public int getTabUnselectedIcon() {
            return unSelected;
        }
    }


}
