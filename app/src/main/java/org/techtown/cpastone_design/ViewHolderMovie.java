package org.techtown.cpastone_design;

import android.animation.ValueAnimator;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolderMovie extends  RecyclerView.ViewHolder {

    TextView tv_movie_title, iv_mal;
    ImageView url_img,url_img2;
    LinearLayout linearlayout;

    OnViewHolderItemClickListener onViewHolderItemClickListener;


    public ViewHolderMovie(@NonNull View itemView) {
        super(itemView);

        url_img = itemView.findViewById(R.id.url_img);
        tv_movie_title = itemView.findViewById(R.id.tv_movie_title);
        iv_mal = itemView.findViewById(R.id.iv_mal);

        url_img2 = itemView.findViewById(R.id.url_img2);
        linearlayout = itemView.findViewById(R.id.linearlayout);

        linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onViewHolderItemClickListener.onViewHolderItemClick();
            }
        });
    }

    public void onBind(DataMovie data,int position, SparseBooleanArray selectedItems){
        tv_movie_title.setText(data.getTitle());
        iv_mal.setText(data.getMalicious());
        url_img.setImageBitmap(data.getImage());
        url_img2.setImageBitmap(data.getImage());
        changeVisibility(selectedItems.get(position));
    }

    private void changeVisibility(final boolean isExpanded) {
        // ValueAnimator.ofInt(int... values)는 View가 변할 값을 지정, 인자는 int 배열
        ValueAnimator va = isExpanded ? ValueAnimator.ofInt(0, 600) : ValueAnimator.ofInt(600, 0);
        // Animation이 실행되는 시간, n/1000초
        va.setDuration(500);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {


                // imageView의 높이 변경
                url_img2.getLayoutParams().height = (int) animation.getAnimatedValue();
                url_img2.requestLayout();
                // imageView가 실제로 사라지게하는 부분
                url_img2.setVisibility(isExpanded ? View.VISIBLE : View.GONE);


                iv_mal.getLayoutParams().height = (int) animation.getAnimatedValue();
                iv_mal.requestLayout();
                iv_mal.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            }
        });
        // Animation start
        va.start();
    }

    public void setOnViewHolderItemClickListener(OnViewHolderItemClickListener onViewHolderItemClickListener) {
        this.onViewHolderItemClickListener = onViewHolderItemClickListener;
    }
}
