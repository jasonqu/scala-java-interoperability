### scala 的脚本模式

在 `main/script` 目录下，可以直接运行

	scala script.scala

但是不能运行 `scalac script.scala`，因为`script.scala`没有**类**定义

### scala 的编译模式

在 `main/scala` 目录下 `Hello.scala`

	object Hello extends App {
	  println("hello")
	}

这是一个拥有`main`方法的scala类，可以编译运行


	scalac Hello.scala
	scala Hello

`scalac`运行下来会生成多个文件：

	Hello$.class
	Hello$delayedInit$body.class
	Hello.class

可以用`javap`来观察这些class

	E:\Project\scala\study\scala-java-interoperability\src\main\scala>javap Hello
	Compiled from "Hello.scala"
	public final class Hello {
	  public static void main(java.lang.String[]);
	  public static void delayedInit(scala.Function0<scala.runtime.BoxedUnit>);
	  public static java.lang.String[] args();
	  public static void scala$App$_setter_$executionStart_$eq(long);
	  public static long executionStart();
	}
	
	E:\Project\scala\study\scala-java-interoperability\src\main\scala>javap Hello$
	Compiled from "Hello.scala"
	public final class Hello$ implements scala.App {
	  public static final Hello$ MODULE$;
	  public static {};
	  public long executionStart();
	  public java.lang.String[] scala$App$$_args();
	  public void scala$App$$_args_$eq(java.lang.String[]);
	  public scala.collection.mutable.ListBuffer<scala.Function0<scala.runtime.BoxedUnit>> scala$App$$initCode();
	  public void scala$App$_setter_$executionStart_$eq(long);
	  public void scala$App$_setter_$scala$App$$initCode_$eq(scala.collection.mutable.ListBuffer);
	  public java.lang.String[] args();
	  public void delayedInit(scala.Function0<scala.runtime.BoxedUnit>);
	  public void main(java.lang.String[]);
	}
	
	E:\Project\scala\study\scala-java-interoperability\src\main\scala>javap Hello$delayedInit$body
	Compiled from "Hello.scala"
	public final class Hello$delayedInit$body extends scala.runtime.AbstractFunction0 {
	  public final java.lang.Object apply();
	  public Hello$delayedInit$body(Hello$);
	}

也可以用`scalap`来观察：

	E:\Project\scala\study\scala-java-interoperability\src\main\scala>scalap Hello
	object Hello extends scala.AnyRef with scala.App {
	  def this() = { /* compiled code */ }
	}
	... ...


### scala 中使用 java 类 

所有的java类都能被scala使用，就像在java中使用java类一样：

1. import
1. new
1. 调用方法

例如在Hello中可以这样使用java的`ArrayList`

	object Hello extends App {
	  println("hello")
	  
	  import java.util.ArrayList
	  var list : ArrayList[String] = new ArrayList[String]()
	  list.add("hello")
	  list.add("world")
	  println(list)
	}

所以在scala中使用java的API是很方便的

### java 中使用 scala 类 

任何一个scala类都可以被编译成为字节码，从而被java使用。不过编译后的类名和方法名可能发生变化，所以如果要在java中使用scala的API，最好提前定义好接口。下面举一些例子

首先我们定义一个scala的类 `Person`

	package org.scala.test
	
	import scala.beans.BeanProperty
	
	class Person(@BeanProperty var name: String,
	             @BeanProperty var age: Int) {
	  override def toString = s"{name = $name, age = $age}"
	}

可以用`javap`查看这个类

	E:\Project\scala\study\scala-java-interoperability\target\scala-2.10\classes>javap org.scala.test.Person
	Compiled from "Person.scala"
	public class org.scala.test.Person {
	  public static java.util.List<org.scala.test.Person> getPersonList(java.lang.String, int, java.lang.String, int, java.lang.String, int);
	  public java.lang.String name();
	  public void name_$eq(java.lang.String);
	  public void setName(java.lang.String);
	  public int age();
	  public void age_$eq(int);
	  public void setAge(int);
	  public java.lang.String toString();
	  public java.lang.String getName();
	  public int getAge();
	  public org.scala.test.Person(java.lang.String, int);
	}

可以看到，在scala中定义了字段的话，会生成一个同名函数，如`name`会有函数`name()`、`name_$eq`。如果加上了`@BeanProperty`注解，就会生成POJO格式的方法 `getName`、 `setName`。

另外，这里利用scala的拼接字符串的特性，重写了`toString`方法，对java来说，这就是Object中默认的`toString`方法

怎样在java中使用这个scala类呢？很简单，直接import即可

加入我们有一个`Company`类需要使用`Person`，可以这样定义：

	package org.java.test;
	
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
	}

可以看到，为了完成类似的功能，java需要编写大量的样板代码。

让我们在java中编写main方法打印一下：

	public static void main(String[] args) {
		Person p0 = new Person("唐僧", 30);
		Person p1 = new Person("孙悟空", 530);
		Person p2 = new Person("猪八戒", 100);
		Person p3 = new Person("沙和尚", 50);
		ArrayList<Person> list = new ArrayList<>();
		list.add(p1);
		list.add(p2);
		list.add(p3);
		Company c = new Company("西天取经团", p0, list);
		System.out.println(c.getName());
		System.out.println(c.getEmployee().name());
		System.out.println(c.getEmployee().getName());
		System.out.println(c.getEmployers());
	}

可以看到这样的代码有点复杂，我们想利用scala的`collection`完成一个快捷方法：

	object Person {
	  def apply(name : String, age : Int) = new Person(name, age)
	  def getPersonList(name1 : String, age1 : Int, 
	      name2 : String, age2 : Int, 
	      name3 : String, age3 : Int) : java.util.List[Person] = {
	    import scala.collection.JavaConverters._
	    List(Person(name1, age1), Person(name2, age2), Person(name3, age3)) asJava
	  }
	}

这里首先定义了一个单例对象`Person`，然后定义了一个apply方法，这样的方法直接使用Person调用即可，这里是作为了工厂方法使用。
然后定义了一个`getPersonList`方法，它接受6各参数，并返回一个包含3个`Person`的`java.util.List`。

我们重写一下main方法：

		// version2
		Company c2 = new Company("西天取经团", p0, 
				org.scala.test.Person$.MODULE$.getPersonList(
						"孙悟空", 530, 
						"猪八戒", 100, 
						"沙和尚", 50));

可以看到，如果要使用scala的单例对象的方法，需要编写一些很奇怪的代码。

检索词 scala java interop