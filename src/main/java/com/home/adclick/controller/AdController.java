package com.home.adclick.controller;

import com.home.adclick.model.Advertisement;
import com.home.adclick.model.ClickedEvent;
import com.home.adclick.model.InstallEvent;
import com.home.adclick.service.AdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class AdController {

    AdService adService;


    @Autowired
    public void setAdService(AdService adService) {
        this.adService = adService;
    }



    @PostMapping("/delivery")
    @ResponseBody
    public ResponseEntity<String> advertisement(@RequestBody Advertisement adv) {
        try {
            adService.addAdvt(adv);
            return new ResponseEntity("Successfully Inserted",HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity("Error happened",HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


    @PostMapping("/click")
    @ResponseBody
    public ResponseEntity<String> clickedEvent(@RequestBody ClickedEvent clickedEvent) {
        try {

            if(adService.addClickedEvent(clickedEvent)){
                return new ResponseEntity("Successfully Inserted",HttpStatus.OK);
            }else {
                return new ResponseEntity("Newly Clicked",HttpStatus.NOT_FOUND);
            }


        } catch (Exception e) {
            return new ResponseEntity("Error happened",HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/install")
    @ResponseBody
    public ResponseEntity<String> installEvent(@RequestBody InstallEvent installEvent) {
        try {
            if(adService.addInstallEvent(installEvent)){
                return new ResponseEntity("Successfully Inserted",HttpStatus.OK);
            }else{
                return new ResponseEntity("Newly installed",HttpStatus.NOT_FOUND);
            }


        } catch (Exception e) {
            return new ResponseEntity("Error happened",HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/statistics/{time}/{fromTime}/{toTime}/{params}")
    @ResponseBody
    public ResponseEntity<Object> statistics(@PathVariable String time,@PathVariable String fromTime,@PathVariable String toTime,
                                             @PathVariable String params) {


        return new ResponseEntity<Object>(adService.search(fromTime,toTime,params),HttpStatus.OK);

    }

}
