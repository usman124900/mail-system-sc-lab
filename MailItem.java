/**
 * A MailItem stores the contents of one e-mail:
 * who sent it, who it is for, its subject and the message text.
 * It is created inside MailClient and stored/passed on by MailServer.
 */
public class MailItem
{
    // The sender of the item.
    private final String from;
    // The intended recipient.
    private final String to;
    // The subject line of the mail.
    private final String subject;
    // The text of the message.
    private final String message;

    /**
     * Create a mail item from sender to the given recipient,
     * containing the given subject and message.
     * @param from The sender of this item.
     * @param to The intended recipient of this item.
     * @param subject The subject line of this item.
     * @param message The text of the message to be sent.
     */
    public MailItem(String from, String to, String subject, String message)
    {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.message = message;
    }

    /** @return The sender of this mail item. */
    public String getFrom()
    {
        return from;
    }

    /** @return The intended recipient of this mail item. */
    public String getTo()
    {
        return to;
    }

    /** @return The subject of this mail item. */
    public String getSubject()
    {
        return subject;
    }

    /** @return The text of the message in this mail item. */
    public String getMessage()
    {
        return message;
    }

    /** Print this mail item to the text terminal. */
    public void print()
    {
        System.out.println(toString());
    }

    /** @return The whole mail formatted as readable text. */
    @Override
    public String toString()
    {
        return "From:    " + from + "\n"
             + "To:      " + to + "\n"
             + "Subject: " + subject + "\n"
             + "------------------------------\n"
             + message;
    }
}
