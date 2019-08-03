package ir.radical_app.radical.arch.Comments;


import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.PageKeyedDataSource;

public class CommentsDataSourceFactory extends DataSource.Factory {

    private MutableLiveData<PageKeyedDataSource<Integer, CommentItem>> itemLiveDataSource = new MutableLiveData<>();
    private CommentsDataSource commentsDataSource;

    @Override
    public DataSource create() {
        commentsDataSource = new CommentsDataSource();
        itemLiveDataSource.postValue(commentsDataSource);
        return commentsDataSource;
    }

    void invalidateDatasource() {
        if (commentsDataSource != null) commentsDataSource.invalidate();
        create();
    }

    MutableLiveData<PageKeyedDataSource<Integer, CommentItem>> getItemLiveDataSource() {
        return itemLiveDataSource;
    }
}
