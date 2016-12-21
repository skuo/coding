package com.coding.inauth.test;

import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.coding.inauth.model.Location;
import com.coding.inauth.service.data.LocationDs;

@Controller
public class InAuthController {
    private static final Log log = LogFactory.getLog(InAuthController.class);

    @Autowired
    LocationDs locationDs;

    @RequestMapping(method = RequestMethod.GET, value = "/inauth/location/{latitude}/{longitude}/", headers = "accept=application/json")
    @ResponseBody
    public Location getLocation(@PathVariable float latitude, @PathVariable float longitude, HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        log.info("getLocation: latitude=" + latitude + ", longitude=" + longitude + ", accept=" + request.getHeader("Content-Type")
                + ", Authorization=" + request.getHeader("Authorization"));
        Location location = null;
        try {
            location = locationDs.find(latitude, longitude);
            if (location == null)
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (EmptyResultDataAccessException erdae) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        response.setHeader("Approved", "true");
        return location;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/inauth/locations", headers = "accept=application/json")
    @ResponseBody
    public List<Location> getLocations(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        log.info("getLocations: accept=" + request.getHeader("Content-Type")
                + ", Authorization=" + request.getHeader("Authorization"));
        List<Location> locations = new LinkedList<>();
        try {
            locations = locationDs.getLocations();
        } catch (EmptyResultDataAccessException erdae) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        response.setHeader("Approved", "true");
        return locations;
    }

    @SuppressWarnings("unused")
    private void printHeader(HttpServletRequest request) {
        Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String header = names.nextElement();
            log.info("\t" + header + "=" + request.getHeader(header));
        }
    }
}
