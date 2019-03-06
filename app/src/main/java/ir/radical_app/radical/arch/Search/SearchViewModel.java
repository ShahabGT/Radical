package ir.radical_app.radical.arch.Search;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

public class SearchViewModel extends ViewModel {

    public LiveData<PagedList<SearchItem>> itemPagedList;
    public SearchViewModel() {

        SearchDataSourceFactory searchDataSourceFactory = new SearchDataSourceFactory();
        PagedList.Config config =
                (new PagedList.Config.Builder())
                        .setEnablePlaceholders(false)
                        .setPageSize(SearchDataSource.SIZE)
                        .build();

        itemPagedList = (new LivePagedListBuilder(searchDataSourceFactory, config)).build();


    }
}
