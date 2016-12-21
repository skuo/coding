package com.coding.inauth.service.data.spring;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.coding.inauth.model.Location;
import com.coding.inauth.service.data.DataServiceException;
import com.coding.inauth.service.data.LocationDs;

public class JdbcLocationDs implements LocationDs {
    private static String SELECT_CLAUSE = "select id, latitude, longitude ";

    private NamedParameterJdbcTemplate namedParamJdbcTemplate;
    private int batchSize = 200;

    @Override
    public int getBatchSize() {
        return batchSize;
    }

    @Override
    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    private static final class LocationMapper implements RowMapper<Location> {
        public Location mapRow(ResultSet rs, int rowNum) throws SQLException {
            Location location = new Location();
            location.setId(rs.getLong("id"));
            location.setLatitude(rs.getFloat("latitude"));
            location.setLongitude(rs.getFloat("longitude"));
            return location;
        }        
    }

    public void setDataSource(DataSource dataSource) {
        this.namedParamJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public boolean saveLocations(Collection<Location> locations) throws DataServiceException {
        List<Location> locationBatch = new ArrayList<Location>();
        int i = 0;
        int start = 0;
        int end = 0;
        boolean result = true;
        Iterator<Location> itr = locations.iterator();
        while (itr.hasNext()) {
            if (i >= batchSize) {
                // batch is full, save it
                start = end + 1;
                end += i;
                result = batchSaveLocations(locationBatch);
                if (!result)
                    throw new DataServiceException("Failed to save Locations for batch starting at " + start + " and end at " + end);
                // reset PlugMonthlyRevBatch and i
                locationBatch.clear();
                i = 0;
            }
            // add to batch and update i
            locationBatch.add(itr.next());
            i++;
        }
        // do the left over batch
        if (locationBatch.size() > 0) {
            start = end + 1;
            end += locationBatch.size();
            result = batchSaveLocations(locationBatch);
            if (!result)
                throw new DataServiceException("Failed to save Locations for batch starting at " + start + " and end at " + end);
        }
        return true;
    }

    private boolean batchSaveLocations(List<Location> locations) {
        String sql = "insert into location (id, latitude, longitude) "
                + "values (:id , :latitude, :longitude)";

        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(locations.toArray());
        int[] results = namedParamJdbcTemplate.batchUpdate( sql, batch);
        
        // determine whether every update succeeds
        int numOfSuccesses = 0;
        for (int result : results)
            numOfSuccesses += result;
        return numOfSuccesses == results.length;
    }

    @Override
    public Location find(float latitude, float longitude) {
        String sql = SELECT_CLAUSE
                + "from location where latitude = :latitude and longitude = :longitude";
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        paramMap.addValue("latitude", latitude);
        paramMap.addValue("longitude", longitude);
        return namedParamJdbcTemplate.queryForObject(sql, paramMap, new LocationMapper());
    }

    @Override
    public List<Location> getLocations() {
        String sql = SELECT_CLAUSE
                + "from location ";
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        return namedParamJdbcTemplate.query(sql, paramMap, new LocationMapper());
    }

    @Override
    public boolean saveLocation(Location location) throws DataServiceException {
        String sql = "insert into location (latitude, longitude) values (:latitude, :longitude)";
        
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        paramMap.addValue("latitude", location.getLatitude());
        paramMap.addValue("longitude", location.getLongitude());
        int result = namedParamJdbcTemplate.update(sql, paramMap);
        return (result == 1);
    }
    
}
