package com.cmsr.onebase.framework.security.build.filter;

import com.cmsr.onebase.framework.common.event.AppEntityChangeEvent;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class BuildApplicationModificationFilter extends OncePerRequestFilter {

    private List<RequestMatcher> requestMatchers = new ArrayList<>();

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public BuildApplicationModificationFilter() {
        requestMatchers.add(new AntPathRequestMatcher("/app/**", HttpMethod.POST.name()));
        requestMatchers.add(new AntPathRequestMatcher("/etl/**", HttpMethod.POST.name()));
        requestMatchers.add(new AntPathRequestMatcher("/bpm/**", HttpMethod.POST.name()));
        requestMatchers.add(new AntPathRequestMatcher("/flow/**", HttpMethod.POST.name()));
        requestMatchers.add(new AntPathRequestMatcher("/metadata/**", HttpMethod.POST.name()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        boolean appEntityPosted = false;

        for (RequestMatcher requestMatcher : requestMatchers) {
            if (requestMatcher.matches(request)) {
                appEntityPosted = true;
                break;
            }
        }

        Long applicationId = ApplicationManager.getApplicationId();

        if (appEntityPosted && applicationId != null) {
            applicationEventPublisher.publishEvent(
                    AppEntityChangeEvent.builder()
                            .applicationId(applicationId)
                            .build()
            );
        }

        filterChain.doFilter(request, response);
    }
}
