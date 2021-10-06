package org.folio.ldp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
public class TenantRequestInterceptor extends HandlerInterceptorAdapter {

  static final String OKAPI_TENANT_HEADER = "X-Okapi-Tenant";
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
    Object object) throws Exception {
    String requestURI = request.getRequestURI();
    String tenantID = request.getHeader(OKAPI_TENANT_HEADER);
    if(tenantID == null) {
      System.out.println("No tenant ID provided");
    }
    System.out.println("Tenant ID is set to " + tenantID);
    TenantContext.setCurrentTenant(tenantID);
    return true;    
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response,
    Object handler, ModelAndView modelAndView) throws Exception {
    TenantContext.clear();
  }
  
}
