package jlo.ioe.messaging;

import static jlo.ioe.messaging.MessageService.*;
import jlo.ioe.util.F;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * Copyright © 2007 imaginaryday.com (jlo)<br>
 * User: jlowens<br>
 * Date: Apr 4, 2007<br>
 * Time: 6:27:42 PM<br>
 */
public interface Message {
	Object  getSender();
	List<? extends F.lambda> match(MsgMap msgMap,
	                                     MsgFilterMap msgFilterMap, 
	                                     SenderMap senderMap,
	                                     SenderFilterMap senderFilterMap);

	void execute(Executor threads, F.lambda f);
}
