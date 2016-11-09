package mario.android.tvseriesmvp;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import mario.android.tvseriesmvp.model.show.TVShow;

/**
 * Created by Mario on 13.10.2016.
 */
public class ShowsAdapter extends RecyclerView.Adapter<ShowsAdapter.ShowsViewHolder> {

    private List<TVShow> mTVShowList;
    private Callback mCallback;
    private LikeCallback mLikeCallback;


    public ShowsAdapter(){
        this.mTVShowList = Collections.emptyList();
    }

    public void setShowsList(List<TVShow> showsList){
        this.mTVShowList = showsList;
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    public void setLikeCallback(LikeCallback likeCallback) {
        mLikeCallback = likeCallback;
    }

    public void setShowLike(int position, boolean like){
        mTVShowList.get(position).getShow().setLiked(like);
    }

    @Override
    public ShowsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final  View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.show_row, parent, false);

        final ShowsViewHolder viewHolder = new ShowsViewHolder(itemView);
        viewHolder.contentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onItemClick(viewHolder.mTVShow);
                }
            }
        });

        viewHolder.likeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLikeCallback != null){
                    final int position = viewHolder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        mLikeCallback.onLikeClick(viewHolder.mTVShow, position);
                    }
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ShowsViewHolder holder, int position) {
        TVShow tvShow = mTVShowList.get(position);
        Context context = holder.titleTextView.getContext();
        holder.mTVShow = tvShow;

        if (tvShow.getShow().getImage() != null){
            Picasso.with(context).load(tvShow.getShow().getImage().getMedium())
                    .placeholder(R.drawable.placeholder)
                    .fit()
                    .into(holder.showImageView);
        }else {
            holder.showImageView.setImageResource(R.drawable.placeholder);
        }


        holder.titleTextView.setText(tvShow.getShow().getName());
        holder.statusTextView.setText(tvShow.getShow().getStatus());

        if (tvShow.getShow().isLiked() != false){
            //holder.likeImageView.setColorFilter(Color.argb(55, 55, 55, 55));
            int color = Color.parseColor("#FF0000"); //The color u want
            holder.likeImageView.setColorFilter(color);
        }else{
            holder.likeImageView.setColorFilter(null);
        }
    }

    @Override
    public int getItemCount() {
        return mTVShowList.size();
    }




    public static class ShowsViewHolder extends RecyclerView.ViewHolder {

        public View contentLayout;
        public ImageView showImageView;
        public TextView titleTextView;
        public TextView statusTextView;
        public ImageView likeImageView;


        public TVShow mTVShow;

        public ShowsViewHolder(View itemView) {
            super(itemView);
            contentLayout = itemView.findViewById(R.id.layout_content);
            showImageView = (ImageView) itemView.findViewById(R.id.show_image);
            titleTextView = (TextView) itemView.findViewById(R.id.text_series_title);
            statusTextView = (TextView) itemView.findViewById(R.id.text_series_status);
            likeImageView = (ImageView) itemView.findViewById(R.id.show_like);
        }
    }


    public interface Callback {
        void onItemClick(TVShow tvShow);
    }

    public interface LikeCallback{
        void onLikeClick(TVShow tvShow, int position);
    }
}
