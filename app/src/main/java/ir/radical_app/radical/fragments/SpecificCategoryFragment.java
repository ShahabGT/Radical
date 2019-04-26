package ir.radical_app.radical.fragments;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ir.radical_app.radical.arch.Bookmark.BookmarkAdapter;
import ir.radical_app.radical.arch.Bookmark.BookmarkItem;
import ir.radical_app.radical.arch.Bookmark.BookmarkViewModel;
import ir.radical_app.radical.arch.Category.ShopsCategoryAdapter;
import ir.radical_app.radical.arch.Category.ShopsCategoryItem;
import ir.radical_app.radical.arch.Category.ShopsCategoryViewModel;
import ir.radical_app.radical.arch.Search.SearchAdapter;
import ir.radical_app.radical.arch.Search.SearchItem;
import ir.radical_app.radical.arch.Search.SearchViewModel;
import ir.radical_app.radical.classes.MySharedPreference;
import ir.radical_app.radical.R;
import ir.radical_app.radical.dialogs.InfoDialog;


public class SpecificCategoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private boolean isSearch;
    private boolean isBookmark;
    private static ConstraintLayout emptyView;
    private static TextView emptyViewText;

    public void setSearch(boolean search) {
        isSearch = search;
    }

    public void setBookmark(boolean bookmark) {
        isBookmark = bookmark;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_specific_category, container, false);
        init(v);
        return v;
    }

    private void showInfoDialog() {
        if(MySharedPreference.Companion.getInstance(getContext()).getFirstFavorite()){
            MySharedPreference.Companion.getInstance(getContext()).setFirstFavorite();
        }else{
            return;
        }


        InfoDialog introDialog = new InfoDialog(getContext(),"مراکز مورد علاقتو که قبلا نشانه گذاری کردی میتونی ببینی");
        introDialog.setCancelable(true);
        introDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        introDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        introDialog.show();
        Window window = introDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void init(View v){
        recyclerView = v.findViewById(R.id.specific_category_recycler);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        emptyView = v.findViewById(R.id.empty_view);
        emptyViewText = v.findViewById(R.id.empty_text);

        if(isSearch)
            showSearchData();
        else if (isBookmark)
            showBookmarkData();
        else
            showCategoryData();
    }

    public static void showEmpty(String text){
        emptyView.setVisibility(View.VISIBLE);
        emptyViewText.setText(text);
    }

    private void showSearchData(){
        SearchViewModel viewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        final SearchAdapter adapter = new SearchAdapter(getContext(),getActivity());

        viewModel.itemPagedList.observe(this, new Observer<PagedList<SearchItem>>() {
            @Override
            public void onChanged(PagedList<SearchItem> searchItems) {
                adapter.submitList(searchItems);
            }
        });
        recyclerView.setAdapter(adapter);
    }
    private void showBookmarkData(){
        String number = MySharedPreference.Companion.getInstance(getContext()).getNumber();
        String accessToken = MySharedPreference.Companion.getInstance(getContext()).getAccessToken();
        BookmarkViewModel viewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T)new BookmarkViewModel(number,accessToken);
            }
        }).get(BookmarkViewModel.class);
        final BookmarkAdapter adapter = new BookmarkAdapter(getContext(),getActivity());

        viewModel.itemPagedList.observe(this, new Observer<PagedList<BookmarkItem>>() {
            @Override
            public void onChanged(PagedList<BookmarkItem> searchItems) {
                adapter.submitList(searchItems);
            }
        });
        recyclerView.setAdapter(adapter);
        showInfoDialog();

    }
    private void showCategoryData(){
        ShopsCategoryViewModel itemViewModel = ViewModelProviders.of(this).get(ShopsCategoryViewModel.class);
        final ShopsCategoryAdapter adapter = new ShopsCategoryAdapter(getContext(),getActivity());


        itemViewModel.itemPagedList.observe(this, new Observer<PagedList<ShopsCategoryItem>>() {

            @Override
            public void onChanged(PagedList<ShopsCategoryItem> shopsItems) {
                adapter.submitList(shopsItems);
            }
        });
        recyclerView.setAdapter(adapter);
    }

}
