package model;

/**
 * Created by Admin on 4/12/2017.
 */

public class User {
    public String id;
    public String urlProfile = null;
    public String name;
    public String phone;
    public  String email;
    public  int gender = 0;
    public int age ;
    public String city = null;
    public User()
    {

    }
    public User (String id,String name,String phone,String email)
    {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }
    public  User (String id,String urlProfile,String name,String phone,String email,int gender,int age,String city)
    {
        this.urlProfile = urlProfile;
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.gender = gender;
        this.age = age;
        this.city = city;


    }

}
