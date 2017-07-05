package com.ygm.andriod.recycler_volley;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ArrayList<Site> mArrayList;
    private String urlJsonArry = "http://192.168.43.18/json.php";
    private SiteAdapter mAdapter;
    private static String TAG = MainActivity.class.getSimpleName();
    private Button mButton;
    private Uri imageuri;
    private ImageView picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mArrayList=new ArrayList<>();
       mRecyclerView=(RecyclerView) findViewById(R.id.Recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        picture=(ImageView) findViewById(R.id.imageview);
        mButton=(Button) findViewById(R.id.button);
        Button photob=(Button) findViewById(R.id.xiangji);
        photob.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                File outputimage=new File(getExternalCacheDir(),"ygm_image.jpg");
                try {
                    if (outputimage.exists()){
                        outputimage.delete();
                    }
                    outputimage.createNewFile();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT>=24){
                   imageuri= FileProvider.getUriForFile(getApplicationContext(),"com.ygm.andriod.recycler_volley.fileprovider",outputimage);
                } else {
                    imageuri=Uri.fromFile(outputimage);
                }
                Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageuri);
                startActivityForResult(intent,1);
            }
        });
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeJsonArrayRequest();
                Toast.makeText(getApplicationContext(),"haha"+mArrayList.size(),Toast.LENGTH_LONG).show();
                if (mAdapter==null) {
                    mAdapter = new SiteAdapter(mArrayList);
                    mRecyclerView.setAdapter(mAdapter);
                }else {
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
        //makeJsonArrayRequest();
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        switch (requestCode){
            case 1:
                if (requestCode==RESULT_OK){
                    try {
                        Bitmap bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(imageuri));
                        picture.setImageBitmap(bitmap);

                    } catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    private class SiteAdapter extends RecyclerView.Adapter<SiteHolder>{

        public SiteAdapter(ArrayList<Site> mSites){
            mArrayList=mSites;
        }
        @Override
        public SiteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater=getLayoutInflater();
            View view=layoutInflater.inflate(R.layout.siteitem,parent,false);
            return new SiteHolder(view);
        }
        @Override
        public void onBindViewHolder(SiteHolder holder ,int postion) {
            Site site=mArrayList.get(postion);
            //holder.mTitleTextView.setText(crime.getTitle());
            holder.bindsite(site);
        }
        @Override
        public int getItemCount(){
            return mArrayList.size();
        }


    }

    private class SiteHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // public TextView mTitleTextView;
        private Site mSite;
        private TextView mSiteNameView;
        private TextView mSiteTypeView;
        private TextView mSiteQuxianView;

        public SiteHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            mSiteNameView=(TextView)itemView.findViewById(R.id.site_name);
            mSiteTypeView=(TextView)itemView.findViewById(R.id.site_type);
            mSiteQuxianView=(TextView) itemView.findViewById(R.id.site_quxian);

        }
        public void bindsite(Site site){
            mSite=site;
            mSiteNameView.setText(mSite.getStieName());
            mSiteQuxianView.setText(mSite.getSiteQuXian());
            mSiteTypeView.setText(mSite.getStietype());
        }

        @Override
        public void onClick(View v){
            Toast.makeText(getApplication(),mSite.mStieName,Toast.LENGTH_LONG).show();

        }

    }
    public class Site {
        public String mStieName;
        public String mSiteQuXian;
        public String mStieJindu;
        public String mSiteWeidu;
        public String mStietype;

        public String getStieName() {
            return mStieName;
        }

        public void setStieName(String stieName) {
            mStieName = stieName;
        }

        public String getSiteQuXian() {
            return mSiteQuXian;
        }

        public String getStieJindu() {
            return mStieJindu;
        }

        public void setStieJindu(String stieJindu) {
            mStieJindu = stieJindu;
        }

        public void setSiteQuXian(String siteQuXian) {
            mSiteQuXian = siteQuXian;
        }

        public String getSiteWeidu() {
            return mSiteWeidu;
        }

        public void setSiteWeidu(String siteWeidu) {
            mSiteWeidu = siteWeidu;
        }

        public String getStietype() {
            return mStietype;
        }

        public void setStietype(String stietype) {
            mStietype = stietype;
        }
    }

    private void makeJsonArrayRequest() {

        //showpDialog();

        JsonArrayRequest req = new JsonArrayRequest(urlJsonArry,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                       // mArrayList=new ArrayList<>();
                        mArrayList.clear();


                       try {
                            // Parsing json array response
                            // loop through each json object

                            for (int i = 0; i < response.length(); i++) {
                                Site msite=new Site();
                                JSONObject msonobject=(JSONObject)response.get(i);
                                msite.mSiteQuXian=msonobject.getString("quxian");
                                msite.mStieName=msonobject.getString("name");
                                msite.mStieJindu=msonobject.getString("long");
                                msite.mSiteWeidu=msonobject.getString("lat");
                                msite.mStietype=msonobject.getString("type");
                                mArrayList.add(msite);
                                // JSONObject person = (JSONObject) response.get(i);
                            }


                            //   txtResponse.setText(jsonResponse);
                        //    Toast.makeText(getApplicationContext(),   "succes quest"+mArrayList.size(),
                         //           Toast.LENGTH_LONG).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                           VolleyLog.d("ygm log success quest", e.toString());
                            VolleyLog.d("ygm log success quest", response.toString());

                        }

                        //   hidepDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                VolleyLog.d(TAG, "Error: " + error.toString());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                //  hidepDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }


}
