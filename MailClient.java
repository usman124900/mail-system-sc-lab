/**
 * A MailClient represents one user of the mail system. It sends mail to
 * other users and receives mail, always going through the shared MailServer.
 */
public class MailClient
{
    // The server used for sending and receiving.
    private final MailServer server;
    // The user running this client.
    private final String user;

    /**
     * Create a mail client run by user and attached to the given server.
     * @param server The mail server this client talks to.
     * @param user The user name of the person using this client.
     */
    public MailClient(MailServer server, String user)
    {
        this.server = server;
        this.user = user;
    }

    /** @return The user name of this client. */
    public String getUser()
    {
        return user;
    }

    /**
     * Return the next mail item (if any) for this user.
     * @return The next mail item, or null if there is none.
     */
    public MailItem getNextMailItem()
    {
        return server.getNextMailItem(user);
    }

    /**
     * Print the next mail item (if any) for this user to the text terminal.
     */
    public void printNextMailItem()
    {
        MailItem item = server.getNextMailItem(user);
        if (item == null) {
            System.out.println("No new mail.");
        }
        else {
            item.print();
        }
    }

    /**
     * Send a mail from this user to the given recipient via the server.
     * @param to The intended recipient.
     * @param subject The subject of the mail.
     * @param message The text of the message to be sent.
     */
    public void sendMailItem(String to, String subject, String message)
    {
        MailItem item = new MailItem(user, to, subject, message);
        server.post(item);
    }

    /** @return How many mail items are waiting for this user. */
    public int howManyMailItems()
    {
        return server.howManyMailItems(user);
    }
}
