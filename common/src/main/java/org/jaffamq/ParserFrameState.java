package org.jaffamq;

import akka.actor.UntypedActor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: win7
 * Date: 17.08.13
 * Time: 19:29
 * To change this template use File | Settings | File Templates.
 */
public abstract class ParserFrameState extends UntypedActor{


    protected Frame.FrameParsingState frameParsingState = Frame.FrameParsingState.READING_COMMAND;

    //protected SessionState sessionState = SessionState.WAITING_FOR_CONNECTION;

    //  move this to frame
    protected Command currentFrameCommand = null;

    protected SessionState sessionState = SessionState.WAITING_FOR_CONNECTION;

    protected Map<String, String> headers = new HashMap<String, String>();

    private void addHeader(String line){

        int ind = line.indexOf( ':' );
        String k = line.substring( 0, ind );
        String v = Frame.decodeHeaderValue(line.substring(ind + 1, line.length()));

        //  only the first occurence of key is important, see spec: "Repeated Header Entries"
        if(!headers.containsKey(k)){
            headers.put(k,v);
        }
    }



    protected void parseLine(String line){

        switch(frameParsingState){
            case READING_COMMAND:
                currentFrameCommand = Command.forName(line.trim());
                setState(Frame.FrameParsingState.READING_HEADERS);
                break;

            case READING_HEADERS:
                if(line.trim().length()==0){
                    setState(Frame.FrameParsingState.READING_BODY);
                    return;
                }

                addHeader(line);
                break;

            case READING_BODY:
                if(line.equals("\000\n")){
                    setState(Frame.FrameParsingState.FINISHED_PARSING);
                    //setState(Frame.FrameParsingState.READING_COMMAND);
                    return;
                }
                break;

            case FINISHED_PARSING:
                throw new IllegalStateException("Content not allowed after finished parsing frame.");
            default:
                throw new IllegalStateException("Unsupported parsing state: " + frameParsingState);
        }

    }

    protected void setState(Frame.FrameParsingState s) {

        if (frameParsingState != s) {
            transition(frameParsingState, s);

            if(s == Frame.FrameParsingState.FINISHED_PARSING){
                /*
                    if the next state will be FINISHED_PARSING after serving the transition in the child class
                    we switch directly to READING_COMMAND to be ready for next command parsing.
                    WARNING: we probably should somehow block receiving next command till the current one is
                    confirmed as executed.
                 */
                frameParsingState = Frame.FrameParsingState.READING_COMMAND;
            }
            else{
                frameParsingState = s;
            }
        }
    }


    abstract protected void transition(Frame.FrameParsingState old, Frame.FrameParsingState next);


    protected enum SessionState{
        WAITING_FOR_CONNECTION,
        CONNECTED,
        SLEEPING_BEFORE_DISCONNECT

    }



}
