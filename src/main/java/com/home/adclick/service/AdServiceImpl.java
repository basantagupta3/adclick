package com.home.adclick.service;

import com.home.adclick.memdb.Event;
import com.home.adclick.model.Advertisement;
import com.home.adclick.model.ClickedEvent;
import com.home.adclick.model.InstallEvent;
import com.home.adclick.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.text.SimpleDateFormat;
import java.util.*;


import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
public class AdServiceImpl implements AdService{

    Date fromDate=null;
    Date toDate= null;

    @Autowired
    Event event;

    @Autowired
    public AdServiceImpl() {
    }



    @Override
    public void addAdvt(Advertisement advertisement) {

        System.out.println(advertisement.getTime());
        String time=advertisement.getTime();
        SimpleDateFormat formatDate=null;
        formatDate= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date=null;
        try {
            date = formatDate.parse(time);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        advertisement.setDate(date);

        event.applicationScopedBean().getAdvList().add(advertisement);

    }

    @Override
    public boolean addClickedEvent(ClickedEvent clickedEvent) {

        List<ClickedEvent> matchList=event.applicationScopedBean().getClickedList()
                .stream()
                .filter(t->t.getDeliveryId().equals(clickedEvent.getDeliveryId())).collect(toList());
        if(matchList.size() >0){
            return true;
        }else {
            String time=clickedEvent.getTime();
            SimpleDateFormat formatDate=null;
            formatDate= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date date=null;
            try {
                date = formatDate.parse(time);

            }catch (Exception ex){
                ex.printStackTrace();
            }
            clickedEvent.setDate(date);
            event.applicationScopedBean().getClickedList().add(clickedEvent);
            return false;
        }


    }

    @Override
    public boolean addInstallEvent(InstallEvent installEvent) {

        List<InstallEvent> matchList=event.applicationScopedBean().getInstallList()
                .stream()
                .filter(t->t.getInstallId().equals(installEvent.getInstallId())).collect(toList());
        if(matchList.size() >0){
            return true;
        }else {
            String time=installEvent.getTime();
            SimpleDateFormat formatDate=null;
            formatDate= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date date=null;
            try {
                date = formatDate.parse(time);

            }catch (Exception ex){
                ex.printStackTrace();
            }
            installEvent.setDate(date);
            event.applicationScopedBean().getInstallList().add(installEvent);
            return false;
        }
    }

    //
    @Override
    public Object search(String fromTime,String toTime,String params){


        Response response=new Response();
        Interval interval=new Interval();
        interval.setStart(fromTime);
        interval.setEnd(toTime);
        response.setInterval(interval);
        List<Advertisement> advList= Event.getInstance().applicationScopedBean().getAdvList();
        List<ClickedEvent> clickedEventList= Event.getInstance().applicationScopedBean().getClickedList();
        List<InstallEvent> installEventList= Event.getInstance().applicationScopedBean().getInstallList();


        String[] paramsArray=params.split(",");
        SimpleDateFormat formatDate;
        formatDate= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            fromDate = formatDate.parse(fromTime);
            toDate = formatDate.parse(toTime);
        }catch (Exception ex){
            ex.printStackTrace();
        }

        advList.forEach(t->System.out.println(t.getDate()));

        List<Advertisement> dellst=advList.stream().filter(t->t.getDate().after(fromDate) && t.getDate().before(toDate))
                .collect(toList());
        List<ClickedEvent> cllst=clickedEventList.stream().filter(t->t.getDate().after(fromDate) && t.getDate().before(toDate))
                .collect(toList());
        List<InstallEvent> inlst=installEventList.stream().filter(t->t.getDate().after(fromDate) && t.getDate().before(toDate))
                .collect(toList());

        if (paramsArray.length == 1 && paramsArray[0].equals("overall")) {
            //DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ");
        Statistics statistics= new Statistics();
        statistics.setDeliveries(dellst.size());

        List<String> collectDeliveyIdFromClicked= cllst.stream().map(t->t.getDeliveryId()).collect(toList());

        List<Advertisement> finalClickedList=dellst.stream().filter(t->{
            if(collectDeliveyIdFromClicked.contains(t.getDeliveryId())) {
                return true;
            }else{
                return false;
            }
        }).collect(toList());

        List<String> collectClickedIdFromInstall= inlst.stream().map(t->t.getClickId()).collect(toList());

        List<String> deliveryIdsForInstall=cllst.stream().filter(t->{
            if(collectClickedIdFromInstall.contains(t.getClickId())){
                return true;
            }else{
                return false;
            }
        }).collect(toList()).stream().map(t->t.getDeliveryId()).collect(toList());

        List<Advertisement> finalListForInstall=dellst.stream().filter(t->{

            if(deliveryIdsForInstall.contains(t.getDeliveryId())){
                return true;
            }else{
                return false;
            }
        }).collect(toList());
        statistics.setClicks(finalClickedList.size());
        statistics.setInstalls(finalListForInstall.size());
        response.setStats(statistics);

        }else if(!paramsArray[0].equals("overall")){
            final MethodHandles.Lookup lookup = MethodHandles.lookup();

            List<MethodHandle> handles =
                    Arrays.stream(paramsArray)
                            .map(field -> {
                                try {
                                    return lookup.findGetter(Advertisement.class, field, String.class);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }).collect(toList());

            Map<List<String>,List<Advertisement>> map=dellst.stream().collect(groupingBy(d->handles.stream()
            .map(handle->{
                try {
                    return (String) handle.invokeExact(d);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }).collect(toList())));

            List<Data> data=new ArrayList<>();


            map.forEach((k,v)->{
                int count=0;
                Data data1=new Data();
                Map<String,String> map1=new HashMap<>();
                for(String str:k){
                    map1.put(paramsArray[count],str);
                    count = count +1;
                }
                data1.setFields(map1);
                Statistics stats=new Statistics();
                stats.setDeliveries(v.size());
                List<String> collectDeliveyIdFromClicked= cllst.stream().map(t->t.getDeliveryId()).collect(toList());

                List<Advertisement> finalClickedList=v.stream().filter(t->{
                    if(collectDeliveyIdFromClicked.contains(t.getDeliveryId())) {
                        return true;
                    }else{
                        return false;
                    }
                }).collect(toList());
                stats.setClicks(finalClickedList.size());

                List<String> collectClickedIdFromInstall= inlst.stream().map(t->t.getClickId()).collect(toList());

                List<String> deliveryIdsForInstall=cllst.stream().filter(t->{
                    if(collectClickedIdFromInstall.contains(t.getClickId())){
                        return true;
                    }else{
                        return false;
                    }
                }).collect(toList()).stream().map(t->t.getDeliveryId()).collect(toList());

                List<Advertisement> finalListForInstall=v.stream().filter(t->{

                    if(deliveryIdsForInstall.contains(t.getDeliveryId())){
                        return true;
                    }else{
                        return false;
                    }
                }).collect(toList());
                stats.setInstalls(finalListForInstall.size());

                data1.setStats(stats);
                data.add(data1);
            });

            response.setData(data);

        }

        return response;
    }


    //
}
