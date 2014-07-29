package org.scala.test

import scala.beans.BeanProperty

class Person(@BeanProperty var name: String,
             @BeanProperty var age: Int) {
  override def toString = s"{name = $name, age = $age}"
}

object Person {
  def apply(name : String, age : Int) = new Person(name, age)
  def getPersonList(name1 : String, age1 : Int, 
      name2 : String, age2 : Int, 
      name3 : String, age3 : Int) : java.util.List[Person] = {
    import scala.collection.JavaConverters._
    List(Person(name1, age1), Person(name2, age2), Person(name3, age3)) asJava
  }
}