package e.user301.worksininternet;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;

import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class PhotoGalleryFragment extends Fragment {
    private static final String TAG = "PhotoGalleryFragment";
    private RecyclerView mPhotoRecyclerView;
    private List<GalleryItems> mItems = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        update();

        //1 Intent intent = PollService.newIntent(getActivity());
        //getActivity().startService(intent);
        // 2 PollService.setSericeAlarm(getActivity(),true);
        Log.d(TAG, "onCreate: background starter");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        mPhotoRecyclerView = view.findViewById(R.id.fragment_photo_gallery_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        setupAdapter();
        return view;
    }

    private void setupAdapter() {
        if(isAdded()){
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
        }
    }

    /**
     * Работа с ресайклером
     */
    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder>{
        List<GalleryItems> rGalleryItems;

        PhotoAdapter(List<GalleryItems> rGalleryItems) {
            this.rGalleryItems = rGalleryItems;
        }

        @NonNull
        @Override
        public PhotoHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.gellery_item, viewGroup, false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PhotoHolder photoHolder, int i) {
            GalleryItems galleryItems = rGalleryItems.get(i);
            photoHolder.bindGalleryItem(galleryItems);
            Log.d(TAG, "onBindViewHolder: " + galleryItems.getrUrl());
        }

        @Override
        public int getItemCount() {
            return rGalleryItems.size();
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder{
        //
        private ImageView rImageView;
        PhotoHolder(@NonNull View itemView) {
            super(itemView);
            rImageView = itemView.findViewById(R.id.fragment_photo_gallery_image_view);
        }

        void bindGalleryItem (GalleryItems galleryItems){
            Picasso.with(getActivity())
                    .load(galleryItems.getrUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(rImageView);
        }
    }
    /**
     * работа с сетью
     *
     */
    @SuppressLint("StaticFieldLeak")
    private class FetchItemsTask extends AsyncTask <Void, Void, List<GalleryItems>>{
        private String query;

        public FetchItemsTask(String query) {
            this.query = query;
        }

        @Override
        protected List<GalleryItems> doInBackground(Void... voids) {

            if (query == null){
                return  new FlickrFetchr().fetchRecentPhotos();
            }else{
                return new FlickrFetchr().seachPhotos(query);
            }
        }

        @Override
        protected void onPostExecute(List<GalleryItems> galleryItems) {
            mItems = galleryItems;
            setupAdapter();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    /**
     * 
     */

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ThumbnailDownloader");
    }
    /**
     *  для меню
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_gallery,menu);

        MenuItem seachItem = menu.findItem(R.id.menu_item_search);
        final SearchView seachView = (SearchView) seachItem.getActionView();
        seachView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d(TAG, "onQueryTextSubmit: " + s);
                QueryPrefereces.setStoreQuery(getActivity(),s);
                update();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        seachView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = QueryPrefereces.getStoreQuery(getActivity());
                seachView.setQuery(query, false);
            }
        });

        MenuItem toggleItem = menu.findItem(R.id.menu_item_toggle_polling);
        if (PollService.isServiceAlarm(getActivity()) ){
            toggleItem.setTitle("Stop polling");
        }else {
            toggleItem.setTitle("Start polling");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_search:
                QueryPrefereces.setStoreQuery(getActivity(), null);
                update();
                return true;
            case R.id.menu_item_toggle_polling:
                boolean shouldAlarm = !PollService.isServiceAlarm(getActivity());
                PollService.setSericeAlarm(getActivity(),shouldAlarm);
                // обновление меню
                getActivity().invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void update (){
        String query = QueryPrefereces.getStoreQuery(getActivity());
        new FetchItemsTask(query).execute();
    }
}
