package com.stt.dash.ui.views.bulksms;

import com.stt.dash.backend.data.entity.FIlesToSend;
import com.stt.dash.backend.data.entity.Order;
import com.stt.dash.ui.utils.ODateUitls;
import com.stt.dash.ui.views.storefront.beans.OrderCardHeader;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;

public class FileToSendCardHeaderGenerator {

    private class HeaderWrapper {
        private Predicate<LocalDate> matcher;

        private OrderCardHeader header;

        private Long selected;

        public HeaderWrapper(Predicate<LocalDate> matcher, OrderCardHeader header) {
            this.matcher = matcher;
            this.header = header;
        }

        public boolean matches(LocalDate date) {
            return matcher.test(date);
        }

        public Long getSelected() {
            return selected;
        }

        public void setSelected(Long selected) {
            this.selected = selected;
        }

        public OrderCardHeader getHeader() {
            return header;
        }
    }

    private final DateTimeFormatter HEADER_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("EEE, MMM d");

    private final Map<Long, OrderCardHeader> ordersWithHeaders = new HashMap<>();
    private List<FileToSendCardHeaderGenerator.HeaderWrapper> headerChain = new ArrayList<>();

    private OrderCardHeader getRecentHeader() {
        return new OrderCardHeader("Recent", "Antes de esta semana");
    }

    private OrderCardHeader getYesterdayHeader() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        return new OrderCardHeader("Ayer", secondaryHeaderFor(yesterday));
    }

    private OrderCardHeader getTodayHeader() {
        LocalDate today = LocalDate.now();
        return new OrderCardHeader("Hoy", secondaryHeaderFor(today));
    }

    private OrderCardHeader getThisWeekBeforeYesterdayHeader() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate thisWeekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
        return new OrderCardHeader("Esta semana antes de ayer", secondaryHeaderFor(thisWeekStart, yesterday));
    }

    private OrderCardHeader getThisWeekStartingTomorrow(boolean showPrevious) {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate nextWeekStart = today.minusDays(today.getDayOfWeek().getValue()).plusWeeks(1);
        return new OrderCardHeader(showPrevious ? "This week starting tomorrow" : "This week",
                secondaryHeaderFor(tomorrow, nextWeekStart));
    }

    private OrderCardHeader getUpcomingHeader() {
        return new OrderCardHeader("Upcoming", "After this week");
    }

    private String secondaryHeaderFor(LocalDate date) {
        return HEADER_DATE_TIME_FORMATTER.format(date);
    }

    private String secondaryHeaderFor(LocalDate start, LocalDate end) {
        return secondaryHeaderFor(start) + " - " + secondaryHeaderFor(end);
    }

    public OrderCardHeader get(Long id) {
        return ordersWithHeaders.get(id);
    }

    public void resetHeaderChain(boolean showPrevious) {
        this.headerChain = createHeaderChain(showPrevious);
        ordersWithHeaders.clear();
    }

    public void ordersRead(List<FIlesToSend> orders) {
        Iterator<FileToSendCardHeaderGenerator.HeaderWrapper> headerIterator = headerChain.stream().filter(h -> h.getSelected() == null).iterator();
        if (!headerIterator.hasNext()) {
            return;
        }

        FileToSendCardHeaderGenerator.HeaderWrapper current = headerIterator.next();
        for (FIlesToSend order : orders) {
            // If last selected, discard orders that match it.
            if (current.getSelected() != null && current.matches(ODateUitls.valueOf2(order.getDateToSend()))) {
                continue;
            }
            while (current != null && !current.matches(ODateUitls.valueOf2(order.getDateToSend()))) {
                current = headerIterator.hasNext() ? headerIterator.next() : null;
            }
            if (current == null) {
                break;
            }
            current.setSelected(order.getId());
            ordersWithHeaders.put(order.getId(), current.getHeader());
        }
    }

    private List<FileToSendCardHeaderGenerator.HeaderWrapper> createHeaderChain(boolean showPrevious) {
        List<FileToSendCardHeaderGenerator.HeaderWrapper> headerChain = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate startOfTheWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
        if (showPrevious) {
            LocalDate yesterday = today.minusDays(1);
            // Week starting on Monday
            headerChain.add(new FileToSendCardHeaderGenerator.HeaderWrapper(d -> d.isBefore(startOfTheWeek), this.getRecentHeader()));
            if (startOfTheWeek.isBefore(yesterday)) {
                headerChain.add(new FileToSendCardHeaderGenerator.HeaderWrapper(d -> d.isBefore(yesterday) && !d.isAfter(startOfTheWeek),
                        this.getThisWeekBeforeYesterdayHeader()));
            }
            headerChain.add(new FileToSendCardHeaderGenerator.HeaderWrapper(yesterday::equals, this.getYesterdayHeader()));
        }
        LocalDate firstDayOfTheNextWeek = startOfTheWeek.plusDays(7);
        headerChain.add(new FileToSendCardHeaderGenerator.HeaderWrapper(today::equals, getTodayHeader()));
        headerChain.add(new FileToSendCardHeaderGenerator.HeaderWrapper(d -> d.isAfter(today) && d.isBefore(firstDayOfTheNextWeek),
                getThisWeekStartingTomorrow(showPrevious)));
        headerChain.add(new FileToSendCardHeaderGenerator.HeaderWrapper(d -> !d.isBefore(firstDayOfTheNextWeek), getUpcomingHeader()));
        return headerChain;
    }
}
