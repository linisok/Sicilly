package xyz.shaohui.sicilly.views.user_info.star;

import android.view.View;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import me.shaohui.scrollablelayout.ScrollableHelper;
import me.shaohui.vistarecyclerview.decoration.SpacingDecoration;
import xyz.shaohui.sicilly.R;
import xyz.shaohui.sicilly.views.feed.BaseFeedFragment;
import xyz.shaohui.sicilly.views.feed.FeedMVP;
import xyz.shaohui.sicilly.views.feed.adapter.SimpleFeedAdapter;
import xyz.shaohui.sicilly.views.user_info.di.UserInfoComponent;

/**
 * Created by shaohui on 2017/1/11.
 */

public class UserStarFragment extends BaseFeedFragment<FeedMVP.View, UserStarPresenterImpl>
        implements FeedMVP.View, ScrollableHelper.ScrollableContainer {

    private int mPage = 1;

    private final static int PRE_LOAD = 6;

    @Override
    public void injectDependencies() {
        UserInfoComponent component = getComponent(UserInfoComponent.class);
        component.inject(this);
        presenter = component.userStarPresenter();
    }

    @Override
    public int layoutRes() {
        return R.layout.fragment_user_timeline;
    }

    @Override
    public void bindViews(View view) {
        SimpleFeedAdapter adapter = new SimpleFeedAdapter(mStatusList, this, getFragmentManager());
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addItemDecoration(new SpacingDecoration(8));
        mRecyclerView.setOnMoreListener((total, left, current) -> {
            if (mStatusList.size() > 0) {
                presenter.loadMoreMessage(++mPage, mStatusList.get(mStatusList.size() - 1));
            }
        }, PRE_LOAD);

        // 加载数据
        presenter.loadMessage();
    }


    @Override
    public View getScrollableView() {
        if (mRecyclerView != null) {
            return mRecyclerView.getRecycler();
        }
        return null;
    }

}