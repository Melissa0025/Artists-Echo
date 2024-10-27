package com.example.artgallery.Adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.artgallery.Model.Post;
import com.example.artgallery.R;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;

    public PostAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(posts.get(position));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUsername;
        private TextView tvDescription;
        private ImageView ivPost;
        private ImageView ivProfileImage;
        private TextView tvRelativeTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivPost = itemView.findViewById(R.id.ivPost);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvRelativeTime = itemView.findViewById(R.id.tvRelativeTime);
        }

        public void bind(Post post) {
            String username = post.getUser() != null ? post.getUser().getUsername() : "Unknown User"; // Added null check
            tvUsername.setText(username);
            tvDescription.setText(post.getDescription());
            Glide.with(context).load(post.getImageUrl()).into(ivPost);
            Glide.with(context).load(getProfileImageUrl(username)).into(ivProfileImage);
            tvRelativeTime.setText(DateUtils.getRelativeTimeSpanString(post.getCreationTimeMs()));
        }

        private String getProfileImageUrl(String username) {
            try {
                MessageDigest digest = MessageDigest.getInstance("MD5");
                byte[] hash = digest.digest(username.getBytes());
                BigInteger bigInt = new BigInteger(1, hash);
                String hex = bigInt.toString(16);
                while (hex.length() < 32) {
                    hex = "0" + hex;
                }
                return "https://www.gravatar.com/avatar/" + hex + "?d=identicon";
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
    }
}
