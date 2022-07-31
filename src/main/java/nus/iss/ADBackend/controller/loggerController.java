package nus.iss.ADBackend.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import nus.iss.ADBackend.Service.DietRecordService;
import nus.iss.ADBackend.Service.UserService;
import nus.iss.ADBackend.model.*;
import nus.iss.ADBackend.model.User;

@RestController
@RequestMapping(value= "/user", produces = "application/json")
public class loggerController {

    @Autowired
    DietRecordService dietRecordService;

    @Autowired
    UserService userService;
    

    @RequestMapping("/gethealthrecords")
    public List<HealthRecord> getHealthRecords(@RequestBody JSONObject response) throws IOException, ParseException{
        String username = response.getAsString("username");
        System.out.println((username));

        // Create method to get the list of health records to be sent back to the android app
        List<HealthRecord> hList = new ArrayList<>();
        HealthRecord test = new HealthRecord();
        test.setCalIntake(2000);
        LocalDate date = LocalDate.now();
        test.setDate(date);
        test.setUserHeight(170);
        test.setUserWeight(65.0);

        hList.add(test);
        return hList;
    }

    @RequestMapping("/getdietrecords")
    public List<DietRecord> getDietRecords(@RequestBody JSONObject response) throws IOException, ParseException{
        String username = response.getAsString("username");
        String dateString = response.getAsString("date");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        LocalDate date = LocalDate.parse(dateString, formatter);
        // System.out.println((username));
        

        User curr = userService.findUserByUsername(username);
        List<DietRecord> dList = new ArrayList<>();
        dList = dietRecordService.findByUserIdAndDate(curr.getId(), date);

        return dList;
    }

}
