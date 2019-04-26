package ir.radical_app.radical.arch.Upgrade;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;


public class UpgradeViewModel extends ViewModel {

    public LiveData<PagedList<UpgradeItem>> itemPagedList;

    public UpgradeViewModel(String number, String accessToken) {


        UpgradeDataSourceFactory upgradeDataSourceFactory = new UpgradeDataSourceFactory(number,accessToken);
        PagedList.Config config =
                (new PagedList.Config.Builder())
                        .setEnablePlaceholders(false)
                        .setPageSize(UpgradeDataSource.SIZE)
                        .build();

        itemPagedList = (new LivePagedListBuilder(upgradeDataSourceFactory, config)).build();


    }


}
