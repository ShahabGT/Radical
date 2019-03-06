package ir.radical_app.radical.arch.BuyHistory;


import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.PageKeyedDataSource;

public class BuyHistoryDataSourceFactory extends DataSource.Factory {

    private MutableLiveData<PageKeyedDataSource<Integer, BuyItem>> itemLiveDataSource = new MutableLiveData<>();
    private String number;
    private String accessToken;

    public BuyHistoryDataSourceFactory(String number, String accessToken){
        this.number=number;
        this.accessToken=accessToken;

    }

    @Override
    public DataSource create() {
        BuyHistoryDataSource buyHistoryDataSource = new BuyHistoryDataSource(number,accessToken);
        itemLiveDataSource.postValue(buyHistoryDataSource);
        return buyHistoryDataSource;
    }

    MutableLiveData<PageKeyedDataSource<Integer, BuyItem>> getItemLiveDataSource() {
        return itemLiveDataSource;
    }
}
