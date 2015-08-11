package com.atsamour.habitatweave.dao;

import java.util.List;

import org.hibernate.Session;

import com.atsamour.habitatweave.models.WorkToDo;
import java.util.Calendar;
/**
 *
 * @author AlariC
 */
public class WorkToDoDAO {
    private final Session session;
    
    public WorkToDoDAO(Session s) {
        session = s;
    }

    public List<WorkToDo> getWorksToDo(Calendar time) {
        Calendar timeEnd;
        timeEnd = (Calendar) time.clone();
        timeEnd.add(Calendar.SECOND, 59);
        @SuppressWarnings("unchecked")
        List<WorkToDo> worksToDo = session
                .createQuery("from WorkToDo where done IS FALSE AND dateToDo >=? and dateToDo <?")
                //.createQuery("from WorkToDo")
                .setParameter(0, time)
                .setParameter(1, timeEnd)
                .list();
        return worksToDo;
    }

    public List<WorkToDo> getWorksToDoByUserId(int user_id, Calendar time) {
        Calendar timeEnd;
        timeEnd = (Calendar) time.clone();
        timeEnd.add(Calendar.SECOND, 59);
        @SuppressWarnings("unchecked")
        List<WorkToDo> worksToDo = session
                .createQuery("from WorkToDo where user_id=? AND done IS FALSE AND dateToDo >=? and dateToDo <?")
                //.createQuery("from WorkToDo")
                .setInteger(0, user_id)
                .setParameter(1, time)
                .setParameter(2, timeEnd)
                .list();
        return worksToDo;
    }
    
    public List<WorkToDo> getAllPendingWorks(Calendar time) {
        @SuppressWarnings("unchecked")
        List<WorkToDo> worksToDo = session
                .createQuery("from WorkToDo where done IS FALSE AND dateToDo >=?")
                //.createQuery("from WorkToDo")
                .setParameter(0, time)
                //.setParameter(1, timeEnd)
                .list();
        return worksToDo;
    }

    public List<WorkToDo> getWorksOld(Calendar time) {
        @SuppressWarnings("unchecked")
        List<WorkToDo> worksToDo = session
                .createQuery("from WorkToDo where done IS FALSE AND dateToDo < ?")
                //.createQuery("from WorkToDo")
                .setParameter(0, time)
                //.setParameter(1, timeEnd)
                .list();
        return worksToDo;
    }
    
    public void save(WorkToDo w) {
        session.save(w);
    }
    
}
