package ir.radical_app.radical.arch.Shops;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

public class ShopsViewModel extends ViewModel {

    public LiveData<PagedList<ShopsItem>> itemPagedList;
    public ShopsViewModel() {

        ShopsDataSourceFactory shopsDataSourceFactory = new ShopsDataSourceFactory();
        PagedList.Config config =
                (new PagedList.Config.Builder())
                        .setEnablePlaceholders(false)
                        .setPageSize(ShopsDataSource.SIZE)
                        .build();

        itemPagedList = (new LivePagedListBuilder(shopsDataSourceFactory, config)).build();


    }
}
