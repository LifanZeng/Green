package zxz.cs160.green.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import zxz.cs160.green.Model.User;
import zxz.cs160.green.Repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    //list all users
    public List<User> findAllUser(){
        return userRepository.findAllBySQL();
    }

    //log in
    public long logedId(String firstname, String password){
        return userRepository.logedId(firstname, password);
    }

    //find user by userId 11/20/2021
    public User findOneUserByIdViaSQL(long id){
        return userRepository.findOneUserByIdViaSQL(id);
    }

    //user update 11/21/2021
    @Transactional
    int userUpdateByIdViaSQL(long id, String firstname, String lastname, String cellphone, String address, String carbrand, String carmodel, String platenumber){
        return userRepository.userUpdateByIdViaSQL(id, firstname, lastname, cellphone, address, carbrand, carmodel, platenumber);
    }
}
