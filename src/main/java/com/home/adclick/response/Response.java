package com.home.adclick.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public class Response {
    @JsonInclude(JsonInclude.Include. NON_NULL)
    Interval interval;
    @JsonInclude(JsonInclude.Include. NON_NULL)
    Statistics stats;
    @JsonInclude(JsonInclude.Include. NON_NULL)
    List<Data> data;


    public Interval getInterval() {
        return interval;
    }

    public void setInterval(Interval interval) {
        this.interval = interval;
    }

    public Statistics getStats() {
        return stats;
    }

    public void setStats(Statistics stats) {
        this.stats = stats;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }
}
