# linq-to-java
Porting LINQ list queries to Java

As someone who programs in both C# and Java on a regular basis, C#'s LINQ list queries are the thing I miss the most when I program in Java.
So, I decided to port them over. I know Java 8 has introduced a lot of this functionality, but I think that the syntax is ugly and C# does it much better.
This project will provide a common, familiar list querying syntax for C# programmers who also work in Java.
This is a work-in-progress. It has not been tested for performance and is not yet optimized. Many things are done "quick and dirty" (read some of my TODOs). Some of the code has side effects (most of the methods aren't pure functions).

Here's an example of how to use some of the code:

public class Test {

    public static void main(String[] args){

        LArrayList<String> myStringList = new LArrayList<>();
        myStringList.add("Hello");
        myStringList.add("World!");
        // Should print out both "Hello" and "World", since they both have a length less than 7
        myStringList.where(x -> x.length() < 7).forEach(System.out::println);


        LArrayList<Integer> myIntegerList = new LArrayList<>();
        myIntegerList.add(4);
        myIntegerList.add(10);
        myIntegerList.add(7);
        // Should print out 10, it's the first element in the list that's greater than 5
        System.out.println(myIntegerList.firstOrDefault(x -> x > 5));

        LArrayList<TestObj> myTestObjList = new LArrayList<>();
        myTestObjList.add(new TestObj("Bob"));
        myTestObjList.add(new TestObj("Tom"));
        // Should print out "Bob" and "Tom", it selects the "name" field from each object in the list
        myTestObjList.select(x -> x.name).forEach(System.out::println);


    }



}

class TestObj{
    public String name;

    public TestObj(String name){
        this.name = name;
    }
}
