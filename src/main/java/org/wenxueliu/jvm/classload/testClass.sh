#!/bin/bash

set -x

javac ClassLoad.java


echo "通过子类引用父类的静态字段，不会导致子类初始化"

java -XX:+TraceClassLoading ClassLoad "case1"


echo "通过数组定义类引用类，不会触发此类初始化"

java -XX:+TraceClassLoading ClassLoad "case2"

echo "常量在编译节点会存入调用类的常量池中，不会触发定义常量的类的初始化"

java -XX:+TraceClassLoading ClassLoad "case3"
