package com.haif.hibernatetypeextend.model;

import com.vladmihalcea.hibernate.type.basic.PostgreSQLHStoreType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Map;

@TypeDefs({
		@TypeDef(name = "json", typeClass = JsonBinaryType.class),
		@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class),
		@TypeDef(name = "hstore", typeClass = PostgreSQLHStoreType.class)
})
@Data
@Entity
@Table(name = "STUDENT")
public class Student implements Serializable {

	private static final long serialVersionUID = -5722616742640875637L;

	@Id
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@GeneratedValue(generator = "system-uuid")
	@Column(name = "ID")
	private String id;

	@Column(name = "NAME")
	private String name;

	@Type(type = "hstore")
	@Column(name = "BOOK", columnDefinition = "hstore")
	private Map<String,String> book;

	@Type(type = "json")
	@Column(name = "INFO", columnDefinition = "json")
	private Object info;

	@Type(type = "jsonb")
	@Column(name = "FRIEND", columnDefinition = "jsonb")
	private Object friend;
}
