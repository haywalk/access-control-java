package ca.mta.iottestbed.arbiter;

import java.util.HashSet;

/**
 * Arbiter to manage access policies.
 */
public class TableArbiter implements Arbiter {

    /**
     * Set of interactions that are allowed to take place. Stored as
     * Strings of the form senderID::_::recipientID::_::command.
     */
    private HashSet<String> allowed;

    /**
     * Create a new TableArbiter object.
     */
    public TableArbiter() {
        allowed = new HashSet<String>();
    }

    /**
     * Check if a sender is allowed to use a command on a recipient.
     * 
     * @param senderID ID of device sending command.
     * @param recipientID ID of device receiving command.
     * @param command Command.
     */
    @Override
    public boolean isAuthorized(String senderID, String recipientID, String command) {
        return allowed.contains(senderID + "::_::" + recipientID + "::_::" + command);
    }
    
    /**
     * Allow a device to use a command on another device.
     * 
     * @param senderID ID of device allowed to send command.
     * @param recipientID ID of device receiving command.
     * @param command Command.
     */
    public void allow(String senderID, String recipientID, String command) {
        allowed.add(getPermissionString(senderID, recipientID, command));
    }

    /**
     * Disallow a device to use a command on another device.
     * 
     * @param senderID ID of device not allowed to send command.
     * @param recipientID ID of device receiving command.
     * @param command Command.
     */
    public void disallow(String senderID, String recipientID, String command) {
        allowed.remove(getPermissionString(senderID, recipientID, command));
    }  

    /**
     * Build a string to store in the set of allowed interactions.
     * 
     * @param senderID ID of sender.
     * @param recipientID ID of recipient.
     * @param command Command.
     * @return String of the form {@code senderID::_::recipientID::_::command}.
     */
    private String getPermissionString(String senderID, String recipientID, String command) {
        return senderID + "::_::" + recipientID + "::_::" + command;
    }
}
