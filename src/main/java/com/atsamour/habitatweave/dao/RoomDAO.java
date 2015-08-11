package com.atsamour.habitatweave.dao;

import java.util.List;

import org.hibernate.Session;

import com.atsamour.habitatweave.models.Room;

public class RoomDAO {

    private final Session session;

    public RoomDAO(Session s) {
        session = s;
    }

    public List<Room> getAllRooms() {
        @SuppressWarnings("unchecked")
        List<Room> rooms = session.createQuery("from Room").list();
        return rooms;
    }
            
    public List<Room> getRoomsByUserId(int id) {
        @SuppressWarnings("unchecked")
        List<Room> rooms = session
                .createQuery("from Room where user_id=?").setInteger(0, id).list();
        return rooms;
    }

    public void insert(Room r) {//?????????????
        session.persist(r);
    }
}
