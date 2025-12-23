package com.yangxy.cloud.gateway.filter;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GatewayFilterPrinter implements ApplicationRunner {

    private final List<GlobalFilter> globalFilters;

    public GatewayFilterPrinter(List<GlobalFilter> globalFilters) {
        this.globalFilters = globalFilters;
    }

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("===== Global Filters =====");
        globalFilters.forEach(filter ->
                System.out.println(filter.getClass().getName())
        );
    }
}
