package ca.mta.iottestbed.arbiter;

import ca.mta.iottestbed.network.Device;

/**
 * Manages access control policies.
 * 
 * @author Hayden Walker
 * @version 2023-07-17
 */
public interface Arbiter extends Device {
    /**
     * Authorize an exchange between two devices.
     * 
     * @param from Device sending the message.
     * @param to Device receiving the message.
     * @param command Message to authorize.
     */
    public boolean auth(Device from, Device to, String command);
}
