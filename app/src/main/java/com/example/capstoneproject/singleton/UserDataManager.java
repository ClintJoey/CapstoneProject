package com.example.capstoneproject.singleton;

import com.example.capstoneproject.models.UserAccountModel;

import java.util.ArrayList;

public class UserDataManager {
    private static UserDataManager instance;
    private ArrayList<UserAccountModel> users;

    public UserDataManager() {
        this.users = new ArrayList<>();
    }

    public static synchronized UserDataManager getInstance() {
        if (instance == null) {
            instance = new UserDataManager();
        }
        return instance;
    }

    public ArrayList<UserAccountModel> getUsers() {
        return users;
    }

    public void clearUsers() {
        users.clear();
    }

    public UserAccountModel getUserById(String userId) {
        for (UserAccountModel user: users) {
            if (user.userUid.equals(userId)) {
                return user;
            }
        }
        return null;
    }

    public void addUser(UserAccountModel user) {
        users.add(user);
    }

    public void updateUserProfile(UserAccountModel updatedUser) {
        for (UserAccountModel user: users) {
            if (user.userUid.equals(updatedUser.userUid)) {
                user.profileImg = updatedUser.profileImg;
                user.firstname = updatedUser.firstname;
                user.middlename = updatedUser.middlename;
                user.lastname = updatedUser.lastname;
                user.sex = updatedUser.sex;
                user.age = updatedUser.age;
                user.role = updatedUser.role;
                user.barangay = updatedUser.barangay;
                user.municipality = updatedUser.municipality;
                user.province = updatedUser.province;
            }
        }
    }

    public void deleteUser(String userUid) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).userUid.equals(userUid)) {
                users.remove(i);
            }
        }
    }
}
