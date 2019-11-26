package com.example.photosblogapp;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private RecyclerView blogListView;
    private List<BlogPost> bloglist;
    private FirebaseFirestore firestore;
    private BlogRecyclerAdapter blogRecyclerAdapter;
    private FirebaseAuth auth;
    DocumentSnapshot lastVisible;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_home, container, false);


        blogListView=view.findViewById(R.id.home_blog_view);
        firestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();

        bloglist=new ArrayList<>();

        blogRecyclerAdapter=new BlogRecyclerAdapter(bloglist);

        blogListView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        blogListView.setAdapter(blogRecyclerAdapter);

        if (auth.getCurrentUser()!=null) {

            blogListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean reachButton=!recyclerView.canScrollVertically(1);
                    if (reachButton){
                        String desc=lastVisible.getString("desc");
                        Toast.makeText(container.getContext(), ""+desc, Toast.LENGTH_SHORT).show();
                        loadMorePost();
                    }
                }
            });

            Query query=firestore.collection("posts").orderBy("times_tamp",Query.Direction.DESCENDING).limit(3);
            query.addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            BlogPost blogPost = doc.getDocument().toObject(BlogPost.class);
                            bloglist.add(blogPost);

                            lastVisible = queryDocumentSnapshots.getDocuments()
                                    .get(queryDocumentSnapshots.size() -1);

                            blogRecyclerAdapter.notifyDataSetChanged();


                        }
                    }

                }
            });
        }

        // Inflate the layout for this fragment
        return view;


    }
public void loadMorePost(){

    Query query=firestore.collection("posts")
            .orderBy("times_tamp",Query.Direction.DESCENDING)
            .startAfter(lastVisible)
            .limit(3);
    query.addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
            if (!queryDocumentSnapshots.isEmpty()){
            lastVisible = queryDocumentSnapshots.getDocuments()
                    .get(queryDocumentSnapshots.size() -1);
            for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                if (doc.getType() == DocumentChange.Type.ADDED) {
                    BlogPost blogPost = doc.getDocument().toObject(BlogPost.class);
                    bloglist.add(blogPost);

                    blogRecyclerAdapter.notifyDataSetChanged();
                }

                }
            }

        }
    });
}

}
