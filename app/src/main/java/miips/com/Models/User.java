package miips.com.Models;
public class User {

    private String email;
    private String username;
    private String city;
    private String state;
    private String gender;
    private String birth;
    private String miips_id;
    private String profile_url;


    public User(String email, String username, String city, String state, String gender, String birth, String miips_id, String profileUrl) {
        this.email = email;
        this.username = username;
        this.city = city;
        this.state = state;
        this.gender = gender;
        this.birth = birth;
        this.miips_id = miips_id;
        this.profile_url = profileUrl;
    }

    public User(){

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getmiips_id() {
        return miips_id;
    }

    public void setmiips_id(String miips_id) {
        this.miips_id = miips_id;
    }

    public String getprofile_url() {
        return profile_url;
    }

    public void setprofile_url(String profile_url) {
        this.profile_url = profile_url;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", gender='" + gender + '\'' +
                ", birth='" + birth + '\'' +
                ", miips_id='" + miips_id + '\'' +
                ", profile_url='" + profile_url + '\'' +
                '}';
    }
}
