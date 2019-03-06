package ir.radical_app.radical.arch.BuyHistory;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

public class BuyHistoryViewModel extends ViewModel {

    public LiveData<PagedList<BuyItem>> itemPagedList;

    public BuyHistoryViewModel(String number, String accessToken) {


        BuyHistoryDataSourceFactory buyHistoryDataSourceFactory = new BuyHistoryDataSourceFactory(number,accessToken);
        PagedList.Config config =
                (new PagedList.Config.Builder())
                        .setEnablePlaceholders(false)
                        .setPageSize(BuyHistoryDataSource.SIZE)
                        .build();

        itemPagedList = (new LivePagedListBuilder(buyHistoryDataSourceFactory, config)).build();


    }


}
