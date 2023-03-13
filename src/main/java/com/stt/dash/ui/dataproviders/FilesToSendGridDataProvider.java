package com.stt.dash.ui.dataproviders;


import com.stt.dash.app.security.CurrentUser;
import com.stt.dash.backend.data.entity.FIlesToSend;
import com.stt.dash.backend.service.FilesToSendService;
import com.stt.dash.ui.utils.BakeryConst;
import com.stt.dash.ui.utils.ODateUitls;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.QuerySortOrderBuilder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.vaadin.artur.spring.dataprovider.FilterablePageableDataProvider;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * A pageable FileToSend data provider.
 */
@SpringComponent
@UIScope
public class FilesToSendGridDataProvider extends FilterablePageableDataProvider<FIlesToSend, FilesToSendGridDataProvider.FileToSendFilter> {
    private final FilesToSendService filesToSendService;
    private final CurrentUser currentUser;
    private List<QuerySortOrder> defaultSortOrders;
    private Consumer<Page<FIlesToSend>> pageObserver;

    public FilesToSendGridDataProvider(FilesToSendService filesToSendService, CurrentUser currentUser) {
        this.filesToSendService = filesToSendService;
        this.currentUser = currentUser;
        setSortOrders(BakeryConst.DEFAULT_SORT_DIRECTION, BakeryConst.BULK_SORT_FIELDS);
    }

    private void setSortOrders(Sort.Direction direction, String[] properties) {
        QuerySortOrderBuilder builder = new QuerySortOrderBuilder();
        for (String property : properties) {
            if (direction.isAscending()) {
                builder.thenAsc(property);
            } else {
                builder.thenDesc(property);
            }
        }
        defaultSortOrders = builder.build();
    }

    @Override
    protected Page<FIlesToSend> fetchFromBackEnd(Query<FIlesToSend, FileToSendFilter> query, Pageable pageable) {
        FileToSendFilter filter = query.getFilter().orElse(FileToSendFilter.getEmptyFilter());
        Page<FIlesToSend> page = filesToSendService.findAnyMatchingAfterDateToSend(currentUser, Optional.ofNullable(filter.getFilter()),
                getFilterDate(filter.isShowPrevious()), pageable);
        System.out.println("******* " + page.getTotalElements() + "/" + page.getTotalPages() + "********");
        if (pageObserver != null) {
            pageObserver.accept(page);
        }
        return page;
    }

    @Override
    protected List<QuerySortOrder> getDefaultSortOrders() {
        return defaultSortOrders;
    }

    @Override
    protected int sizeInBackEnd(Query<FIlesToSend, FileToSendFilter> query) {
        FileToSendFilter filter = query.getFilter().orElse(FileToSendFilter.getEmptyFilter());
        return (int) filesToSendService
                .countAnyMatchingAfterDateToSend(currentUser, Optional.ofNullable(filter.getFilter()), getFilterDate(filter.isShowPrevious()));
    }

    public void setPageObserver(Consumer<Page<FIlesToSend>> pageObserver) {
        this.pageObserver = pageObserver;
    }

    @Override
    public Object getId(FIlesToSend item) {
        return item.getId();
    }

    public static class FileToSendFilter implements Serializable {
        private String filter;
        private boolean showPrevious;

        public String getFilter() {
            return filter;
        }

        public boolean isShowPrevious() {
            return showPrevious;
        }

        public FileToSendFilter(String filter, boolean showPrevious) {
            this.filter = filter;
            this.showPrevious = showPrevious;
        }

        public static FileToSendFilter getEmptyFilter() {
            return new FileToSendFilter("", false);
        }
    }

    private Optional<Date> getFilterDate(boolean showPrevious) {
        if (showPrevious) {
            return Optional.empty();
        }
        LocalDate ld = LocalDate.now().minusDays(1);

        return Optional.of(ODateUitls.valueOf(ld));
    }

}
