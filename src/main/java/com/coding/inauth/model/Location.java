package com.coding.inauth.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Location {
    @JsonIgnore
    private Long id;
    @JsonProperty("latitude")
    private float latitude;
    @JsonProperty("longitude")
    private float longitude;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public float getLatitude() {
        return latitude;
    }
    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }
    public float getLongitude() {
        return longitude;
    }
    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
    
    public Location() {
        
    }
    
    public Location(float latitude, float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    public boolean validate(Location loc) {
        // The latitude must be a number between -90 and 90 and the longitude between -180 and 180.
        boolean result = true;
        if (loc.getLatitude() < -90f || loc.getLatitude() > 90f)
            result = false;
        if (loc.getLongitude() < -180f || loc.longitude > 180f)
            result = false;
        return result;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + Float.floatToIntBits(latitude);
        result = prime * result + Float.floatToIntBits(longitude);
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
        Location other = (Location) obj;

        if (Float.floatToIntBits(latitude) != Float.floatToIntBits(other.latitude))
            return false;
        if (Float.floatToIntBits(longitude) != Float.floatToIntBits(other.longitude))
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "Location [id=" + id + ", latitude=" + latitude + ", longitude=" + longitude + "]";
    }
}
