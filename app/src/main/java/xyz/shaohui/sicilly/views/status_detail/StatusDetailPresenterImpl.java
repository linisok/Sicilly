package xyz.shaohui.sicilly.views.status_detail;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import xyz.shaohui.sicilly.data.models.Status;
import xyz.shaohui.sicilly.data.network.api.FavoriteAPI;
import xyz.shaohui.sicilly.data.network.api.StatusAPI;
import xyz.shaohui.sicilly.utils.ErrorUtils;
import xyz.shaohui.sicilly.views.feed.BaseFeedPresenter;
import xyz.shaohui.sicilly.views.feed.FeedMVP;

/**
 * Created by shaohui on 16/9/17.
 */

public class StatusDetailPresenterImpl extends BaseFeedPresenter<FeedMVP.View> {

    private final Status mOriginStatus;

    @Inject
    StatusDetailPresenterImpl(StatusAPI statusService, FavoriteAPI favoriteService, Status status) {
        super(favoriteService, statusService);
        mOriginStatus = status;
    }

    @Override
    protected Observable<List<Status>> loadStatus() {
        List<Status> origin = new ArrayList<>();
        origin.add(mOriginStatus);
        return Observable.concatDelayError(Observable.just(origin),
                mStatusService.context(mOriginStatus.id())
                        .filter(statuses -> statuses.size() > 1)
                        .onErrorResumeNext(
                                mStatusService.context(mOriginStatus.in_reply_to_status_id())));
    }

    @Override
    public void loadMessage() {
        loadStatus().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(statuses -> {
                    if (isViewAttached()) {
                        getView().showMessage(statuses);
                    }
                }, ErrorUtils::catchException);
    }

    @Override
    public Observable<List<Status>> loadMoreStatus(int page, Status lastStatus) {
        return Observable.empty();
    }
}
