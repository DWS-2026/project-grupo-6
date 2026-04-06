package com.example.projectgrupo6.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class CSRFHandlerConfiguration implements WebMvcConfigurer {

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new CSRFHandlerInterceptor());
	}
}

class CSRFHandlerInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler,
            final ModelAndView modelAndView) throws Exception {

        if (modelAndView != null && modelAndView.getViewName() != null && !modelAndView.getViewName().startsWith("redirect:")) {

            CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
            if (token != null) {
                // Añadimos el token al modelo para usarlo como ${token} en HTML
                modelAndView.addObject("token", token.getToken());
            }
        }
    }
}