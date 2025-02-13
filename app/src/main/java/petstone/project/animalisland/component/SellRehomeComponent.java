package petstone.project.animalisland.component;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import petstone.project.animalisland.R;
import petstone.project.animalisland.activity.RehomeSellSubmitActivity;
import petstone.project.animalisland.other.SellRecycleAdapter;
import petstone.project.animalisland.other.SellRehomeList;

public class SellRehomeComponent extends Fragment {

    FirebaseAuth auth;
    FirebaseFirestore db;

    FloatingActionButton sell_submit;
    RecyclerView recyclerView ;
    SellRecycleAdapter srAdapter ;
    ArrayList<SellRehomeList> mList = new ArrayList<SellRehomeList>();
    ArrayList<SellRehomeList> FilterList = new ArrayList<SellRehomeList>();
    SearchView sell_search;

    FirebaseStorage storage;
    StorageReference ImgRef, main_img;
    String s_animal_type, s_birth, s_local, s_date, s_price, s_did ;
    int sex ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sell_rehome, container, false);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        ImgRef = storage.getReference("PostImg");

        sell_submit = view.findViewById(R.id.sell_submit);
        recyclerView = view.findViewById(R.id.sell_recycler);
        sell_search = view.findViewById(R.id.sell_search_view);

        auth = FirebaseAuth.getInstance();

        sell_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Search(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Search(s);
                return false;
            }
        });

        db.collection("sale_posts")
                .whereNotEqualTo("is_sell", "0")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        mList.clear();
                        for (DocumentSnapshot document : value) {
                            s_animal_type = "[" + document.getData().get("animal_type").toString() + "]";
                            s_date = "작성날짜 : " + document.getData().get("date").toString();
                            s_price = document.getData().get("is_sell").toString() + "원";
                            s_birth = "생년월일 : " + document.getData().get("birth").toString();
                            s_did = document.getData().get("document_id").toString();
                            s_local = "지역 : " + document.getData().get("district").toString();

                            if ((document.getData().get("sex").toString()).equals("암컷")){
                                sex = R.drawable.female;
                            }
                            else if((document.getData().get("sex").toString()).equals("수컷")){
                                sex = R.drawable.male;
                            }

                            StorageReference postImgRef = ImgRef.child(s_did);
                            main_img = postImgRef.child("img1");

                            addItem(main_img,
                                    sex,
                                    s_animal_type,
                                    document.getData().get("animal_breed").toString(),
                                    s_birth,
                                    s_local,
                                    s_date,
                                    s_price,
                                    s_did) ;

                            srAdapter = new SellRecycleAdapter(mList);
                            recyclerView.setAdapter(srAdapter);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.removeAllViewsInLayout();
                            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                            srAdapter.notifyDataSetChanged();
                        }
                    }
                });

        //유료분양 회원만 게시물 작성할 수 있도록
        db.collection("users")
                .whereEqualTo("uid", auth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {

                            if ((boolean)document.get("sell_permission")) {
                                sell_submit.setVisibility(View.VISIBLE);
                            } else {
                                sell_submit.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                });

        sell_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), RehomeSellSubmitActivity.class);
                startActivityForResult(intent, 0 );
            }
        });

        return view;
    }

    public void Search(String text){


        try {
            String s_text = text;
            FilterList.clear();

            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i).getLocal().toLowerCase().contains(s_text.toLowerCase())) {
                    FilterList.add(mList.get(i));
                } else if (mList.get(i).getType().toLowerCase().contains(s_text.toLowerCase())) {
                    FilterList.add(mList.get(i));
                } else if (mList.get(i).getBreed().toLowerCase().contains(s_text.toLowerCase())) {
                    FilterList.add(mList.get(i));
                }
            }
            srAdapter.filterList(FilterList);
        }catch (Exception e)
        {
            Log.d("검색에러",e.toString());
        }
    }

    public void addItem(StorageReference main, int gender, String type, String breed, String birth, String local, String date, String price, String did) {
        SellRehomeList item = new SellRehomeList();

        item.setImg(main);
        item.setGenderImg(gender);
        item.setType(type);
        item.setBreed(breed);
        item.setBirth(birth);
        item.setLocal(local);
        item.setDate(date);
        item.setPrice(price);
        item.setDid(did);

        mList.add(item);
    }
}
