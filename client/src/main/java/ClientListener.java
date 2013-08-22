import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: win7
 * Date: 16.08.13
 * Time: 20:57
 * To change this template use File | Settings | File Templates.
 */
public class ClientListener extends UntypedActor{

    final LoggingAdapter log = Logging
            .getLogger(getContext().system(), getSelf());

    @Override
    public void onReceive(Object o) throws Exception {
       log.info("ClientListener.onReceive: {}", ((String)o).trim());
    }
}
