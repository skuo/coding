package com.coding.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.coding.model.Bid;
import com.coding.model.RestStatus;

@Component
public class BidDao {
	private final String GET_QUERY = "select bid, updated_at from bid where source_id=? and source=?";
    private final String INSERT_QUERY = "insert into bid (source_id, source, bid, updated_at) values(?, ?, ?, ?)";
    private final String UPDATE_QUERY = "update bid set bid=?, updated_at=? where source_id=? and source=?";
    
    private final String INSERT_ERROR = "Error when inserting bid";
    private final String INSERT_NONE_ERROR = "Inserted zero bid";
    private final String UPDATE_ERROR = "Error when updating bid";
    private final String UPDATE_NONE_ERROR = "Updated zero bid";
    
	@Autowired
	DataSource dataSource;

	/**
	 * Get bid which is null if it does not exist.
	 * Release connection back to pool but close statement.
	 * @throws SQLException 
	 */
	public Bid get(String sourceId, String source) throws SQLException{
		Bid bid = null;
		Connection c = null;
		PreparedStatement s = null;
		try {
			c = dataSource.getConnection();
			if (c != null) {
			    s = c.prepareStatement(GET_QUERY);
				s.setString(1, sourceId);
				s.setString(2, source);
				ResultSet rs = s.executeQuery();
				if (rs.next()) {
					Float b = rs.getFloat(1);
					Timestamp ts = rs.getTimestamp(2);
					bid = new Bid(sourceId, source, b, ts);
				}
			}
		} catch (SQLException sqle) {
			throw sqle;
		} finally {
			close(c);
			close(s);
		}
		return bid;
	}

    /**
     * Call insert() and if failed call update().
     */
	public RestStatus put(Bid bid) throws SQLException {
	    if (get(bid.getSourceId(), bid.getSource()) == null)
	        return insert(bid);
	    else 
	        return update(bid);
	}
	
    /**
     * Insert bid.
     * Release connection back to pool but close statement.
     */
	public RestStatus insert(Bid bid){
        Connection c = null;
        PreparedStatement s = null;
        RestStatus bidStatus = new RestStatus(RestStatus.SUCCESS, "");
        try {
            c = dataSource.getConnection();
            if (c != null) {
                s = c.prepareStatement(INSERT_QUERY);
                s.setString(1, bid.getSourceId());
                s.setString(2, bid.getSource());
                s.setFloat(3, bid.getBid());
                s.setTimestamp(4, bid.getUpdatedAtTimestamp());
                int i = s.executeUpdate();
                if (i == 0) {
                    bidStatus.setStatus(RestStatus.FAILURE);
                    bidStatus.setMessage(INSERT_NONE_ERROR);                    
                }
            }
        } catch (SQLException sqle) {
            bidStatus.setStatus(RestStatus.FAILURE);
            bidStatus.setMessage(INSERT_ERROR);
        } catch (ParseException pe) {
            bidStatus.setStatus(RestStatus.FAILURE);
            bidStatus.setMessage(INSERT_ERROR);
        } finally {
            close(c);
            close(s);
        }
        return bidStatus;
	}
	
	/**
     * Update bid.
     * Release connection back to pool but close statement.
     */
    public RestStatus update(Bid bid){
        Connection c = null;
        PreparedStatement s = null;
        RestStatus bidStatus = new RestStatus(RestStatus.SUCCESS, "");
        try {
            c = dataSource.getConnection();
            if (c != null) {
                s = c.prepareStatement(UPDATE_QUERY);
                s.setFloat(1, bid.getBid());
                s.setTimestamp(2, bid.getUpdatedAtTimestamp());
                s.setString(3, bid.getSourceId());
                s.setString(4, bid.getSource());
                int i = s.executeUpdate();
                if (i == 0) {
                    bidStatus.setStatus(RestStatus.FAILURE);
                    bidStatus.setMessage(UPDATE_NONE_ERROR);                    
                }
            }
        } catch (SQLException sqle) {
            bidStatus.setStatus(RestStatus.FAILURE);
            bidStatus.setMessage(UPDATE_ERROR);
        } catch (ParseException pe) {
            bidStatus.setStatus(RestStatus.FAILURE);
            bidStatus.setMessage(UPDATE_ERROR);
        } finally {
            close(c);
            close(s);
        }
        return bidStatus;
    }
    
	private void close(Connection c) {
		try {
			if (c != null)
				c.close();
		} catch (SQLException sqle) {
			// do nothing
		}
	}

	private void close(Statement s) {
		try {
			if (s != null)
				s.close();
		} catch (SQLException sqle) {
			// do nothing
		}
	}
}
