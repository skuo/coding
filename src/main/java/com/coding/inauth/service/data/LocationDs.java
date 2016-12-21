package com.coding.inauth.service.data;

import java.util.Collection;
import java.util.List;

import com.coding.inauth.model.Location;

public interface LocationDs {

    /**
     * Get batch size
     */
    public int getBatchSize();

    /**
     * Set batch size
     * 
     * @param batchSize
     */
    public void setBatchSize(int batchSize);

    /**
     * Save a collection of Locations
     * 
     * @param locations
     * @return
     * @throws DataServiceException
     */
    public boolean saveLocations(Collection<Location> locations) throws DataServiceException;

    /**
     * Save a Locations
     * 
     * @param location
     * @return
     * @throws DataServiceException
     */
    public boolean saveLocation(Location location) throws DataServiceException;

    /**
     * Find a location by its latitude and longitude
     * 
     * @param latitude
     * @param longitude
     * @return
     */
    public Location find(float latitude, float longitude);

    /**
     * Find all locations
     * 
     * @param runCycle
     * @return
     */
    public List<Location> getLocations();

}
