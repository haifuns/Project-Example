package com.haif.hibernatetypeextend.repository;

import com.haif.hibernatetypeextend.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student,String> {

	@Query(value = "SELECT s.* FROM student s WHERE s.friend ->> 0 = :name", nativeQuery = true)
	List<Student> queryByFriend(@Param("name") String name);
}
