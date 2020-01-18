package cn.wildfire.chat.kit.search.viewHolder;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lr.sia.R;


public class CategoryViewHolder extends RecyclerView.ViewHolder {
   private TextView categoryTextView;

    public CategoryViewHolder(View itemView) {
        super(itemView);
        categoryTextView = itemView.findViewById(R.id.categoryTextView);
    }

    public void onBind(String category) {
        if (TextUtils.isEmpty(category)) {
            categoryTextView.setVisibility(View.GONE);
            return;
        }
        categoryTextView.setVisibility(View.VISIBLE);
        categoryTextView.setText(category);
    }
}
