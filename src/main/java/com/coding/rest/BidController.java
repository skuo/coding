package com.coding.rest;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.coding.entity.Bid;
import com.coding.service.BidService;

@Controller
public class BidController {
    private static final Log log = LogFactory.getLog(BidController.class);

    @Autowired
    BidService bidService;

    @RequestMapping(method = RequestMethod.GET, value = "/version", headers = "accept=application/json")
    @ResponseBody
    public String getVersion(HttpServletRequest request, HttpServletResponse response) {
        // log.info("getVersion");
        StringBuilder sb = new StringBuilder();
        sb.append("{\"java.version\":\"" + System.getProperty("java.version") + "\"}");
        log.info(sb.toString());
        return sb.toString();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/bids/{sourceId}/source/{source}", headers = "accept=application/json")
    @ResponseBody
    /**
     * Return bid in json format and status code of 200, 404 and 500.
     * 
     * @param sourceId
     *            original source id
     * @param source
     *            source identifier
     * @param response
     *            HttpServletReponse
     * @return Bid in json format
     */
    public Bid getBid(@PathVariable String sourceId, @PathVariable String source, HttpServletRequest request,
            HttpServletResponse response) {
        log.info("getBid: sourceId=" + sourceId + ", source=" + source + ", accept=" + request.getHeader("Content-Type")
                + ", Authorization=" + request.getHeader("Authorization"));
        Bid bid = null;
        bid = bidService.getBid(sourceId, source);
        if (bid == null)
            // use setStatus() instead of sendError() because it allows
            // ResponseBody to be written
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        response.setHeader("Approved", "true");
        return bid;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/bids/{sourceId}/source/{source}", headers = "accept=application/json")
    @ResponseBody
    /**
     * Put bid. Returns BidStatus with status code of 200 or 500.
     * 
     * @param sourceId
     *            original source id
     * @param source
     *            source identifier
     * @param bid
     *            Bid
     * @param response
     *            HttpServletReponse
     * @return boolean
     */
    public boolean putBid(@PathVariable String sourceId, @PathVariable String source, @RequestBody Bid bid,
            HttpServletResponse response) throws SQLException {
        log.info("putBid: sourceId=" + sourceId + ", source=" + source + ", bid=" + bid.toString());
        bid.setSourceId(sourceId);
        bid.setSource(source);
        boolean status = bidService.saveBid(bid);
        if (!status) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return status;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/bids/{sourceId}", headers = "accept=application/json")
    @ResponseBody
    /**
     * Return bid in json format and status code of 200, 404 and 500.
     * 
     * @param sourceId
     *            original source id
     * @param source
     *            source identifier
     * @param id
     *            optional request param, not used.
     * @param response
     *            HttpServletResponse
     * @return BidStatus in json format
     * @throws SQLException
     *             returns 500 status with error html page
     * @throws IOException
     *             returns 500 status with error html page
     */
    public Bid getBidByPathAndParams(@PathVariable String sourceId, @RequestParam("source") String source,
            @RequestParam(value = "id", required = false) Long id, HttpServletResponse response) throws IOException {
        log.info("getBidByPathAndParams: sourceId=" + sourceId + ", source=" + source + ", id=" + id);
        Bid bid = bidService.getBid(sourceId, source);
        if (bid == null)
            // use setStatus() instead of sendError() because it allows
            // ResponseBody to be written
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return bid;
    }

}
