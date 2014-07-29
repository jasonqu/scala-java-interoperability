package org.java.test;

import java.util.ArrayList;
import java.util.List;

import org.scala.test.Person;

public class Company {
	private String name;
	private Person employee;
	private List<Person> employers;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Person getEmployee() {
		return employee;
	}

	public void setEmployee(Person employee) {
		this.employee = employee;
	}

	public List<Person> getEmployers() {
		return employers;
	}

	public void setEmployers(List<Person> employers) {
		this.employers = employers;
	}

	public Company(String name, Person employee, List<Person> employers) {
		super();
		this.name = name;
		this.employee = employee;
		this.employers = employers;
	}

	public static void main(String[] args) {
		Person p0 = new Person("唐僧", 30);
		Person p1 = new Person("孙悟空", 530);
		Person p2 = new Person("猪八戒", 100);
		Person p3 = new Person("沙和尚", 50);
		List<Person> list = new ArrayList<>();
		list.add(p1);
		list.add(p2);
		list.add(p3);
		Company c = new Company("西天取经团", p0, list);
		System.out.println(c.getName());
		System.out.println(c.getEmployee().name());
		System.out.println(c.getEmployee().getName());
		System.out.println(c.getEmployers());
		
		// version2
		Company c2 = new Company("西天取经团", p0, 
				org.scala.test.Person$.MODULE$.getPersonList(
						"孙悟空", 530, 
						"猪八戒", 100, 
						"沙和尚", 50));
	}

}
