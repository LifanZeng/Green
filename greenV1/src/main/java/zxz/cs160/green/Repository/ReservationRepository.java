package zxz.cs160.green.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import zxz.cs160.green.Model.Reservation;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query(value = "select * from reservation where id=?1", nativeQuery = true)
    List<Reservation> findOneBySQL(long id);

    @Query(value = "select * from reservation", nativeQuery = true)
    List<Reservation> findAllBySQL();

    @Query(value = "select * from reservation where available=true", nativeQuery = true)
    List<Reservation> findAllAvailableBySQL();

    @Query(value = "select * from reservation where provider_id=?1", nativeQuery = true)
    List<Reservation> findAllBySQLAsProvider(long provider_id);

    @Query(value = "select * from reservation where customer_id=?1 and available=false and customer_id!=provider_id", nativeQuery = true)    //稍后修改
    List<Reservation> findAllBySQLAsCustomer(long customer_id);

    //@Transactional
    @Modifying
    @Query(value = "update reservation set provider_id=?2, customer_id=?3, startcity=?4, endcity=?5, startday=?6, starttime=?7, note=?8, available=?9 where id=?1", nativeQuery = true)
    int updateBySQL(long id, long provider_id, long customer_id, String startcity, String endcity, Date startday, Time starttime, String note, boolean available);

    //To make a reservation  11/24/2021
    //@Transactional
    @Modifying
    @Query(value = "update reservation set customer_id=?2, available=false where id=?1", nativeQuery = true) //id is reservation id.
    //int makeReservation(long id, long provider_id, long customer_id);
    int makeReservation(long id, long customer_id);

    //Search by Start-City and End-City     11/26/2021
    @Query(value = "select * from reservation where startcity=?1 and endcity=?2 and available=true", nativeQuery = true)
    List<Reservation> searchByStartcityAndEndcity(String startcity, String endcity);

    //To cancel a reservation  11/27/2021
    //@Transactional
    @Modifying
    @Query(value = "update reservation set customer_id=?2, available=true where id=?1", nativeQuery = true) //?1 is reservation id; ?2 is provider id
    int cancelReservation(long id, long customer_id);
}


