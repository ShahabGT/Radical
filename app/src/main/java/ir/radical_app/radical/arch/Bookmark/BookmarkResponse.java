package ir.radical_app.radical.arch.Bookmark;

import java.util.List;

public class BookmarkResponse {

    private List<BookmarkItem> data;

    public List<BookmarkItem> getData() {
        return data;
    }

    public void setData(List<BookmarkItem> data) {
        this.data = data;
    }
}
