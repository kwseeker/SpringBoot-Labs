package top.kwseeker.springboot.lab03.springsecurity;

import javax.servlet.*;
import java.io.IOException;

public class AFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("AFilter doFilter ...");
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
