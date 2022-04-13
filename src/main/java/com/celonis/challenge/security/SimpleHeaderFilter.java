package com.celonis.challenge.security;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class SimpleHeaderFilter extends OncePerRequestFilter {

    private final String HEADER_NAME = "Celonis-Auth";
    private final String HEADER_VALUE = "totally_secret";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // OPTIONS should always work
        if (request.getMethod().equals("OPTIONS")) {
            filterChain.doFilter(request, response);
            return;
        }

        String val = request.getHeader(HEADER_NAME);
        if (val == null || !val.equals(HEADER_VALUE)) {
            response.setStatus(401);
            response.getWriter().append("Not authorized");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
