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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.coding.inauth.InAuth;
import com.coding.inauth.model.Location;
import com.coding.inauth.service.data.LocationDs;
import com.coding.model.RestStatus;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class InAuthController {
    private static final Log log = LogFactory.getLog(InAuthController.class);

    @Autowired
    LocationDs locationDs;

    @Autowired 
    InAuth inAuth;
    
    @RequestMapping(method = RequestMethod.GET, value = "/inauth/location/{latitude}/{longitude}/", headers = "accept=application/json")
    @ResponseBody
    public Location getLocation(@PathVariable float latitude, @PathVariable float longitude, HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        log.info("getLocation: latitude=" + latitude + ", longitude=" + longitude + ", accept=" + request.getHeader("Content-Type")
                + ", Authorization=" + request.getHeader("Authorization"));
        Location location = null;
        try {
            location = getLocation(latitude, longitude);
            if (location == null)
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        response.setHeader("Approved", "true");
        return location;
    }

    private Location getLocation(float latitude, float longitude) {
        Location location = null;
        try {
            location = locationDs.find(latitude, longitude);
        } catch (EmptyResultDataAccessException erdae) {
            // do nothing.  location = null;
        }        
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

    @RequestMapping(method = RequestMethod.POST, value = "/inauth/location", headers = "accept=application/json")
    @ResponseBody
    public RestStatus addData(HttpServletRequest request, HttpServletResponse response, @RequestBody String body)
            throws Exception {
        log.info(body);

        // convert JSON to Location
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        JsonParser jsonParser = mapper.getFactory().createParser(body);
        Location location = mapper.readValue(jsonParser, new TypeReference<Location>() {});
        log.info(location);
        
        RestStatus restStatus = new RestStatus(RestStatus.SUCCESS, "");
        // check to see if location exists in db
        Location dbLoc = getLocation(location.getLatitude(), location.getLongitude());
        if (dbLoc == null) {
            // save to db
            boolean result = locationDs.saveLocation(location);
            if (!result) {
                restStatus.setStatus(RestStatus.FAILURE);
                restStatus.setMessage("Insertion Failed");
            }
        }

        return restStatus;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/inauth/initialLoad", headers = "accept=application/json")
    @ResponseBody
    public RestStatus initialLoad(HttpServletRequest request, HttpServletResponse response, @RequestBody String body)
            throws Exception {
        
        RestStatus restStatus = new RestStatus(RestStatus.SUCCESS, "");
        boolean result = inAuth.initialLoad();
        if (!result) {
            restStatus.setStatus(RestStatus.FAILURE);
            restStatus.setMessage("Insertion Failed");
        }

        return restStatus;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/inauth/calcAllLocations", headers = "accept=application/json")
    @ResponseBody
    public RestStatus calcAllLocations(HttpServletRequest request, HttpServletResponse response, @RequestBody String body)
            throws Exception {
        
        RestStatus restStatus = new RestStatus(RestStatus.SUCCESS, "");
        // call the method used in REST endpoint
        List<Location> locations = getLocations(request, response);
        inAuth.calcAllLocations(locations);
        return restStatus;
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
