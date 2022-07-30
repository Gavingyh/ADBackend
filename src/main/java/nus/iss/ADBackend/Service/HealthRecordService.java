package nus.iss.ADBackend.Service;

import nus.iss.ADBackend.Repo.HealthRecordRepository;
import nus.iss.ADBackend.Repo.UserRepository;
import nus.iss.ADBackend.model.HealthRecord;
import nus.iss.ADBackend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class HealthRecordService {

    @Autowired
    HealthRecordRepository hrRepo;
    @Autowired
    UserRepository uRepo;

    void createHealthRecord(HealthRecord hr) {
        hrRepo.saveAndFlush(hr);
    }
    boolean saveHealthRecord(HealthRecord hr) {
        if (hrRepo.findById(hr.getId()) != null) {
            hrRepo.saveAndFlush(hr);
            return true;
        }
        return false;
    }
    HealthRecord findHealthRecordByUserIdAndDate(int userId, LocalDate date) {
        return hrRepo.findByUserIdAndAndDate(userId, date);
    }
    HealthRecord createHealthRecordIfAbsent(int userId, LocalDate date) {
        if (findHealthRecordByUserIdAndDate(userId, date) != null) {
            return findHealthRecordByUserIdAndDate(userId, date);
        }
        User u = uRepo.findById(userId);
        if (u == null) {
            //user not exist
            return null;
        }
        HealthRecord hr = new HealthRecord(u, date);
        hrRepo.saveAndFlush(hr);
        return hr;
    }

    List<HealthRecord> findAllHealthRecordsByUserId(int userId) {
        return hrRepo.findByUserId(userId);
    }
}