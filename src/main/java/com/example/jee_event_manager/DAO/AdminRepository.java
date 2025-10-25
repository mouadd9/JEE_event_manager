package com.example.jee_event_manager.DAO;

import com.example.jee_event_manager.model.Admin;

import java.util.List;

public interface AdminRepository extends UtilisateurRepository {
    Admin saveAdmin(Admin admin);
    List<Admin> findAllAdmins();
}
