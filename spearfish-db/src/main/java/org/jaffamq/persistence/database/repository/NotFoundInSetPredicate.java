package org.jaffamq.persistence.database.repository;

import com.google.common.base.Predicate;

import java.util.Set;

/**
 * Created by urwisy on 2014-04-22.
 */
public class NotFoundInSetPredicate implements Predicate<Identifiable>{
    private final Set<? extends Identifiable> collection;

    public NotFoundInSetPredicate(Set<? extends Identifiable> collection) {
        this.collection = collection;
    }

    @Override
    public boolean apply(Identifiable input) {

        for(Identifiable id:collection){

            if(input == null){
                continue;
            }

            if(input.equals(id)){
                return false;
            }
        }

        return true;
    }
}
