package org.jaffamq;


import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CommandTest {

    @Test
    public void shouldReturnEmptyCommand(){

        //  when
        Command space1 = Command.forName("");
        Command space2 = Command.forName(" ");

        //  then
        assertThat(space1, is(Command._EMPTY));
        assertThat(space2, is(Command._EMPTY));
    }

    @Test
    public void shouldReturnUnknownCommandForUnrecognizedCommand(){

        //  when
        Command unknown1 = Command.forName("ThisShouldNotExist");

        //  then
        assertThat(unknown1, is(Command._UNKNOWN));

    }

    @Test
    public void shouldRecognizedStompCommands(){

        //  when
        Command send = Command.forName("SEND");
        Command subscribe = Command.forName("SUBSCRIBE");
        Command unsubscribe = Command.forName("UNSUBSCRIBE");
        Command begin = Command.forName("BEGIN");
        Command commit = Command.forName("COMMIT");
        Command abort = Command.forName("ABORT");
        Command disconnect = Command.forName("DISCONNECT");
        Command connect = Command.forName("CONNECT");
        Command message = Command.forName("MESSAGE");
        Command receipt = Command.forName("RECEIPT");
        Command connected = Command.forName("CONNECTED");
        Command error = Command.forName("ERROR");

        //  then
        assertThat(send, is(Command.SEND));
        assertThat(subscribe, is(Command.SUBSCRIBE));
        assertThat(unsubscribe, is(Command.UNSUBSCRIBE));
        assertThat(begin, is(Command.BEGIN));
        assertThat(commit, is(Command.COMMIT));
        assertThat(abort, is(Command.ABORT));
        assertThat(disconnect, is(Command.DISCONNECT));
        assertThat(connect, is(Command.CONNECT));
        assertThat(message, is(Command.MESSAGE));
        assertThat(receipt, is(Command.RECEIPT));
        assertThat(connected, is(Command.CONNECTED));
        assertThat(error, is(Command.ERROR));

    }
}
