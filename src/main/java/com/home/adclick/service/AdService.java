package com.home.adclick.service;

import com.home.adclick.model.Advertisement;
import com.home.adclick.model.ClickedEvent;
import com.home.adclick.model.InstallEvent;

public interface AdService {

    void addAdvt(Advertisement advertisement);

    boolean addClickedEvent(ClickedEvent clickedEvent);

    boolean addInstallEvent(InstallEvent installEvent);

    Object search(String fromTime,String toTime,String params);
}
