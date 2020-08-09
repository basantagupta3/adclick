package com.home.adclick.memdb;

import com.home.adclick.model.Advertisement;
import com.home.adclick.model.ClickedEvent;
import com.home.adclick.model.InstallEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.ArrayList;
import java.util.List;

@Component
@ApplicationScope
public class Event {
    private List<Advertisement> advList=null;
    private List<ClickedEvent> clickedList=null;
    private List<InstallEvent> installList=null;
    private static Event SINGLE_INSTANCE = null;
    public Event() {
        advList=new ArrayList<>();
        clickedList=new ArrayList<>();
        installList=new ArrayList<>();
    }
    public static Event getInstance() {
        if (SINGLE_INSTANCE == null) {
            synchronized (Event.class) {
                if (SINGLE_INSTANCE == null) {
                    SINGLE_INSTANCE = new Event();
                }
            }
        }
        return SINGLE_INSTANCE;
    }


    public Event applicationScopedBean() {
        return Event.getInstance();
    }

    public List<Advertisement> getAdvList() {
        return advList;
    }

    public void setAdvList(List<Advertisement> advList) {
        this.advList = advList;
    }

    public List<ClickedEvent> getClickedList() {
        return clickedList;
    }

    public void setClickedList(List<ClickedEvent> clickedList) {
        this.clickedList = clickedList;
    }

    public List<InstallEvent> getInstallList() {
        return installList;
    }

    public void setInstallList(List<InstallEvent> installList) {
        this.installList = installList;
    }
}
