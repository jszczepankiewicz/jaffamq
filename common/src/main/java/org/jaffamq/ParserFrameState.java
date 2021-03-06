package org.jaffamq;

import akka.actor.UntypedActor;
import akka.util.ByteString;

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

    //  move this to frame
    protected Command currentFrameCommand = null;

    protected SessionState sessionState = SessionState.WAITING_FOR_CONNECTION;

    protected Map<String, String> headers;

    //  TODO: change it to ByteString
    protected StringBuilder currentFrameBody;

    public ParserFrameState(){
        prepareForNewFrame();
    }

    protected String getCurrentFrameBody(){
        return currentFrameBody.toString();
    }

    protected String getRequiredHeaderValue(String requiredHeader, Errors.Code errorCode) throws RequestValidationFailedException{

        String headerValue = headers.get(requiredHeader);

        /*
            Please note that according to the 1.2 spec we can not use trim()
         */
        if(headerValue == null || headerValue.length() == 0){
            throw new RequestValidationFailedException(errorCode);
        }

        return headerValue;
    }

    private void addHeader(String line){
        /*
            Unfortunatelly spec (1.2) do not precise what EOL should be exactly.
            It may be CR NL or NL. We need to test it before trimming line.
            Please note that according to spec (1.2) we SHOULD NOT use trim()!.
         */
        int lengthOfEOL = 1;

        if(line.endsWith("\r\n")){
            lengthOfEOL = 2;
        }

        int ind = line.indexOf( ':' );
        String k = line.substring( 0, ind );
        String v = Frame.decodeHeaderValue(line.substring(ind + 1, line.length() - lengthOfEOL));

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
                if("\000\n".equals(line)){
                    setState(Frame.FrameParsingState.FINISHED_PARSING);
                    return;
                }
                currentFrameBody.append(line);
                break;

            case FINISHED_PARSING:
                throw new IllegalStateException("Content not allowed after finished parsing frame.");
            default:
                throw new IllegalStateException("Unsupported parsing state: " + frameParsingState);
        }

    }

    /**
     * Reinitialize content before parsing next frame.
     */
    private void prepareForNewFrame(){
        currentFrameBody = new StringBuilder();
        headers = new HashMap<String, String>();
    }

    protected void setState(Frame.FrameParsingState s) {

        if (frameParsingState != s) {
            //  here we should do all things with content
            transition(frameParsingState, s);


            if(s == Frame.FrameParsingState.FINISHED_PARSING){
                /*
                    if the next state will be FINISHED_PARSING after serving the transition in the child class
                    we switch directly to READING_COMMAND to be ready for next command parsing.
                    WARNING: we probably should somehow block receiving next command till the current one is
                    confirmed as executed.
                 */
                frameParsingState = Frame.FrameParsingState.READING_COMMAND;

                //  last moment before reseting the parser state
                prepareForNewFrame();
                //  all content of the current frame is reinitialized
            }
            else{
                frameParsingState = s;
            }
        }
    }


    protected abstract void transition(Frame.FrameParsingState old, Frame.FrameParsingState next);

    protected enum SessionState{
        WAITING_FOR_CONNECTION,
        CONNECTED,
        SLEEPING_BEFORE_DISCONNECT

    }

}
