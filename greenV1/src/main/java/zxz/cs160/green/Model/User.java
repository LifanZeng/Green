package zxz.cs160.green.Model;

import javax.persistence.*;

@Entity
@Table(name = "user")
public class User {
    @Id
   // @GeneratedValue(strategy = GenerationType.TABLE)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String userName;        //新增
    private String password;
    private String firstname;
    private String lastname;
    private String address;
    private String cellphone;
    private String carbrand;
    private String carmodel;
    private String platenumber;
    private double grade_p;         //grade as a provider.
    private int grade_p_times;
    private double grade_c;         //grade as a customer.
    private int grade_c_times;

    private boolean active;         //新增
    private String roles;           //新增
    private String email;           //新增


    public User(){
    }

    public User(String firstname, String lastname, String email, String password, String roles) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.userName = email;
    }

    public String getUserName() {return email;}

    public void setUserName(String userName) {this.userName = userName;}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getCarbrand() {
        return carbrand;
    }

    public void setCarbrand(String carbrand) {
        this.carbrand = carbrand;
    }

    public String getCarmodel() {
        return carmodel;
    }

    public void setCarmodel(String carmodel) {
        this.carmodel = carmodel;
    }

    public String getPlatenumber() {
        return platenumber;
    }

    public void setPlatenumber(String platenumber) {
        this.platenumber = platenumber;
    }

    public double getGrade_p() {
        return grade_p;
    }

    public void setGrade_p(double grade_p) {
        this.grade_p = grade_p;
    }

    public int getGrade_p_times() {
        return grade_p_times;
    }

    public void setGrade_p_times(int grade_p_times) {
        this.grade_p_times = grade_p_times;
    }

    public double getGrade_c() {
        return grade_c;
    }

    public void setGrade_c(double grade_c) {
        this.grade_c = grade_c;
    }

    public int getGrade_c_times() {
        return grade_c_times;
    }

    public void setGrade_c_times(int grade_c_times) {
        this.grade_c_times = grade_c_times;
    }

    public boolean isActive() {return active;}                      //新增

    public void setActive(boolean active) {this.active = active;}   //新增

    public String getRoles() {return roles;}                        //新增

    public void setRoles(String roles) {this.roles = roles;}        //新增

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}
}
