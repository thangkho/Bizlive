package tuannt.bizlive.adapter;

import tuannt.bizlive.activity.BookmarkActivity;
import tuannt.bizlive.fragment.TabCategoryFragment;

/**
 * Created by Administrator on 12/18/2015.
 */
public class MyAdapter {
    public static volatile MyAdapter myAdapter = null;

    private HomeAdapter gridhomeAdapter;
    private ListAdapter listAdapterHome;
    private ListAdapterHot listAdapterHot;
    private ListAdapterNew listAdapterNew;
    private ListAdapterSearch listAdapterSearch;
    private TabCategoryFragment.GridAdapter gridAdapCategory;// grid category
    private AdapterListComment adaterListCommentRead;
    private AdapterListComment adaterListComment;
    private AdapterRelated adapterRelated;
    private ListAdapterCategory adapterListCategoryfrag;
    private ListAdapterActivityCategory adapterListCategoryActivity;
    private AdapterVpReadingBookMark adapterVpReadingBookMark;
    private ListAdapterRadioNew listAdapterRadioNew;

    public ListAdapterRadioNew getListAdapterRadioNew() {
        return listAdapterRadioNew;
    }

    public void setListAdapterRadioNew(ListAdapterRadioNew listAdapterRadioNew) {
        this.listAdapterRadioNew = listAdapterRadioNew;
    }

    public AdapterVpReadingBookMark getAdapterVpReadingBookMark() {
        return adapterVpReadingBookMark;
    }

    public void setAdapterVpReadingBookMark(AdapterVpReadingBookMark adapterVpReadingBookMark) {
        this.adapterVpReadingBookMark = adapterVpReadingBookMark;
    }

    public AdapterVpReading getAdapterVpReading() {
        return adapterVpReading;
    }

    public void setAdapterVpReading(AdapterVpReading adapterVpReading) {
        this.adapterVpReading = adapterVpReading;
    }

    private AdapterVpReading adapterVpReading;

    public ListAdapterActivityCategory getAdapterListCategoryActivity() {
        return adapterListCategoryActivity;
    }

    public void setAdapterListCategoryActivity(ListAdapterActivityCategory adapterListCategoryActivity) {
        this.adapterListCategoryActivity = adapterListCategoryActivity;
    }

    public ListAdapterSearch getListAdapterSearch() {
        return listAdapterSearch;
    }

    public void setListAdapterSearch(ListAdapterSearch listAdapterSearch) {
        this.listAdapterSearch = listAdapterSearch;
    }

    private BookmarkActivity.ListMarkedAdapter adapterBookmark;

    public BookmarkActivity.ListMarkedAdapter getAdapterBookmark() {
        return adapterBookmark;
    }

    public void setAdapterBookmark(BookmarkActivity.ListMarkedAdapter adapterBookmark) {
        this.adapterBookmark = adapterBookmark;
    }

    public ListAdapterCategory getAdapterListCategoryfrag() {
        return adapterListCategoryfrag;
    }

    public void setAdapterListCategoryfrag(ListAdapterCategory adapterListCategoryfrag) {
        this.adapterListCategoryfrag = adapterListCategoryfrag;
    }

    public AdapterRelated getAdapterRelated() {
        return adapterRelated;
    }

    public void setAdapterRelated(AdapterRelated adapterRelated) {
        this.adapterRelated = adapterRelated;
    }

    public AdapterListComment getAdaterListComment() {
        return adaterListComment;
    }

    public void setAdaterListComment(AdapterListComment adaterListComment) {
        this.adaterListComment = adaterListComment;
    }

    public AdapterListComment getAdaterListCommentRead() {
        return adaterListCommentRead;
    }

    public void setAdaterListCommentRead(AdapterListComment adaterListCommentRead) {
        this.adaterListCommentRead = adaterListCommentRead;
    }

    public TabCategoryFragment.GridAdapter getGridAdapCategory() {
        return gridAdapCategory;
    }

    public void setGridAdapCategory(TabCategoryFragment.GridAdapter gridAdapCategory) {
        this.gridAdapCategory = gridAdapCategory;
    }

    public ListAdapterNew getListAdapterNew() {
        return listAdapterNew;
    }

    public void setListAdapterNew(ListAdapterNew listAdapterNew) {
        this.listAdapterNew = listAdapterNew;
    }

    public ListAdapterHot getListAdapterHot() {
        return listAdapterHot;
    }

    public void setListAdapterHot(ListAdapterHot listAdapterHot) {
        this.listAdapterHot = listAdapterHot;
    }

    public ListAdapter getListAdapterHome() {
        return listAdapterHome;
    }

    public void setListAdapterHome(ListAdapter listAdapterHome) {
        this.listAdapterHome = listAdapterHome;
    }

    public HomeAdapter getGridhomeAdapter() {
        return gridhomeAdapter;
    }

    public void setGridhomeAdapter(HomeAdapter gridhomeAdapter) {
        this.gridhomeAdapter = gridhomeAdapter;
    }
    public static MyAdapter getInstance(){
        if (myAdapter == null ) {
            synchronized (MyAdapter.class) {
                if (myAdapter == null) {
                    myAdapter = new MyAdapter();
                }
            }
        }
        return myAdapter;
    }
}
