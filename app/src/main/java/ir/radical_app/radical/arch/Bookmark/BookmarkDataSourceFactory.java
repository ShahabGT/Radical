package ir.radical_app.radical.arch.Bookmark;


import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.PageKeyedDataSource;

public class BookmarkDataSourceFactory extends DataSource.Factory {

    private MutableLiveData<PageKeyedDataSource<Integer, BookmarkItem>> itemLiveDataSource = new MutableLiveData<>();
    private String number;
    private String accessToken;

    BookmarkDataSourceFactory(String number, String accessToken){
        this.number=number;
        this.accessToken=accessToken;

    }

    @Override
    public DataSource create() {
        BookmarkDataSource bookmarkDataSource = new BookmarkDataSource(number,accessToken);
        itemLiveDataSource.postValue(bookmarkDataSource);
        return bookmarkDataSource;
    }

    MutableLiveData<PageKeyedDataSource<Integer, BookmarkItem>> getItemLiveDataSource() {
        return itemLiveDataSource;
    }
}
