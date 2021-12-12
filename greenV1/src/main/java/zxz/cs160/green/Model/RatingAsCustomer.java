package zxz.cs160.green.Model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class RatingAsCustomer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long reservation_id;
    private long customer_id;
    int grade_out_of_5;

    public RatingAsCustomer(){
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

    public long getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(long customer_id) {
        this.customer_id = customer_id;
    }

    public int getGrade_out_of_5() {
        return grade_out_of_5;
    }

    public void setGrade_out_of_5(int grade_out_of_5) {
        this.grade_out_of_5 = grade_out_of_5;
    }
}
