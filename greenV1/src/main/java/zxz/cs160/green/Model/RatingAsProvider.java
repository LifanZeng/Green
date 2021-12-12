package zxz.cs160.green.Model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class RatingAsProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long reservation_id;
    private long provider_id;
    private int grade_out_of_5;

    public RatingAsProvider(){
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getReservation_id() {
        return reservation_id;
    }

    public void setReservation_id(long reservation_id) {
        this.reservation_id = reservation_id;
    }

    public long getProvider_id() {
        return provider_id;
    }

    public void setProvider_id(long provider_id) {
        this.provider_id = provider_id;
    }

    public int getGrade_out_of_5() {
        return grade_out_of_5;
    }

    public void setGrade_out_of_5(int grade_out_of_5) {
        this.grade_out_of_5 = grade_out_of_5;
    }
}
