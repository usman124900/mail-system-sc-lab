import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The MailServer receives mail items from the clients and stores them
 * until the intended recipient asks for them. One server is shared by
 * all clients. User names are not case-sensitive ("Ali" and "ali" are
 * the same mailbox).
 */
public class MailServer
{
    // Storage for the mail waiting on the server.
    // Key = user name (lower case), value = that user's waiting mail.
    private final HashMap<String, List<MailItem>> items;

    /** Construct a mail server. */
    public MailServer()
    {
        items = new HashMap<>();
    }

    /**
     * Return how many mail items are waiting for a user.
     * @param who The user to check for.
     * @return How many items are waiting.
     */
    public int howManyMailItems(String who)
    {
        List<MailItem> mail = items.get(normalise(who));
        if (mail == null) {
            return 0;
        }
        return mail.size();
    }

    /**
     * Return the next mail item for a user, or null if there are none.
     * The item is removed from the server once it has been handed over.
     * @param who The user requesting their next item.
     * @return The user's next item, or null.
     */
    public MailItem getNextMailItem(String who)
    {
        List<MailItem> mail = items.get(normalise(who));
        if (mail == null || mail.isEmpty()) {
            return null;
        }
        return mail.remove(0);
    }

    /**
     * Add the given mail item to the recipient's message list.
     * @param item The mail item to be stored on the server.
     */
    public void post(MailItem item)
    {
        String recipient = normalise(item.getTo());
        List<MailItem> mail = items.get(recipient);
        if (mail == null) {
            mail = new ArrayList<>();
            items.put(recipient, mail);
        }
        mail.add(item);
    }

    /** Make user names comparable: trimmed and lower case. */
    private String normalise(String name)
    {
        return name.trim().toLowerCase();
    }
}
