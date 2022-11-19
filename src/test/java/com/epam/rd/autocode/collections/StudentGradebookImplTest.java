package com.epam.rd.autocode.collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;

class StudentGradebookImplTest { 

	private StudentGradebook gbook;
	
	private static Student S1 = new Student("LName1", "FName1", "Group1");
	private static Student S2 = new Student("LName2", "FName2", "Group1");
	private static Student S3 = new Student("LName3", "FName3", "Group1");
	private static Student S4 = new Student("LName4", "FName4", "Group2");
	private static Student S5 = new Student("LName5", "FName5", "Group2");
	private static Student S6 = new Student("LName6", "FName6", "Group2");
	
	@BeforeEach
	void setUp() {
		gbook = new StudentGradebookImpl();
		gbook.addEntryOfStudent(S1, "dis1", BigDecimal.valueOf(3.3));
		gbook.addEntryOfStudent(S1, "dis2", BigDecimal.valueOf(3.4));
		gbook.addEntryOfStudent(S1, "dis3", BigDecimal.valueOf(3.5));

		gbook.addEntryOfStudent(S2, "dis1", BigDecimal.valueOf(3.3));
		gbook.addEntryOfStudent(S2, "dis2", BigDecimal.valueOf(3.4));
		gbook.addEntryOfStudent(S2, "dis3", BigDecimal.valueOf(3.5));

		gbook.addEntryOfStudent(S3, "dis1", BigDecimal.valueOf(3.9));
		gbook.addEntryOfStudent(S3, "dis2", BigDecimal.valueOf(4.0));
		gbook.addEntryOfStudent(S3, "dis3", BigDecimal.valueOf(4.1));

		gbook.addEntryOfStudent(S4, "dis1", BigDecimal.valueOf(4.6));
		gbook.addEntryOfStudent(S4, "dis2", BigDecimal.valueOf(4.2));
		gbook.addEntryOfStudent(S4, "dis3", BigDecimal.valueOf(5.0));

		gbook.addEntryOfStudent(S5, "dis1", BigDecimal.valueOf(4.5));
		gbook.addEntryOfStudent(S5, "dis2", BigDecimal.valueOf(4.6));
		gbook.addEntryOfStudent(S5, "dis3", BigDecimal.valueOf(4.7));

		gbook.addEntryOfStudent(S6, "dis1", BigDecimal.valueOf(4.4));
		gbook.addEntryOfStudent(S6, "dis2", BigDecimal.valueOf(4.6));
		gbook.addEntryOfStudent(S6, "dis3", BigDecimal.valueOf(4.8));
	}

	@Test
	void addEntryOfStudentShouldReturnCorrectValues() {
		assertTrue(gbook.addEntryOfStudent(S1, "dis4", BigDecimal.valueOf(3.3)));
		assertFalse(gbook.addEntryOfStudent(S1, "dis4", BigDecimal.valueOf(3.3)));
		assertFalse(gbook.addEntryOfStudent(S1, "dis4", BigDecimal.valueOf(3.3)));
		assertTrue(gbook.addEntryOfStudent(S1, "dis5", BigDecimal.valueOf(3.3)));
	}

	@Test
	void sizeShoulBeEqualed6() {
		int expected = 6;
		int actual = gbook.size();
		assertEquals(expected, actual);
	}

	@Test
	void getAndSortAllStudentsShoulReturnMapWithCorrectContent() {
		Map<BigDecimal, List<Student>> map = gbook.getAndSortAllStudents();
		
		List<Student> expected;
		List<Student> actual;
		
		expected = Arrays.asList(S1, S2);
		actual = map.get(BigDecimal.valueOf(3.4));
		assertIterableEquals(expected, actual);

		expected = Arrays.asList(S3);
		actual = map.get(BigDecimal.valueOf(4.0));
		assertIterableEquals(expected, actual);

		expected = Arrays.asList(S4, S5, S6);
		actual = map.get(BigDecimal.valueOf(4.6));
		assertIterableEquals(expected, actual);
	}

	@Test
	void getComparatorShoulReturnCorrectComparator() {
		Comparator<Student> comp = gbook.getComparator();

		assertThrows(RuntimeException.class, () -> comp.compare(S1, null));
		assertTrue(Math.signum(comp.compare(S1, S2)) == -Math.signum(comp.compare(S2, S1)));
		
		Student s1 = new Student("name1", "name1", "group1");
		Student s2 = new Student("name1", "name1", "group1");
		Student s3 = new Student("name2", "name2", "group2");
		
		assertTrue(comp.compare(s1, s2) == 0);
		assertTrue(Math.signum(comp.compare(s1, s3)) == Math.signum(comp.compare(s1, s3)));
	}

	@Test
	void getStudentsByDisciplineShouldReturnProperValue() {
		Map<Student, Map<String, BigDecimal>> map = 
				gbook.removeStudentsByGrade(BigDecimal.valueOf(4.0));
		
		List<Student> expected;
		Set<Student> actual;
		
		expected = Arrays.asList(S1, S2, S3);
		actual = map.keySet();
		assertIterableEquals(expected, actual);
		
		List<Student> studs = new ArrayList<>();
		gbook.getAndSortAllStudents().forEach((k, v) -> studs.addAll(v));
		
		assertEquals(3, studs.size());

		assertTrue(studs.contains(S4));
		assertTrue(studs.contains(S5));
		assertTrue(studs.contains(S6));
	}

	@Test
	void getStudentsByDisciplineShouldProperlyRemoveEntries() {
		gbook.removeStudentsByGrade(BigDecimal.valueOf(4.0));
		
		List<Student> studs = new ArrayList<>();
		gbook.getAndSortAllStudents().forEach((k, v) -> studs.addAll(v));
		
		assertEquals(3, studs.size());
		
		assertTrue(studs.contains(S4));
		assertTrue(studs.contains(S5));
		assertTrue(studs.contains(S6));
	}
	
	@Test
	void complianceTestLambdaExpressionsAreRestrictedForUsing() {
		Stream.of(StudentGradebookImpl.class)
			.map(Class::getDeclaredMethods)
			.flatMap(Stream::of)
			.filter(m -> Modifier.isStatic(m.getModifiers()))
			.filter(m -> Modifier.isPrivate(m.getModifiers()))
			.map(Method::getName)
			.filter(name -> name.contains("lambda$"))
			.findAny()
			.ifPresent(m -> 
					fail(() -> "Using of lambda expressions is restricted: " + m));
	}
	
	@Test
	void appShouldUseOnlyOptionalFromJavaUtilPackage() {
		SpoonAPI spoon = new Launcher();
		spoon.addInputResource("src/main/java/");
		spoon.buildModel();

		spoon.getModel()
			.getElements(new TypeFilter<>(CtTypeReference.class))
			.stream()
			.filter(r -> r.toString().startsWith("java.util.stream"))
			.map(CtTypeReference::getQualifiedName)
			.findAny()
			.ifPresent(name -> 
					fail(() -> "Using of stream API is restricted: " + name));
	}

}
