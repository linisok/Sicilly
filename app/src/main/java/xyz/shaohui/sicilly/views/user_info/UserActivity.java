package xyz.shaohui.sicilly.views.user_info;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import me.shaohui.scrollablelayout.ScrollableHelper;
import me.shaohui.scrollablelayout.ScrollableLayout;
import me.shaohui.sicillylib.utils.ToastUtils;
import org.greenrobot.eventbus.EventBus;
import xyz.shaohui.sicilly.R;
import xyz.shaohui.sicilly.base.BaseMvpActivity;
import xyz.shaohui.sicilly.base.HasComponent;
import xyz.shaohui.sicilly.data.models.User;
import xyz.shaohui.sicilly.utils.HtmlUtils;
import xyz.shaohui.sicilly.views.user_info.di.DaggerUserInfoComponent;
import xyz.shaohui.sicilly.views.user_info.di.UserInfoComponent;
import xyz.shaohui.sicilly.views.user_info.mvp.UserInfoPresenter;
import xyz.shaohui.sicilly.views.user_info.mvp.UserInfoView;
import xyz.shaohui.sicilly.views.user_info.photo.UserPhotoFragmentBuilder;
import xyz.shaohui.sicilly.views.user_info.timeline.UserTimelineFragmentBuilder;

public class UserActivity extends BaseMvpActivity<UserInfoView, UserInfoPresenter>
        implements UserInfoView, HasComponent<UserInfoComponent> {

    @BindView(R.id.count_follow)
    TextView countFollow;
    @BindView(R.id.count_follower)
    TextView countFollower;
    @BindView(R.id.count_status)
    TextView countStatus;
    @BindView(R.id.user_name)
    TextView name;
    @BindView(R.id.user_avatar)
    CircleImageView avatar;
    @BindView(R.id.user_location)
    TextView location;
    @BindView(R.id.user_brief)
    TextView brief;
    @BindView(R.id.user_bg)
    ImageView userBackground;

    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.btn_follow)
    ImageButton actionFollow;
    @BindView(R.id.tab_layout)
    SegmentTabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.scrollableLayout)
    ScrollableLayout scrollableLayout;

    private User mUser;
    private String userId;
    private List<Fragment> fragmentList;
    private UserInfoComponent mComponent;

    @Inject
    EventBus mBus;

    public static Intent newIntent(Context context, String userId) {
        Intent intent = new Intent(context, UserActivity.class);
        intent.putExtra("user_id", userId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        if (savedInstanceState == null) {
            if (getIntent().getData() != null) {
                userId = HtmlUtils.cleanUserScheme(getIntent().getData());
            } else {
                userId = getIntent().getStringExtra("user_id");
            }
        } else {
            userId = savedInstanceState.getString("user_id");
        }
        ButterKnife.bind(this);
        fragmentList = new ArrayList<>();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putString("user_id", userId);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @NonNull
    @Override
    public EventBus getBus() {
        return mBus;
    }

    @Override
    public void injectDependencies() {
        mComponent = DaggerUserInfoComponent.builder().appComponent(getAppComponent()).build();
        mComponent.inject(this);
        presenter = mComponent.userInfoPresenter();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        presenter.fetchUserInfo(userId);
    }

    private void initViewPager() {

        UserPagerAdapter adapter = new UserPagerAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                    int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                scrollableLayout.getHelper()
                        .setScrollableContainer(
                                (ScrollableHelper.ScrollableContainer) fragmentList.get(position));
                tabLayout.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.setTabData(new String[] {
                getString(R.string.user_tab_1), getString(R.string.user_tab_2)
        });
        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }

    private void initScrollableLayout() {
        scrollableLayout.getHelper()
                .setScrollableContainer((ScrollableHelper.ScrollableContainer) fragmentList.get(0));
        scrollableLayout.setOnScrollListener((currentY, maxY) -> {
            if (currentY >= maxY) {
                titleBar.setBackgroundColor(getResources().getColor(R.color.positive));
            } else {
                titleBar.setBackgroundColor(getResources().getColor(R.color.transparent));
            }
        });
    }

    @Override
    public void placeUserInfo(User user, boolean isProtected) {
        mUser = user;
        countFollow.setText(String.valueOf(user.friends_count()));
        countFollower.setText(String.valueOf(user.followers_count()));
        countStatus.setText(String.valueOf(user.statuses_count()));

        name.setText(user.screen_name());
        if (!TextUtils.isEmpty(user.location())) {
            location.setText(user.location());
        }
        brief.setText(user.description());
        Glide.with(this).load(user.profile_image_url_large()).into(avatar);
        Glide.with(this).load(user.profile_background_image_url()).into(userBackground);

        if (mUser.following()) {
            actionFollow.setImageResource(R.drawable.ic_followed);
        }

        // 针对设置隐私保护的user
        if (!mUser.following() && mUser.is_protected()) {
            showPrivacyFragment();
        } else {
            showSimpleFragment();
        }
        initViewPager();
        initScrollableLayout();
    }

    private void showPrivacyFragment() {
        fragmentList.add(new PrivacyFragment());
        fragmentList.add(new PrivacyFragment());
    }

    private void showSimpleFragment() {
        fragmentList.add(UserTimelineFragmentBuilder.newUserTimelineFragment(userId));
        fragmentList.add(UserPhotoFragmentBuilder.newUserPhotoFragment(userId));
    }

    @Override
    public void loadUserInfoFailure() {
        ToastUtils.showToast(this, R.string.load_user_failure);
        finish();
    }

    @Override
    public void opFollow() {
        presenter.opFollow(mUser);

        // 用户没设置隐私, 直接显示关注成功
        if (!mUser.is_protected()) {
            mUser = mUser.updateFollow();
            actionFollow.setImageResource(R.drawable.ic_followed);
        }
    }

    @Override
    public void opRequestFollow() {

    }

    @Override
    public void showUnFollowConfirmDialog() {
        new MaterialDialog.Builder(this)
                .content(R.string.confirm_un_follow_message)
                .positiveText(R.string.yes)
                .negativeText(R.string.no)
                .onPositive((dialog, which) -> presenter.opUnFollow(mUser));

        mUser = mUser.updateFollow();
        actionFollow.setImageResource(R.drawable.ic_follow);
    }

    @Override
    public void followError() {
        ToastUtils.showToast(this, R.string.follow_error);
        mUser = mUser.updateFollow();
        actionFollow.setImageResource(R.drawable.ic_follow);
    }

    @Override
    public void unFollowError() {
        ToastUtils.showToast(this, R.string.follow_error);
        mUser = mUser.updateFollow();
        actionFollow.setImageResource(R.drawable.ic_followed);
    }

    @OnClick(R.id.btn_back)
    void btnBack() {
        finish();
    }

    @OnClick(R.id.btn_follow)
    void actionFollow() {
        presenter.opFollow(mUser);
    }

    @Override
    public UserInfoComponent getComponent() {
        return mComponent;
    }

    class UserPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragmentList;

        public UserPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
