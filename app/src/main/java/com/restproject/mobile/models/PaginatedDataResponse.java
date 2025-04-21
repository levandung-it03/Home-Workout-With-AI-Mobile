package com.restproject.mobile.models;

import com.google.gson.internal.LinkedTreeMap;

import java.util.List;
import java.util.Objects;

public class PaginatedDataResponse {
    private List<LinkedTreeMap> data;
    private Integer currentPage;
    private Integer totalPages;

    public PaginatedDataResponse() {
    }

    public PaginatedDataResponse(List<LinkedTreeMap> data, Integer currentPage, Integer totalPages) {
        this.data = data;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
    }

    public List<LinkedTreeMap> getData() {
        return data;
    }

    public void setData(List<LinkedTreeMap> data) {
        this.data = data;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public static PaginatedDataResponse mapping(LinkedTreeMap data) {
        PaginatedDataResponse result = new PaginatedDataResponse();
        if (data.containsKey("data") && Objects.nonNull(data.get("data")))
            result.setData((List<LinkedTreeMap>) data.get("data"));
        if (data.containsKey("currentPage") && Objects.nonNull(data.get("currentPage")))
            result.setCurrentPage((int) Double.parseDouble(data.get("currentPage").toString()));
        if (data.containsKey("totalPages") && Objects.nonNull(data.get("totalPages")))
            result.setTotalPages((int) Double.parseDouble(data.get("totalPages").toString()));
        return result;
    }
}
