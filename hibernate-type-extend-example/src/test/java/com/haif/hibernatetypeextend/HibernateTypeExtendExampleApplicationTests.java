package com.haif.hibernatetypeextend;

import com.haif.hibernatetypeextend.model.Student;
import com.haif.hibernatetypeextend.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
class HibernateTypeExtendExampleApplicationTests {

	@Autowired
	private StudentRepository studentRepository;

	@Test
	public void contextLoads() {

		Map<String,String> books = new HashMap<>();
		books.put("1","白夜行");
		books.put("2","嫌疑人X的献身");

		List<String> friend = new ArrayList<>();
		friend.add("李四");
		friend.add("王五");

		Map<String,Object> info = new HashMap<>();
		info.put("book",books);
		info.put("friend",friend);

		Student student = new Student();
		student.setName("张三");
		student.setBook(books);
		student.setFriend(friend);
		student.setInfo(info);

		studentRepository.save(student);

		List<Student> students = studentRepository.findAll();
		log.info("All students:{}", students.toString());

		List<Student> queryByFriend = studentRepository.queryByFriend("李四");
		log.info("QueryByFriend:{}", queryByFriend.toString());
	}

}
