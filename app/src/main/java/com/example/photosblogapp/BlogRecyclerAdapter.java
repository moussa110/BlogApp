package com.example.photosblogapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    List<BlogPost> blogPostList;
    Context context;
    FirebaseFirestore firestore;

    public BlogRecyclerAdapter(List<BlogPost> blogPostList){
        this.blogPostList=blogPostList;
    }

    @NonNull
    @Override
    public BlogRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item,parent,false);
        context=parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BlogRecyclerAdapter.ViewHolder holder, int position) {

        firestore=FirebaseFirestore.getInstance();

        String desc_data=blogPostList.get(position).getDesc();
        holder.setDescText(desc_data);

        String downImgUri=blogPostList.get(position).getImage_uri();
        holder.setPostImageView(downImgUri);

        String user_id=blogPostList.get(position).getUser_id();
        firestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){

                    String user_name=task.getResult().getString("name");
                    String user_image=task.getResult().getString("image");

                    holder.setBlogUserData(user_name,user_image);

                }else {
                   String msg= task.getException().getMessage();
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }

            }
        });


//        long millisecond = blogPostList.get(position).getTimes_tamb().getTime();
//       SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
//       String dateString = formatter.format(new Date(milliseconds));
//
//       holder.setTimeDateText(String.valueOf(millisecond));


    }

    @Override
    public int getItemCount() {
        return blogPostList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private TextView descView,timeDateView,blogUserNameView;
        private ImageView postImageView,blogUserImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mView=itemView;
        }

        public void setDescText(String descText){

            descView= mView.findViewById(R.id.blog_post_desc);
            descView.setText(descText);

        }

        public void setPostImageView(String downloadUri){
            postImageView=mView.findViewById(R.id.blog_post_image);
            Glide.with(context).load(downloadUri).into(postImageView);
        }

        public void setTimeDateText(String timeDateText){
            timeDateView=mView.findViewById(R.id.blog_date);
            timeDateView.setText(timeDateText);
        }

        public void setBlogUserData(String name,String image){
           blogUserNameView=mView.findViewById(R.id.blog_user_name);
            blogUserImageView=mView.findViewById(R.id.blog_user_image);

            blogUserNameView.setText(name);
            RequestOptions requestOptions=new RequestOptions();
            requestOptions.placeholder(R.drawable.default_image);
            Glide.with(context).setDefaultRequestOptions(requestOptions).load(image).into(blogUserImageView);


        }

    }
}
