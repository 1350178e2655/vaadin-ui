package com.partior.client.data.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/*from www .  ja va  2 s  . co  m*/
public class Main {
    public static void main(String...args){

        // Creating List and adding Employees values.
        List<Employee> employeesList = new ArrayList<>();

        employeesList.add(new Employee(101, "Glady", "Manager", "Male", 25_00_000));
        employeesList.add(new Employee(102, "Vlad", "Software Engineer", "Female", 15_00_000));
        employeesList.add(new Employee(103, "Shine", "Lead Engineer", "Female", 20_00_000));
        employeesList.add(new Employee(104, "Nike", "Manager", "Female", 25_00_000));
        employeesList.add(new Employee(105, "Slagan", "Software Engineer", "Male", 15_00_000));
        employeesList.add(new Employee(106, "Murekan", "Software Engineer", "Male", 15_00_000));
        employeesList.add(new Employee(107, "Gagy", "Software Engineer", "Male", 15_00_000));

        // group by - multiple fields
        // Grouping by designation and Gender two properties and need to get the count.

        // account id, transaction-type, currency, amount
//        Map<String, Map<String, Long>> multipleFieldsMap = employeesList.stream()
//                .collect(
//                        Collectors.groupingBy(Employee::getDesignation,
//                                Collectors.groupingBy(Employee::getGender,
//                                        Collectors.summingLong(Employee::getSalary) ) ));



        Map<String, Map<String, Long>> multipleFieldsMap =   employeesList.stream()
                .collect(
                        Collectors.groupingBy(Employee::getDesignation,
                                Collectors.groupingBy(Employee::getGender,
                                        Collectors.summingLong(Employee::getSalary) ) ));

        List<Employee> rets = multipleFieldsMap.entrySet()
                        .stream().map(
                                result -> {
                                    System.out.println("" + result);
                                   return new Employee();
                                }
                        ).collect(Collectors.toList());


        // printing the count based on the designation and gender.
      //  System.out.println("Group by on multiple properties" + multipleFieldsMap);

    }
}
enum Type { MEAT, FISH, OTHER }

class Food {

    private final String name;
    private final boolean vegetarian;
    private final int calories;
    private final Type type;

    public Food(String name, boolean vegetarian, int calories, Type type) {
        this.name = name;
        this.vegetarian = vegetarian;
        this.calories = calories;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public boolean isVegetarian() {
        return vegetarian;
    }

    public int getCalories() {
        return calories;
    }

    public Type getType() {
        return type;
    }
    @Override
    public String toString() {
        return name;
    }

    public static final List<Food> menu =
            Arrays.asList( new Food("pork", false, 1800, Type.MEAT),
                    new Food("beef", false, 7100, Type.MEAT),
                    new Food("chicken", false, 1400, Type.MEAT),
                    new Food("french fries", true, 1530, Type.OTHER),
                    new Food("rice", true, 3510, Type.OTHER),
                    new Food("season fruit", true, 1120, Type.OTHER),
                    new Food("pizza", true, 5150, Type.OTHER),
                    new Food("prawns", false, 1400, Type.FISH),
                    new Food("salmon", false, 4150, Type.FISH),
                    new Food("salmon", false, 4150, Type.FISH),
                    new Food("salmon", false, 4150, Type.FISH)

                    );
}


class Employee {

    private int id;
    private String name;
    private String designation;
    private String gender;
    private long salary;

    public Employee(){};

    public Employee(int id, String name, String designation, String gender, long salary) {
        super();
        this.id = id;
        this.name = name;
        this.designation = designation;
        this.gender = gender;
        this.salary = salary;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public long getSalary() {
        return salary;
    }

    public void setSalary(long salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "Employee [id=" + id + ", name=" + name + ", designation=" + designation + ", gender=" + gender
                + ", salary=" + salary + "]";
    }
}