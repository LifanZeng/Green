package zxz.cs160.green.Model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Date;
import java.sql.Time;


@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long provider_id;
    private long customer_id;
    private String startcity;
    private String endcity;
    private Date startday;
    @DateTimeFormat( pattern = "HH:mm" )
    private Time starttime;
    private boolean available = true;
    private String note;

    public Reservation(){
    }

    public Reservation(long id, long provider_id, long customer_id, String startcity, String endcity,       //id
                       Date startday, Time starttime, String note, boolean available){
        this.provider_id=provider_id;
        this.customer_id=customer_id;
        this.startcity=startcity;
        this.endcity=endcity;
        this.startday=startday;
        this.starttime=starttime;
        this.note=note;
        this.available=available;
    }

//    public Reservation(long provider_id, long customer_id, String startcity, String endcity,
//                       String startday, String starttime, String note, boolean available){
//        this.provider_id=provider_id;
//        this.customer_id=customer_id;
//        this.startcity=startcity;
//        this.endcity=endcity;
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        this.startday=LocalDate.parse(startday, formatter);
//        this.starttime=LocalTime.parse(starttime);
//        this.note=note;
//        this.available=available;
//    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProvider_id() {
        return provider_id;
    }

    public void setProvider_id(long provider_id) {
        this.provider_id = provider_id;
    }

    public long getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(long customer_id) {
        this.customer_id = customer_id;
    }

    public String getStartcity() {
        return startcity;
    }

    public void setStartcity(String startcity) {
        this.startcity = startcity;
    }

    public String getEndcity() {
        return endcity;
    }

    public void setEndcity(String endcity) {
        this.endcity = endcity;
    }

    public Date getStartday() {
        return startday;
    }

    public void setStartday(Date startday) {
        this.startday = startday;
    }

    public Time getStarttime() {
        return starttime;
    }

    public void setStarttime(Time starttime) {
        this.starttime = starttime;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}