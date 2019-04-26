package ir.radical_app.radical.arch.Upgrade;


import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.PageKeyedDataSource;


public class UpgradeDataSourceFactory extends DataSource.Factory {

    private MutableLiveData<PageKeyedDataSource<Integer, UpgradeItem>> itemLiveDataSource = new MutableLiveData<>();
    private String number;
    private String accessToken;

    public UpgradeDataSourceFactory(String number, String accessToken){
        this.number=number;
        this.accessToken=accessToken;

    }

    @Override
    public DataSource create() {
        UpgradeDataSource upgradeDataSource = new UpgradeDataSource(number,accessToken);
        itemLiveDataSource.postValue(upgradeDataSource);
        return upgradeDataSource;
    }

    MutableLiveData<PageKeyedDataSource<Integer, UpgradeItem>> getItemLiveDataSource() {
        return itemLiveDataSource;
    }
}
