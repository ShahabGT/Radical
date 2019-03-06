package ir.radical_app.radical.arch.Shops;


import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.PageKeyedDataSource;

public class ShopsDataSourceFactory extends DataSource.Factory {

    private MutableLiveData<PageKeyedDataSource<Integer, ShopsItem>> itemLiveDataSource = new MutableLiveData<>();


    @Override
    public DataSource create() {
        ShopsDataSource shopsDataSource = new ShopsDataSource();
        itemLiveDataSource.postValue(shopsDataSource);
        return shopsDataSource;
    }

    MutableLiveData<PageKeyedDataSource<Integer, ShopsItem>> getItemLiveDataSource() {
        return itemLiveDataSource;
    }
}
