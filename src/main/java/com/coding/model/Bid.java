package com.coding.model;

import java.sql.Timestamp;
import java.text.ParseException;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Bid {
    private static final String TIMESTAMP_FORMAT = "yyyyMMddHHmmss";
    private static final String[] TIMESTAMP_PATTERNS = { TIMESTAMP_FORMAT };
    private static final DateTimeFormatter utcFmt = ISODateTimeFormat.dateTime().withZoneUTC();

    private String sourceId;
    private String source;
    private float bid;
    private long updatedAt;

    public Bid() {
    }

    public Bid(String sourceId, String source, float bid, Timestamp updatedAtTimestamp) {
        this.sourceId = sourceId;
        this.source = source;
        this.bid = bid;
        this.updatedAt = Long.valueOf(DateFormatUtils.format(updatedAtTimestamp, TIMESTAMP_FORMAT));
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public float getBid() {
        return bid;
    }

    public void setBid(float bid) {
        this.bid = bid;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @JsonIgnore
    public String getUpdateAtStr() {
        return utcFmt.print(updatedAt);        
    }

    @JsonIgnore
    public Timestamp getUpdatedAtTimestamp() throws ParseException {
        return new Timestamp(DateUtils.parseDate(Long.toString(updatedAt), TIMESTAMP_PATTERNS).getTime());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Float.floatToIntBits(bid);
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        result = prime * result + ((sourceId == null) ? 0 : sourceId.hashCode());
        result = prime * result + (int) (updatedAt ^ (updatedAt >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Bid other = (Bid) obj;
        if (Float.floatToIntBits(bid) != Float.floatToIntBits(other.bid))
            return false;
        if (source == null) {
            if (other.source != null)
                return false;
        } else if (!source.equals(other.source))
            return false;
        if (sourceId == null) {
            if (other.sourceId != null)
                return false;
        } else if (!sourceId.equals(other.sourceId))
            return false;
        if (updatedAt != other.updatedAt)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Bid [sourceId=" + sourceId + ", source=" + source + ", bid=" + bid + ", updatedAt=" + updatedAt + "]";
    }

}
