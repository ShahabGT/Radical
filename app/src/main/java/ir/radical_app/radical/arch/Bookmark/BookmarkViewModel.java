package ir.radical_app.radical.arch.Bookmark;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

public class BookmarkViewModel extends ViewModel {

    public LiveData<PagedList<BookmarkItem>> itemPagedList;

    public BookmarkViewModel(String number,String accessToken) {


        BookmarkDataSourceFactory bookmarkDataSourceFactory = new BookmarkDataSourceFactory(number,accessToken);
        PagedList.Config config =
                (new PagedList.Config.Builder())
                        .setEnablePlaceholders(false)
                        .setPageSize(BookmarkDataSource.SIZE)
                        .build();

        itemPagedList = (new LivePagedListBuilder(bookmarkDataSourceFactory, config)).build();


    }


}
