package jlo.ioe.messaging;

/**
 * Created by IntelliJ IDEA.
 * User: jlowens
 * Date: Apr 4, 2007
 * Time: 6:24:43 PM
 */
public class MessageService {
	public static final MessageService _instance = new MessageService();

	private MessageService() {

	}

	// add messages to a thread safe priority queue
	// use a thread pool to distribute messages to listeners
	// drop messages that do not have listeners

	public MessageService publish(Message m) {
		return this;
	}

	public void subscribe(Object sender, Class msgClass) {

	}

	public void subscribe(Class msgClass) {

	}

	public static MessageService singleton() {
		return _instance;
	}
}
