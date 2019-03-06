package ir.radical_app.radical.arch.Search;


import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.PageKeyedDataSource;

public class SearchDataSourceFactory extends DataSource.Factory {

    private MutableLiveData<PageKeyedDataSource<Integer, SearchItem>> itemLiveDataSource = new MutableLiveData<>();


    @Override
    public DataSource create() {
        SearchDataSource searchDataSource = new SearchDataSource();
        itemLiveDataSource.postValue(searchDataSource);
        return searchDataSource;
    }

    MutableLiveData<PageKeyedDataSource<Integer, SearchItem>> getItemLiveDataSource() {
        return itemLiveDataSource;
    }
}
