//package org.wenxueliu.test;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

class TestJava8 {

    @FunctionalInterface
	interface Converter<F, T> {
	    T convert(F from);
	}

    static int outerStaticNum = 0;
	int outerNum = 0;

    void testScopes() {
        System.out.println("test testScopes");

        outerStaticNum = 1;
        outerNum = 1;
        System.out.println("outerStaticNum=" + outerStaticNum + " outerNum=" + outerNum);
	    Converter<Integer, String> stringConverter1
            = (from) -> { outerNum = 23; return String.valueOf(from);
	    };
        System.out.println(stringConverter1.convert(111));
	    Converter<Integer, String> stringConverter2
            = (from) -> { outerStaticNum = 72; return String.valueOf(from);
	    };
        System.out.println(stringConverter1.convert(222));

        System.out.println("outerStaticNum=" + outerStaticNum + " outerNum=" + outerNum);
	}

    static void testLambdaVar() {
        System.out.println("test testLambdaVar");
        final int num = 1;
	    Converter<Integer, String> stringConverter
            = (from) -> String.valueOf(from + num);
	    stringConverter.convert(2);
    }

    static void testFunction() {
        System.out.println("test testFunction");
	    Converter<String, Integer> converter = (from) -> Integer.valueOf(from);
	    System.out.println(converter.convert("123"));    // 123

	    Converter<String, Integer> converted = Integer::valueOf;
        System.out.println(converted.convert("456"));

        Function<String, Integer> toInteger = Integer::valueOf;
        Function<String, String> backToString = toInteger.andThen(String::valueOf);
        backToString.apply("123");     // "123"
    }

    static void testLambda() {
        System.out.println("test testLambda");
        List<String> names = Arrays.asList("peter", "anna", "mike", "xenia");
        //Collections.sort(names, (a, b) -> b.compareTo(a));
        Collections.sort(names, (a, b) -> b.compareTo(a));
        names.stream().forEach((str) -> System.out.println(str));
    }


    static void testPredicate() {
        Predicate<String> predicate = (s) -> s.length() > 0;
        predicate.test("foo");              // true
        predicate.negate().test("foo");     // false
        Predicate nonNull = Objects::nonNull;
        Predicate isNull = Objects::isNull;
        Predicate<String> isEmpty = String::isEmpty;
        Predicate isNotEmpty = isEmpty.negate();
    }

    static String returnNull() {
        return null;
    }

    static String returnNoNull() {
        return "123";
    }

    static void testOptinal() {
        Optional<String> optional = Optional.of(returnNoNull());
        System.out.println(optional.isPresent());           // true
        System.out.println(optional.get());                 // "bam"
        System.out.println(optional.orElse("fallback"));    // "bam"
        optional.ifPresent((s) -> System.out.println(s.charAt(0)));

        optional = Optional.ofNullable(returnNull());
        System.out.println(optional.isPresent());           // true
        System.out.println(optional.orElse("bam"));                 // "bam"
        System.out.println(optional.orElse("fallback"));    // "bam"
        optional.ifPresent((s) -> System.out.println(s.charAt(0)));

        Stream<String> names = Stream.of("Lamurudu", "Okanbi", "Oduduwa");
        Optional<String> longest = names
                                .filter(name -> name.startsWith("L"))
                                .findFirst();
        longest.ifPresent(name -> {
                String s = name.toUpperCase();
                System.out.println("The longest name is "+ s);
            });

        Optional<String> lNameInCaps = longest.map(String::toUpperCase);

        String alternate = longest.orElse("Nimrod");
        System.out.println(alternate); //prints out "Nimrod"

        String alternate1 = longest.orElseGet(() -> {
            // perform some interesting code operation
            // then return the alternate value.
            return "Nimrod";
        });
        System.out.println(alternate1); //prints out "Nimrod"

        longest.orElseThrow(NullPointerException::new);
    }


    static void testStream() {
        List<String> stringCollection = new ArrayList<String>();
        stringCollection.add("ddd2");
        stringCollection.add("aaa2");
        stringCollection.add("bbb1");
        stringCollection.add("aaa1");
        stringCollection.add("bbb3");
        stringCollection.add("ccc");
        stringCollection.add("bbb2");
        stringCollection.add("ddd1");


        stringCollection
        .stream()
        .filter((s) -> s.startsWith("a"))
        .forEach(System.out::println);
        // "aaa2", "aaa1"

        stringCollection
        .stream()
        .sorted()
        .filter((s) -> s.startsWith("a"))
        .forEach(System.out::println);
        // "aaa1", "aaa2"

        stringCollection
        .stream()
        .map(String::toUpperCase)
        .sorted((a, b) -> b.compareTo(a))
        .forEach(System.out::println);
        // "DDD2", "DDD1", "CCC", "BBB3", "BBB2", "A<i>AA2", "AAA1"</i>

        boolean anyStartsWithA =
        stringCollection
        .stream()
        .anyMatch((s) -> s.startsWith("a"));
        System.out.println(anyStartsWithA);      // true

        boolean allStartsWithA =
        stringCollection
        .stream()
        .allMatch((s) -> s.startsWith("a"));
        System.out.println(allStartsWithA);      // false

        boolean noneStartsWithZ =
        stringCollection
        .stream()
        .noneMatch((s) -> s.startsWith("z"));
        System.out.println(noneStartsWithZ);      // true

        long startsWithB =
        stringCollection
        .stream()
        .filter((s) -> s.startsWith("b"))
        .count();
        System.out.println(startsWithB);    // 3

        Optional<String> reduced =
        stringCollection
        .stream()
        .sorted()
        .reduce((s1, s2) -> s1 + "#" + s2);
        reduced.ifPresent(System.out::println);
        // "aaa1#aaa2#bbb1#bbb2#bbb3#ccc#ddd1#ddd2"

    }

    static void testParaStream() {
        int max = 1000000;
        List values = new ArrayList<>(max);
        for (int i = 0; i < max; i++) {
            UUID uuid = UUID.randomUUID();
            values.add(uuid.toString());
        }

        // sequential sort took: 899 ms
        long t0 = System.nanoTime();
        long count = values.stream().sorted().count();
        System.out.println(count);
        long t1 = System.nanoTime();
        long millis = TimeUnit.NANOSECONDS.toMillis(t1 - t0);
        System.out.println(String.format("sequential sort took: %d ms", millis));

        // parallel sort took: 899 ms
        t0 = System.nanoTime();
        count = values.parallelStream().sorted().count();
        System.out.println(count);
        t1 = System.nanoTime();
        millis = TimeUnit.NANOSECONDS.toMillis(t1 - t0);
        System.out.println(String.format("parallel sort took: %d ms", millis));
    }

    static public void main(String []args) {
        System.out.println("hello");
        //TestJava8.testLambda();
        //TestJava8.testFunction();
        //TestJava8.testPredicate();
        TestJava8.testOptinal();
        //TestJava8.testParaStream();
        //TestJava8 t = new TestJava8();
        //t.testScopes();
    }


}
