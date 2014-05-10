package org.jaffamq.persistence.database.actor;

/**
 * Created by urwisy on 2014-05-06.
 */
public class GetPagedListRequest {

    private final int limit;
    private final int offset;

    public GetPagedListRequest(int limit, int offset) {
        this.limit = limit;
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public int getOffset() {
        return offset;
    }
}
