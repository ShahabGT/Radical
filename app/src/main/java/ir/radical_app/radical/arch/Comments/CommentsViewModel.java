package ir.radical_app.radical.arch.Comments;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

public class CommentsViewModel extends ViewModel {

    public LiveData<PagedList<CommentItem>> itemPagedList;
    private CommentsDataSourceFactory commentsDataSourceFactory;
    public CommentsViewModel() {

        commentsDataSourceFactory = new CommentsDataSourceFactory();
        PagedList.Config config =
                (new PagedList.Config.Builder())
                        .setEnablePlaceholders(false)
                        .setPageSize(CommentsDataSource.SIZE)
                        .build();

        itemPagedList = (new LivePagedListBuilder(commentsDataSourceFactory, config)).build();


    }

    public void invalidateData() {
        commentsDataSourceFactory.invalidateDatasource();
    }
}
