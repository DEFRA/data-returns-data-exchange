package uk.gov.defra.datareturns.data.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Spring-Web error controller
 *
 * @author Sam Gardner-Dell
 */
@RestController
@RequiredArgsConstructor
@ConditionalOnWebApplication
public class ErrorController implements org.springframework.boot.autoconfigure.web.ErrorController {
    /**
     * spring boot server properties bean
     */
    private final ServerProperties serverProperties;

    /**
     * Server error handler method
     *
     * @param request the servlet request
     * @return a {@link Map} of the properties exposed in the error response
     */
    @RequestMapping(path = "${server.error.path:${error.path:/error}}")
    @ResponseBody
    public Map<String, Object> handle(final HttpServletRequest request) {
        final Map<String, Object> map = new HashMap<>();
        map.put("status", request.getAttribute("javax.servlet.error.status_code"));
        map.put("reason", request.getAttribute("javax.servlet.error.message"));
        map.put("uri", request.getAttribute("javax.servlet.error.request_uri"));
        return map;
    }

    @Override
    public String getErrorPath() {
        return serverProperties.getError().getPath();
    }
}
