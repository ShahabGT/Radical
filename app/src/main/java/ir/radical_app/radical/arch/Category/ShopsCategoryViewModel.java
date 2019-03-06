package ir.radical_app.radical.arch.Category;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

public class ShopsCategoryViewModel extends ViewModel {

    public LiveData<PagedList<ShopsCategoryItem>> itemPagedList;
    public ShopsCategoryViewModel() {

        ShopsCategoryDataSourceFactory shopsDataSourceFactory = new ShopsCategoryDataSourceFactory();
        PagedList.Config config =
                (new PagedList.Config.Builder())
                        .setEnablePlaceholders(false)
                        .setPageSize(ShopsCategoryDataSource.SIZE)
                        .build();

        itemPagedList = (new LivePagedListBuilder(shopsDataSourceFactory, config)).build();


    }
}
