package ir.radical_app.radical.arch.Category;


import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.PageKeyedDataSource;

public class ShopsCategoryDataSourceFactory extends DataSource.Factory {

    private MutableLiveData<PageKeyedDataSource<Integer, ShopsCategoryItem>> itemLiveDataSource = new MutableLiveData<>();


    @Override
    public DataSource create() {
        ShopsCategoryDataSource shopsDataSource = new ShopsCategoryDataSource();
        itemLiveDataSource.postValue(shopsDataSource);
        return shopsDataSource;
    }

    MutableLiveData<PageKeyedDataSource<Integer, ShopsCategoryItem>> getItemLiveDataSource() {
        return itemLiveDataSource;
    }
}
