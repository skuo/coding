package com.coding.inauth.service.data;

public class DataServiceException extends Exception {
    private static final long serialVersionUID = 3407983299922276803L;

    /**
     * Constructor for DataServiceException.
     * 
     * @param msg
     *            the detail message
     */
    public DataServiceException(String msg) {
        super(msg);
    }

    /**
     * Constructor for DataServiceException.
     * 
     * @param msg
     *            the detail message
     * @param cause
     *            the root cause 
     */
    public DataServiceException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
