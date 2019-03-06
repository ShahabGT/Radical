package ir.radical_app.radical.arch.Search;

import java.util.List;

public class SearchResponse {

    private List<SearchItem> data;

    public List<SearchItem> getData() {
        return data;
    }

    public void setData(List<SearchItem> data) {
        this.data = data;
    }
}
