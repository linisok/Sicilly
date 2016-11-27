package xyz.shaohui.sicilly.views.feed.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import java.util.List;
import me.shaohui.sicillylib.utils.ToastUtils;
import xyz.shaohui.sicilly.R;
import xyz.shaohui.sicilly.SicillyApplication;
import xyz.shaohui.sicilly.data.models.Status;
import xyz.shaohui.sicilly.data.models.User;
import xyz.shaohui.sicilly.utils.HtmlUtils;
import xyz.shaohui.sicilly.utils.NoUnderlineSpan;
import xyz.shaohui.sicilly.utils.SimpleUtils;
import xyz.shaohui.sicilly.utils.TimeUtils;
import xyz.shaohui.sicilly.views.feed.FeedItemListener;
import xyz.shaohui.sicilly.views.home.timeline.TimelineItemListener;
import xyz.shaohui.sicilly.views.photo.PictureActivity;
import xyz.shaohui.sicilly.views.status_detail.StatusDetailActivity;
import xyz.shaohui.sicilly.views.user_info.UserActivity;

/**
 * Created by shaohui on 2016/11/27.
 */

public class SimpleFeedAdapter
        extends BaseFeedAdapter<SimpleFeedAdapter.SimpleStatusViewHolder> {

    private FeedItemListener mListener;

    public SimpleFeedAdapter(List<Status> dataList, FeedItemListener listener,
            FragmentManager fragmentManager) {
        super(dataList, fragmentManager);
        mListener = listener;
    }

    @Override
    public SimpleStatusViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_status_index, parent, false);
        return new SimpleStatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleStatusViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        final Status status = dataList.get(position);
        final User user = status.user();
        final Context context = holder.itemView.getContext();

        holder.name.setText(user.screen_name());
        holder.createdTime.setText(TimeUtils.simpleFormat(status.created_at()));
        holder.source.setText(HtmlUtils.cleanAllTag(status.source()));

        // text
        holder.text.setText(Html.fromHtml(HtmlUtils.switchTag(status.text())));
        Spannable s = new SpannableString(holder.text.getText());
        s.setSpan(new NoUnderlineSpan(), 0, s.length(), Spanned.SPAN_MARK_MARK);
        holder.text.setText(s);
        holder.text.setMovementMethod(LinkMovementMethod.getInstance());
        holder.text.setOnLongClickListener(v -> {
            SimpleUtils.copyText(context, HtmlUtils.cleanAllTag(status.text()));
            ToastUtils.showToast(context, R.string.copy_text_tip);
            return true;
        });

        Glide.with(context).load(user.profile_image_url_large()).into(holder.avatar);
        if (status.photo() != null) {
            holder.image.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(status.photo().getLargeurl())
                    .asBitmap()
                    .placeholder(
                            context.getResources().getDrawable(R.drawable.drawable_plcae_holder))
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(holder.image);
            holder.image.setOnClickListener(v -> {
                Intent intent = PictureActivity.newIntent(context, status.photo().getLargeurl());
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
                        && !status.photo().getLargeurl().toLowerCase().endsWith(".gif")) {
                    ActivityOptions options =
                            ActivityOptions.makeSceneTransitionAnimation((Activity) context,
                                    holder.image, "image");
                    context.startActivity(intent, options.toBundle());
                } else {
                    context.startActivity(intent);
                }
            });
            // gif
            if (status.photo().getLargeurl().toLowerCase().endsWith(".gif")) {
                holder.gif.setVisibility(View.VISIBLE);
            } else {
                holder.gif.setVisibility(View.GONE);
            }
        } else {
            holder.image.setVisibility(View.GONE);
            holder.gif.setVisibility(View.GONE);
        }

        // action

        if (SicillyApplication.isSelf(status.user().id())) {
            holder.actionDelete.setVisibility(View.VISIBLE);
        } else {
            holder.actionDelete.setVisibility(View.GONE);
        }

        // 是否显示上下文图标
        if (TextUtils.isEmpty(status.in_reply_to_status_id())) {
            holder.actionContxt.setVisibility(View.GONE);
        } else {
            holder.actionContxt.setVisibility(View.VISIBLE);
        }

        View.OnClickListener listener = v -> {
            switch (v.getId()) {
                case R.id.user_avatar:
                case R.id.user_name:
                    context.startActivity(UserActivity.newIntent(context, user.id()));
                    break;
                case R.id.action_comment:
                    replyStatus(status);
                    break;
                case R.id.action_repost:
                    repostStatus(status);
                    break;
                case R.id.action_star:
                    mListener.opStar(status, position);
                    break;
                case R.id.action_delete:
                    mListener.opDelete(status, position);
                    break;
                case R.id.action_context:
                    context.startActivity(StatusDetailActivity.newIntent(context, status));
                    break;
            }
        };
        holder.actionComment.setOnClickListener(listener);
        holder.actionRepost.setOnClickListener(listener);
        holder.actionStar.setOnClickListener(listener);
        holder.actionDelete.setOnClickListener(listener);
        holder.actionContxt.setOnClickListener(listener);
        holder.name.setOnClickListener(listener);
        holder.avatar.setOnClickListener(listener);
        holder.text.setOnClickListener(
                v -> context.startActivity(StatusDetailActivity.newIntent(context, status)));
        holder.itemView.setOnClickListener(
                v -> context.startActivity(StatusDetailActivity.newIntent(context, status)));
        //viewHolder.header.setOnClickListener(listener);

        if (status.favorited()) {
            holder.actionStar.setImageResource(R.drawable.ic_star_fill);
        } else {
            holder.actionStar.setImageResource(R.drawable.ic_star);
        }

        // 因为导致卡顿，所以暂时去掉首页的动画
        // 动画效果
        //if (holder.getAdapterPosition() > mLastPosition) {
        //    startAnimator(holder.itemView);
        //    mLastPosition = holder.getAdapterPosition();
        //} else {
        //    clearAnimator(holder.itemView);
        //}
    }

    class SimpleStatusViewHolder extends BaseFeedViewHolder {

        @BindView(R.id.user_avatar)
        ImageView avatar;
        @BindView(R.id.user_name)
        TextView name;
        @BindView(R.id.status_source)
        TextView source;
        @BindView(R.id.action_delete)
        ImageButton actionDelete;
        @BindView(R.id.action_context)
        ImageButton actionContxt;
        @BindView(R.id.action_comment)
        ImageButton actionComment;
        @BindView(R.id.action_repost)
        ImageButton actionRepost;
        @BindView(R.id.action_star)
        ImageButton actionStar;
        @BindView(R.id.status_header)
        RelativeLayout header;

        public SimpleStatusViewHolder(View itemView) {
            super(itemView);
        }
    }
}
