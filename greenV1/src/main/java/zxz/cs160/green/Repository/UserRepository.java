package zxz.cs160.green.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import zxz.cs160.green.Model.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "select * from user", nativeQuery = true)
    List<User> findAllBySQL();

    @Query(value = "select id from user where firstname = ?1 AND password = ?2 order by id DESC limit 1", nativeQuery = true)
    long logedId(String firstname, String password);

    Optional<User> findByUserName(String userName);

    //find user by userId 11/20/2021
    @Query(value = "select * from user where id = ?1 order by id DESC limit 1", nativeQuery = true)
    User findOneUserByIdViaSQL(long id);

    //User Update 11/21/2021
    @Transactional                                              //??????什么原因？？？？
    @Modifying
    @Query(value = "update user set firstname=?2, lastname=?3, cellphone=?4, address=?5, carbrand=?6, carmodel=?7, platenumber=?8 where id=?1", nativeQuery = true)
    int userUpdateByIdViaSQL(long id, String firstname, String lastname, String cellphone, String address, String carbrand, String carmodel, String platenumber);

    @Transactional
    @Modifying
    @Query("UPDATE User a " +
            "SET a.active = TRUE WHERE a.email = ?1")
    int enableUser(String userName);
}

