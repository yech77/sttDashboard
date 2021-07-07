package com.stt.dash.backend.data.bean;

public class OPageable {

    private int currentPage;
    private int currentPageToShow;
    private long currentData;
    private long totalData;
    private int totalPage;
    private long totalDataPage;

    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * Cambia de pagina. Retorna un negativo si esta en la ultima
     *
     * @return
     */
    public int nextPage() {
        if (currentPage + 1 >= totalPage) {
            return -1;
        }
        setCurrentPage(++currentPage);
        return currentPage;
    }

    /**
     * Cambia de pagina. Retorna un negativo si esta en la primera
     *
     * @return
     */
    public int beforePage() {
        if (currentPage - 1 < 0) {
            return -1;
        }
        setCurrentPage(--currentPage);
        return currentPage;
    }

    public boolean hasNext() {
        return currentPage + 1 < totalPage;
    }

    public boolean hasBefore() {
        return currentPage - 1 >-1;
    }

    public void setCurrentPage(int currentPage) {
        if (currentPage >= totalPage) {
            return;
        }
        this.currentPage = currentPage;
        this.currentPageToShow = currentPage + 1;
    }

    public long getCurrentData() {
        return currentData;
    }

    public void setCurrentData(long currentData) {
        this.currentData = currentData;
    }

    public long getTotalData() {
        return totalData;
    }

    public void setTotalData(long totalData) {
        this.totalData = totalData;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public void init() {
        currentPage = 0;
        currentData = 0;
        currentPageToShow=0;
        totalData = 0;
        totalPage = 0;
    }

    public int getCurrentPageToShow() {
        return currentPageToShow;
    }

    public long getTotalDataPage() {
        return totalDataPage;
    }

    public void setTotalDataPage(long totalDataPage) {
        this.totalDataPage = totalDataPage;
    }

    @Override
    public String toString() {
        return "OPageable{" + "currentPage=" + currentPage + ", currentPageToShow=" + currentPageToShow + ", currentData=" + currentData + ", totalData=" + totalData + ", totalPage=" + totalPage + '}';
    }

}
