package ca.mta.iottestbed.arbiter;

/**
 * Manages access control policies.
 * 
 * @author Hayden Walker
 * @version 2023-07-17
 */
public interface Arbiter {
    /**
     * Authorize an exchange between two devices.
     * 
     * @param from Device sending the message.
     * @param to Device receiving the message.
     * @param command Message to authorize.
     */
    public boolean isAuthorized(String senderID, String recipientID, String command);
}
