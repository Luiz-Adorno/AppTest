package miips.com.Models;

public class User {

    private String user_id;
    private String email;
    private long phone;
    private String usermane;

    public User(String user_id, String email, long phone, String usermane) {
        this.user_id = user_id;
        this.email = email;
        this.phone = phone;
        this.usermane = usermane;
    }

    public User(){

    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public String getUsermane() {
        return usermane;
    }

    public void setUsermane(String usermane) {
        this.usermane = usermane;
    }

    @Override
    public String toString() {
        return "User{" +
                "user_id='" + user_id + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", usermane='" + usermane + '\'' +
                '}';
    }
}
