package nus.iss.ADBackend.Service;

import nus.iss.ADBackend.Repo.*;
import nus.iss.ADBackend.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.math.BigInteger;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class UserService {
    @Autowired
    UserRepository uRepo;
    @Autowired
    HealthRecordRepository hrRepo;
    @Autowired
    DietRecordRepository drRepo;
    @Autowired
    UserRewardRepository urRepo;
    @Autowired
    CommentRepository cRepo;
    @Autowired
    RecipeRepository rRepo;
    @Autowired
    ReportRepository rpRepo;

    public boolean isUserExist(int id) {
        return uRepo.findById(id) != null;
    }
    public boolean isUserNameExist(String username) {
        return uRepo.findByName(username) != null;
    }

    public User getDefaultUserForDelete() {
        return uRepo.findByUsername("deleted-user");
    }

    public User getOffiialUser() {
        return uRepo.findByUsername("official-user");
    }

    public User createUser(String name, String username, String password) {
        if (!isUserNameExist(username)) {
            User u = new User(name, username, password, Role.NORMAL);
            uRepo.saveAndFlush(u);
            return u;
        }
        return null;
    }
    public void createUser(User u) {
        uRepo.saveAndFlush(u);
    }
    public User createAdmin(String name, String username, String password) {
        if (!isUserNameExist(username)) {
            User admin = new User(name, username, password, Role.ADMIN);
            uRepo.saveAndFlush(admin);
            return admin;
        }
        return null;
    }
    public User findUserById(int id) {
        return uRepo.findById(id);
    }
    public User findUserByUserNameAndPassword(String username, String password) {
        return uRepo.findByUsernameAndAndPassword(username, password);
    }
    @Transactional
    public boolean saveUser(User user) {
        if (!isUserExist(user.getId())) {
            return false;
        }
        uRepo.saveAndFlush(user);
        return true;
    }
    @Transactional
    public boolean deleteUserById(int id) {
        User u = findUserById(id);
        if (u == null) {
            //If no such user found
            return false;
        }
        //default for delete
        User deleteUser = getDefaultUserForDelete();
        hrRepo.deleteByUserId(id);
        drRepo.deleteByUserId(id);
        urRepo.deleteByUserId(id);
        rpRepo.deleteByUserId(id);
        deprecateCommentAndRecipe(id, deleteUser);
        uRepo.deleteById(id);
        return true;
    }
    private void deprecateCommentAndRecipe(int id, User user) {
        List<Comment> commentList= cRepo.findAllByUserId(id);
        for (Comment c : commentList) {
            c.setUser(user);
        }
        cRepo.saveAllAndFlush(commentList);
        List<Recipe> recipeList = rRepo.findAllByUserId(id);
        for (Recipe r : recipeList) {
            r.setUser(user);
        }
        rRepo.saveAllAndFlush(recipeList);
    }

    public List<User> findAllUsers() {
        return uRepo.findAll();
    }
    public List<User> findAllNormalUsers() {
        return uRepo.findAllByType(Role.NORMAL);
    }
    public List<User> findAllAdmins() {
        return uRepo.findAllByType(Role.ADMIN);
    }
    public List<User> findAllUsersByGoal(Goal goal) {
        return uRepo.findAllByGoal(goal);
    }

    public void saveAllUsers(List<User> list) {
        for (User u : list) {
            saveUser(u);
        }
    }

    public User findUserByUsername(String username){
        return uRepo.findByUsername(username);
    }

    public User checkHashedUser(int userId, String userHash, String passHash){
        User user = uRepo.findById(userId);
        if(hashing(user.getUsername()).equals(userHash) && hashing(user.getPassword()).equals(passHash)){
            return user;
        }
        else{
            return null;
        }
        
    }

    private String hashing(String text) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // Change this to UTF-16 if needed
        md.update(text.getBytes(StandardCharsets.UTF_8));
        byte[] digest = md.digest();

        String hex = String.format("%064x", new BigInteger(1, digest));
        return hex;
    }
}
