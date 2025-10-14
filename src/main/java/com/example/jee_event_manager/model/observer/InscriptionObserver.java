package com.example.jee_event_manager.model.observer;

import com.example.jee_event_manager.model.Inscription;

public interface InscriptionObserver {
    void onInscriptionCreated(Inscription inscription);
}
