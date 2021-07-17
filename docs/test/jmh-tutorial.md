---
title: "JMH使用教程"
date: "2021-07-13"
description: "基准测试，JMH教程，JMH用法，微基准测试"
footer: CC-BY-SA-4.0 Licensed | Copyright © liqiang
---

## 概述
* ArrayList 和 LinkedList谁更快？
* 二维数组行优先和列优先哪种方式遍历更优？
* StringBuilder 和 StringBuffer性能差异多少？

在工作中我们有很多性能对比测试的需求，为了能得出正确的结论，避免受到不必要的挑战，我们需要给出量化的测试结果。

所以掌握一种科学的，量化对比测试方法很有必要，尤其是对于从事底层开发来说，这是一项必会的技能。

### 基准测试
基准测试（Benckmarking）是指通过设计科学的测试方法、测试工具和测试系统，实现对一类测试对象的某项性能指标进行定量的和可对比的测试。

例如，对计算机CPU进行浮点运算、数据访问的带宽和延迟等指标的基准测试，可以使用户清楚地了解每一款CPU的运算性能及作业吞吐能力是否满足应用程序的要求.

### JMH 是什么
JMH（Java Microbenchmark Harness）是一个用来构建，执行和分析 Java 和其他面向 JVM 的语言的微基准测试工具包。

所谓微基准指的是其测试精读最高可达**纳秒**级别，使得应用场景更广，测试结果更加精准；而Harness则就表明了其不仅能够进行测试，还集成了生成测试报告的能力，当代码执行完毕后，可以轻而易举地生成图片和报表。

TODO： 图片

JMH 与 JVM 是同一团队开发的，所以针对虚拟机的各种优化 JMH 也会考虑在内。是比较靠谱的一个基准测试工具，在很多开源框架中也使用JMH做性能测试。

## QuickStart
### 命令行中构建 <Badge text="推荐" type="tip"/>

在测试大型项目时，通常将基准测试保存在单独的子项目（模块）中，通过构建依赖关系来依赖测试模块。通常不建议在IDE中运行基准测试，因为基准测试运行的环境通常不受控制。

虽推荐使用命令行的方式，但是很多人仍然你喜欢使用IDE。IDE的具体使用教程请移步[JMH官方文档]( https://github.com/openjdk/jmh)。此处介绍命令行的方式构建基准测试程序。

**Step 1**：配置基准测试项目。使用以下命令，可以基于 maven 模板，在test文件夹下生成一个 JMH 驱动的项目。

```shell
$ mvn archetype:generate \
  -DinteractiveMode=false \
  -DarchetypeGroupId=org.openjdk.jmh \
  -DarchetypeArtifactId=jmh-java-benchmark-archetype \
  -DgroupId=org.sample \
  -DartifactId=test \
  -Dversion=1.0
```
如果您想要对其他的JVM语言进行基准测试，只需要将DarchetypeArtifactId参数值改成即可，具体可选项参考已有的[模板列表](https://repo.maven.apache.org/maven2/org/openjdk/jmh/)。

**Step 2**：构建基准测试。在项目生成后，可以通过一下命令进行构建项目。

```shell
cd test/
mvn clean verify
```

**Step 3**：运行基准测试。当项目构建完成后，你会得到包含所有的JMH基础代码以及你的基准测试代码的可执行jar包。运行它即可。

```
java -jar target/benchmarks.jar
```

## JMH 注意事项
想要获得准确的测试结果，我们必须模拟程序的真实执行场景，排除JVM优化和其他无关操作对执行结果的影响。
### JVM，操作系统优化
**无效代码消除(Dead Code Elimination)**，在运行时不是所有代码都会执行。当JVM认为一段逻辑执行后没有结果输出或者外部影响，就会将当前代码判定为DeadCode，从而不会执行这段代码。

::: details 代码示例
```java
    @Benchmark
    public void measureWrong() {
        // 这是错误的：结果没有被使用，整个计算将会被编译器优化。
        Math.log(x);
    }
```
:::
**常量折叠（Constant Folding）**，是通过对编译时常量或常量表达式进行计算来简化代码。它是无效代码消除的另一种形式。

::: details 代码示例
```java
   @Benchmark
    public void measureWrong() {
        // 常量折叠
        int x = 7 * 8 / 2;
        int y = 4;
        // 常量传播（也属于常量折叠）
        return x + y;
    }
```
:::
以上代码经过编译器的常量折叠优化后，会直接返回一个数值，无需额外计算，具体代码如下：
::: details 代码示例
```java
    @Benchmark
    public void measureWrong() {
        return 32;
    }
```
:::

JVM是解释执行语言，Java代码会先编译成二进制码（.class文件），然后加载到JVM中，在运行时时在转换成机器码执行。HotSpot自适应优化器在执行期间收集有关程序热点的信息，会将热点编译为机器码以提高程序的秩序速度。
**方法内联（Inlining）**是JVM非常重要的一个优化，内联是一种优化已编译源源码的方式，通常将最常执行的方法调用（也称之为热点），在运行时替换为方法主体，以便减少调用成本。比如 A 方法内调用 B 方法，则编译器可能会将 B 方法的代码编译进 A 方法体中，以提高 A 方法的执行速度。

TODO: code

### 资源释放与销毁
测试时往往会依赖于一些参数和外部资源，这些和测试目标无关的操作，不应当计入测试报告中。
比如测试文件随机访问性能时，我们要在每次此时执行之前生成测试文件；测试HashMap和ConcurrentHashmap的性能区别时，我们要预先构建出相应的测试数据。
::: details 代码示例
```java
// 错误代码示例

```
:::

### 注意事项

1. 用户可以通过注解选择默认的执行模式，也可以通过运行时选择其他的模式。

2. 在测试过程中我们的代码可能会因为性能抛出异常，此时需要声明将它们抛出去即可。如果代码抛出了实际的异常，此次测试将会因为报错而立马终止。

3. 当你对代码执行行为或结果感到疑惑时，需要检查生成的代码是否和你的预期相符。准确的测试结果往往需要正确得试验配置，所以交叉检查生成的代码对试验的成功至关重要。
   生成的代码位置一般在/target/generated-sources/annotations/.../XXXX.java。

## @Benchmark：Hello world

Benchmark注解用于基准方法之上。JMH会在编译时为该方法生成生成的benchmark代码，将该方法注册到及注册时方法列表中，从注解中读出默认值，然后为benchmark准备运行环境。

测试可以使存粹的性能测试，也可以是对比测试，所以一个基准测试类中可以包含多个被@Benchmark注释的代码，表示将进行多组基准测试。

需要注意的是，org.openjdk.jmh.annotations包中的大多数注解都可以放在Benchmark方法中，也可以放在类上由类中的所有Benchmark方法继承。

### 使用限制
1. 修饰的方法应该是public的
2. 方法入参只能包括调用方法时注入的@State对象（下文有介绍），或者@Blackhole对象
3. 只有在相关的State放在封闭类上时，方法才能同步

如果要对破坏这些属性的方法进行基准测试，则必须将它们写成不同的方法并从Benchmark方法中调用它们。
Benchmark 方法可以声明要抛出的异常和 Throwable，任何实际引发和抛出的异常都将被视为基准测试失败
### 代码示例
::: details 代码示例
```java
public class JMHSample_01_HelloWorld {

    @Benchmark
    public void wellHelloThere() {
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_01_HelloWorld.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
```
:::
### 让程序跑起来
构建JMH程序的执行方式分为两种，即命令行和IDE。对于基准测试方法的执行，我们还有很多可选的配置参数，比如程序的执行时间，执行次数，预热次数等，这些在下文将会进行详细介绍。
假设是通过命令行构建的程序，我们通过以下命令执行程序。
```
:::shell
    # a) Via command-line:
    $ mvn clean install
    $ java -jar target/benchmarks.jar JMHSample_01
```
:::
也可以通过JavaAPI方式运行程序，我们需要在main方法中执行，将执行所需要的参数设置到Options对象中，然后通过Runner方法的run方法启动程序即可。
::: details 代码示例
```java
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_01_HelloWorld.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
```
:::
### 参数设置原则
对于配置参数，我们可以通过三种方式进行设置，即 注解，Java API/命令行。

命令行/JavaAPI模式统称为命令行选项，如果是命令行模式，我们可以在命令行中指定对应的参数即可；如果是在IDE通过main方法执行，则可以通过在Options配置JMH的执行参数。

除了运行时所有的命令行选项外，我们还可以通过注解给一些基准测试提供默认值。在你处理大量基准测试时这个很有用，因为其中一些基准方法需要特殊处理。

注解可以放在class上，来影响这个class中所有的基准测试方法。规则是，靠近作用域的注解有优先权：比如，方法上的注解可以覆盖类上的注解；命令行优先级最高。

#### 代码示例
::: details 代码示例
```java
@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class JMHSample4ParamsSetup {

    double x1 = Math.PI;

    /*
     * 方法上的@Measurement覆盖类上的设置
     */
    @Benchmark
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public double measure() {
        return Math.log(x1);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_20_Annotations.class.getSimpleName())
                .timeUnit(TimeUnit.SECONDS)
                .build();
        new Runner(opt).run();
    }

}
```
:::
如果在IDE中执行以上案例，测试报告中的单位应该是秒，因为命令行选项的优先级最高。

## @OutputTimeUnit：结果时间单位
我们可以在方法或者类上加@OutputTimeUnit注解，来设置执行结果测试报告中的时间单位，JMH支持的精读范围是 纳秒 到 天。

::: tip 时间单位换算
1000纳秒= 1 微秒，1000微秒=1毫秒 ，1秒=1000毫秒
:::
## @BenchmarkMode: 明确测量指标
进行基准测试之前，我们首先要根据测试目的设定一些指标。是吞吐量还是执行时间？
这时可以通过@BenckmarkMode设置，可以传入1个或者多个Mode枚举值设置测量指标。

### Mode可选参数
吞吐量（Throughput），表示在单位时间内的执行次数。通过在一段时间内（time）不断调用基准方法，统计该方法（ops）的执行次数进而计算吞吐量，即throughput = ops/time。

平均时间（AverageTime）， AverageTime= time/ops, 表示每次执行所需要的平均时间。和Throughput类似，此模式是基于时间的，通过在一段时间内（time）不断调用基准方法，统计该方法（ops）的执行次数，即AverageTime = time/ops。

取样时间（SampleTime）, 采样统计方法执行时间。此模式也是基于时间的，通过在一段时间内不断调用基准方法，然后对方法的执行进行采样统计，以方便我们可以推算出**分布、百分位数**等。JMH会尝试自动调整采样频率，如果方法执行时间足够长，最终将会采集所有的样本。

单次调用时间（SingleShotTime）, 测试方法执行一次所需要的时间。此模式是基于调用次数的，测试时，只会调用一次 @Benchmark 方法，并记录执行时间。这种模式通常用来测试冷启动时的性能。

所有模式（ALL），相当于传入以上所有的参数数组。
### 代码示例
当然我们也可以一次选择多个模式，只需要将参数换成数组即可；也可以通过Mode.All来选择全部模式。
::: details 代码示例
```java
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void measureMultiple() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(100);
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput, Mode.AverageTime, Mode.SampleTime, Mode.SingleShotTime})
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void measureMultiple() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(100);
    }
    
    @Benchmark
    @BenchmarkMode(Mode.All)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void measureAll() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(100);
    }
```
:::

## @State：多线程测试必备

### 什么是状态变量
基准测试有时需要依赖一些变量或者外部资源，但是又不想这些变量或资源成为基准测试代码的一部分。

比如在测试文件随机读取性能时，我们要先生成不同的测试文件；测试HashMap和ConcurrentHashMap的读取性能时，我们需要先创建相应的对象并设置初始值；或者说我们需要将基准测试过程中的某个结果记录到一个变量中。

这一类数据在JMH中统称为“状态”变量。状态变量需要再特定的状态类（@State注解修饰）中声明，然后可以将该状态类的实例作为参数提供给基准测试方法。这些类的实例会在需要时初始化，，并在整个基准测试过程中重复使用。
 
以下是两个是两个状态类，可以忽略类名，只需要在对应类上加上@State即可。

::: details 代码示例
```java
    @State(Scope.Benchmark)
    public static class BenchmarkState {
        volatile double x = Math.PI;
    }

    @State(Scope.Thread)
    public static class ThreadState {
        volatile double x = Math.PI;
    }
```
:::

基准方法可以引用这些状态变量，JMH将在调用这些方法时注入适当的状态。可以完全没有状态变量，或者只有一个状态变量，或者引用多个状态变量。状态的引入使得我们进行多线程并发基准测试更加简单。

::: details 代码示例
```java
    // 所有的基准测试方法都会调用这个方法，因为Scope是Thread，所以每个线程都会有一个独立的状态对象，这种case通常用于保存非共享数据。
   @Benchmark
    public void measureUnshared(ThreadState state) {
        // All benchmark threads will call in this method.
        //
        // However, since ThreadState is the Scope.Thread, each thread
        // will have it's own copy of the state, and this benchmark
        // will measure unshared case.
        state.x++;
    }
    // 所有的基准测试方法都会调用这个方法，因为Scope为Benchmark，所以所有线程都共享一个状态实例，这种case通常用于保存多线程共享数据
    @Benchmark
    public void measureShared(BenchmarkState state) {
        // All benchmark threads will call in this method.
        //
        // Since BenchmarkState is the Scope.Benchmark, all threads
        // will share the state instance, and we will end up measuring
        // shared case.
        state.x++;
    }
```
:::
### 状态类的限制
1. 类的作用域必须是public
2. 如果是内部类，则必须声明为静态内部类（public static class ...)
3. 必须包含无参构造函数

### State Scope
State 对象在整个基准测试过程中可以被重复使用，可以通过Scope枚举指定 State 的作用域。State作用域一共有三种：

Benchmark：同一类型的State对象将会在所有线程之间共享，即只会创建一个全局状态对象。
Thread：：同一类型的State对象，每个线程都会创建一个，即线程间不同享。
Group：同一类型的所有State对象将会在同一执行组内的所有线程共享，即每个执行组都会创建一个状态对象。

### Default State
幸运的是，大多数情况我们只需要使用一个状态对象，这是我们可以直接基准测试类所在的类加上@State注解，将状态信息定义在当前类中，在基准测试方法中直接引用即可。

::: details 代码示例
```java
@State(Scope.Thread)
public class JMHSample_04_DefaultState {

    double x = Math.PI;

    @Benchmark
    public void measure() {
        x++;
    }
}
```
:::

当然，你可以选择单独定义一个状态对象。
::: details 代码示例
```java
@State(Scope.Thread)
public class JMHSample_04_DefaultState {

    @Benchmark
    public void measure(CountState state) {
        state.addOne();
    }
}
@State(Scope.Thread)
public class CountState {
    
    double x = 0;
    
    public void addOne() {
        x++;
    }
}
```
:::

### State Fixtures：状态设置和销毁

大多数的基准测试都会执行很多次基准方法，而状态对象会伴随着在整个基准测试生命周期，我们需要再特定时机对状态值进行初始化和重置动作。

JMH提供了两个状态管理的注解，@Setup, 表示被该注解标记的方法会在基准测试方法执行之前执行，而被@Teardown标记的方法则会在方法执行之后执行。与Junit中的Before和Teardown的语义是类似的。**fixture方法的耗时不会被统计进性能指标，所以可以用它来做一些比较重的操作。**

JUnit和TestNG中的也有类似的方法，比如JUnit中@Before和@After与之语义相似。
::: warn 注意事项
这些状态管理的方法只能在对状态对象起作用， 否则编译时会报错！
::: 
和State对象一样，这些固定的方法只会被使用State对象的基准测试线程调用。也就是说，你可以在ThreadLocal上下文中操作，不用对这些方法加锁同步。

注意：这些固定方法也能够操作静态字段，尽管这些操作语意已经超过了状态对象的范畴，但是这仍然符合Java的语法规则。（比如，每个类中都定义一个静态字段）

::: details 代码示例
```java
@State(Scope.Thread)
public class JMHSample_05_StateFixtures {

    double x;
    /* 默认每个@Benchmark前执行：基准测试测试前的准备工作
     */

    @Setup
    public void prepare() {
        x = Math.PI;
    }

    /* 默认每个@Benchmark执行之后执行：检查基准测是否执行成功 ？？？
     */

    @TearDown
    public void check() {
        assert x > Math.PI : "Nothing changed?";
    }

    /* 这个方法显然是正确的，每次基准测试方法执行时，都是操作State对象的x字段。check()永远不会失败，因为我们总是保证每轮测试至少调用一次基准测试方法。
     */

    @Benchmark
    public void measureRight() {
        x++;
    }

    /* 这个方法执行到check()时一定会报错，因为每轮测试时我们修改的都是局部变量x。
     */

    @Benchmark
    public void measureWrong() {
        double x = 0;
        x++;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_05_StateFixtures.class.getSimpleName())
                .forks(1)
                .jvmArgs("-ea")
                .build();
        new Runner(opt).run();
    }
}
```
:::

### FixureLevel：状态生命周期方法被执行多少次？

上一节讲到了@Setup和@Teardown，被它们标记的方法分别会在基准测试方法执行前和执行后执行。但是无论是基于时间还是次数的基准测试，往往都会执行很多次才能统计出一个较为准确的结果。

那么这些 Fixtures 方法究竟何时执行，执行多少次呢？我们可以通过 FixureLevel 来进行配置，Fixure方法在运行时一共可以分为三个等级：

1. Level.Trial：整个基准测试（一个@Benchmark注解为一个基准测试）运行之前或之后（多个迭代）
2. Level.Iteration：在基准测试迭代之前或之后（每一轮迭代）
3. Level.Invocation：在每次基准测试方法调用之前或之后。（每一次调用调用）

::: details 代码示例
```java
@State(Scope.Thread)
public class JMHSample_06_FixtureLevel {

    double x;

    @TearDown(Level.Iteration)
    public void check() {
        assert x > Math.PI : "Nothing changed?";
    }

    @Benchmark
    public void measureRight() {
        x++;
    }

    @Benchmark
    public void measureWrong() {
        double x = 0;
        x++;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_06_FixtureLevel.class.getSimpleName())
                .forks(1)
                .jvmArgs("-ea")
                .shouldFailOnError(false) // switch to "true" to fail the complete run
                .build();

        new Runner(opt).run();
    }
}
```
:::

### FixureLevel: 慎用Invocation

需要注意的是 对于 Leavel.Invocation 等级下的**时间戳**和**方法同步调用**可能会明显影响测试报告结果值，一定要谨慎使用。

这个等级只适用于每次benchmark方法执行时间超过1ms的场景， 在特别的基础上验证对您的案例的影响也是一个好主意???

1. 由于我们必须从基准测试时间中减去setup和teardown方法的耗时，在这个级别上，我们必须为每次基准测试方法调用设置时间戳。如果基准测试方法耗时很短，那么大量的时间戳请求会使系统饱和，这将引入虚假的延迟、吞吐量和可伸缩性瓶颈。
   
2. 由于我们使用此级别测量单个调用时间，因此我们可能会为（协调的）遗漏做好准备。这意味着测量中的小问题可以从时序测量中隐藏起来，并可能带来令人惊讶的结果。例如，当我们使用时序来理解基准吞吐量时，省略的时序测量将导致较低的聚合时间，并虚构更大的吞吐量。

3. 为了维持与其他级别相同的共享行为，我们有时必须同步的方式对{@link State}对象的访问。其他级别下这样做可能对测量结果影响不大，但是在这个级别，我们必须在关键方法上同步，增加同步等待时间，使得测量结果偏差很大。

4. 目前的实现允许在这个级别的辅助方法执行与基准调用本身重叠，在多线程基准测试中，当一个执行{@link Benchmark}方法的工作线程执行时，可能会得到的是状态数据被其他线程执行了{@link TearDown}方法。
   
::: details 代码示例
```java
/**
 * Fixtures have different Levels to control when they are about to run.
 * Level.Invocation is useful sometimes to do some per-invocation work
 * which should not count as payload (e.g. sleep for some time to emulate
 * think time)
 */
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class JMHSample_07_FixtureLevelInvocation {

    /*
     * Fixtures have different Levels to control when they are about to run.
     * Level.Invocation is useful sometimes to do some per-invocation work,
     * which should not count as payload. PLEASE NOTE the timestamping and
     * synchronization for Level.Invocation helpers might significantly offset
     * the measurement, use with care. See Level.Invocation javadoc for further
     * discussion.
     *
     * Consider this sample:
     */

    /*
     * This state handles the executor.
     * Note we create and shutdown executor with Level.Trial, so
     * it is kept around the same across all iterations.
     */

    @State(Scope.Benchmark)
    public static class NormalState {
        ExecutorService service;

        @Setup(Level.Trial)
        public void up() {
            service = Executors.newCachedThreadPool();
        }

        @TearDown(Level.Trial)
        public void down() {
            service.shutdown();
        }

    }

    /*
     * This is the *extension* of the basic state, which also
     * has the Level.Invocation fixture method, sleeping for some time.
     */

    public static class LaggingState extends NormalState {
        public static final int SLEEP_TIME = Integer.getInteger("sleepTime", 10);

        @Setup(Level.Invocation)
        public void lag() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(SLEEP_TIME);
        }
    }

    /*
     * This allows us to formulate the task: measure the task turnaround in
     * "hot" mode when we are not sleeping between the submits, and "cold" mode,
     * when we are sleeping.
     */

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public double measureHot(NormalState e, final Scratch s) throws ExecutionException, InterruptedException {
        return e.service.submit(new Task(s)).get();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public double measureCold(LaggingState e, final Scratch s) throws ExecutionException, InterruptedException {
        return e.service.submit(new Task(s)).get();
    }

    /*
     * This is our scratch state which will handle the work.
     */

    @State(Scope.Thread)
    public static class Scratch {
        private double p;
        public double doWork() {
            p = Math.log(p);
            return p;
        }
    }

    public static class Task implements Callable<Double> {
        private Scratch s;

        public Task(Scratch s) {
            this.s = s;
        }

        @Override
        public Double call() {
            return s.doWork();
        }
    }

    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     *
     * You can see the cold scenario is running longer, because we pay for
     * thread wakeups.
     *
     * You can run this test:
     *
     * a) Via the command line:
     *    $ mvn clean install
     *    $ java -jar target/benchmarks.jar JMHSample_07 -f 1
     *    (we requested single fork; there are also other options, see -h)
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_07_FixtureLevelInvocation.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

}

```
:::
## 避免 JVM 优化
很多情况下基准测试的失败，都是由JVM优化导致的。所以在进行基准测试时，我们必须要考虑JVM优化对我们测试方法的影响。
### @Forking
众所周知，JVM 擅长配置 profile-guided 的化。但是这对进准测试并不友好，因为不同的测试可以将它们配置混合在一起， 然后为每个测试呈现一致的异常的结果。

forking（运行在不同的进程中）每个测试都可以帮助规避这个问题。

::: warning
使用non-forked运行仅用于调试目的，而不是用于实际基准测试，JMH默认会fork所有的test。
:::

::: details 代码示例
```java
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JMHSample_12_Forking {

    /*
     * JVMs are notoriously good at profile-guided optimizations. This is bad
     * for benchmarks, because different tests can mix their profiles together,
     * and then render the "uniformly bad" code for every test. Forking (running
     * in a separate process) each test can help to evade this issue.
     * 
     * JMH will fork the tests by default.
     *
     * JMH默认fork所有test。
     */

    /*
     * Suppose we have this simple counter interface, and two implementations.
     * Even though those are semantically the same, from the JVM standpoint,
     * those are distinct classes.
     *
     * 假设我们又一个简单的统计接口，并且有两个实现。
     * 即使他们逻辑相同，站在JVM角度看他们是不同的类。
     */

    public interface Counter {
        int inc();
    }

    public class Counter1 implements Counter {
        private int x;

        @Override
        public int inc() {
            return x++;
        }
    }

    public class Counter2 implements Counter {
        private int x;

        @Override
        public int inc() {
            return x++;
        }
    }

    /*
     * And this is how we measure it.
     * Note this is susceptible for same issue with loops we mention in previous examples.
     */

    public int measure(Counter c) {
        int s = 0;
        for (int i = 0; i < 10; i++) {
            s += c.inc();
        }
        return s;
    }

    /*
     * These are two counters.
     */

    Counter c1 = new Counter1();
    Counter c2 = new Counter2();

    /*
     * We first measure the Counter1 alone...
     * Fork(0) helps to run in the same JVM.
     */

    @Benchmark
    @Fork(0)
    public int measure_1_c1() {
        return measure(c1);
    }

    /*
     * Then Counter2...
     */

    @Benchmark
    @Fork(0)
    public int measure_2_c2() {
        return measure(c2);
    }

    /*
     * Then Counter1 again...
     */

    @Benchmark
    @Fork(0)
    public int measure_3_c1_again() {
        return measure(c1);
    }

    /*
     * These two tests have explicit @Fork annotation.
     * JMH takes this annotation as the request to run the test in the forked JVM.
     * It's even simpler to force this behavior for all the tests via the command
     * line option "-f". The forking is default, but we still use the annotation
     * for the consistency.
     *
     * This is the test for Counter1.
     */

    @Benchmark
    @Fork(1)
    public int measure_4_forked_c1() {
        return measure(c1);
    }

    /*
     * ...and this is the test for Counter2.
     */

    @Benchmark
    @Fork(1)
    public int measure_5_forked_c2() {
        return measure(c2);
    }

    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     *
     * Note that C1 is faster, C2 is slower, but the C1 is slow again! This is because
     * the profiles for C1 and C2 had merged together.
     *
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_12_Forking.class.getSimpleName())
                .output("JMHSample_12_Forking.sampleLog")
                .build();

        new Runner(opt).run();
    }

}
```
:::

### RunToRun 
Forking 还允许预估每次运行之间的的差异。毕竟 JVM 是个非常复杂的系统，所以它包含了很多不确定性。

这就要求我们始终将运行间的差异的视为我们实验中的影响之一，幸运的是，foking 模式下聚合了多个 JVM 运行的结果。

为了引入易于量化的运行间差异，我们建立了工作负载（同时运行多个进程），其性能在运行间有所不同。请注意，许多工作负载会有类似的行为，但我们人为地这样做是为了说明一个问题。

::: details 代码示例
```java

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class JMHSample_13_RunToRun {

    /*
     * Forking also allows to estimate run-to-run variance.
     *
     * JVMs are complex systems, and the non-determinism is inherent for them.
     * This requires us to always account the run-to-run variance as the one
     * of the effects in our experiments.
     *
     * Luckily, forking aggregates the results across several JVM launches.
     */

    /*
     * In order to introduce readily measurable run-to-run variance, we build
     * the workload which performance differs from run to run. Note that many workloads
     * will have the similar behavior, but we do that artificially to make a point.
     */

    @State(Scope.Thread)
    public static class SleepyState {
        public long sleepTime;

        @Setup
        public void setup() {
            sleepTime = (long) (Math.random() * 1000);
        }
    }

    /*
     * Now, we will run this different number of times.
     */

    @Benchmark
    @Fork(1)
    public void baseline(SleepyState s) throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(s.sleepTime);
    }

    @Benchmark
    @Fork(5)
    public void fork_1(SleepyState s) throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(s.sleepTime);
    }

    @Benchmark
    @Fork(20)
    public void fork_2(SleepyState s) throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(s.sleepTime);
    }

    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     *
     * Note the baseline is random within [0..1000] msec; and both forked runs
     * are estimating the average 500 msec with some confidence.
     *
     * You can run this test:
     *
     * a) Via the command line:
     *    $ mvn clean install
     *    $ java -jar target/benchmarks.jar JMHSample_13 -wi 0 -i 3
     *    (we requested no warmup, 3 measurement iterations; there are also other options, see -h)
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_13_RunToRun.class.getSimpleName())
                .warmupIterations(0)
                .measurementIterations(3)
                .build();

        new Runner(opt).run();
    }

}
```
:::

### DeadCode：编译器优化
许多基准测试的失败是因为Dead-Code的消除：编译器非常智能可以推断出一些冗余的计算并彻底消除。如果被消除的部分是我们的基准测试代码，我们的基准测试将是无效的。

如下示例代码，
::: details 代码示例
```java
package org.openjdk.jmh.samples;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JMHSample_08_DeadCode {

    /*
     * The downfall of many benchmarks is Dead-Code Elimination (DCE): compilers
     * are smart enough to deduce some computations are redundant and eliminate
     * them completely. If the eliminated part was our benchmarked code, we are
     * in trouble.
     *
     * Fortunately, JMH provides the essential infrastructure to fight this
     * where appropriate: returning the result of the computation will ask JMH
     * to deal with the result to limit dead-code elimination (returned results
     * are implicitly consumed by Blackholes, see JMHSample_09_Blackholes).
     */

    private double x = Math.PI;

    @Benchmark
    public void baseline() {
        // do nothing, this is a baseline
    }
    // 错误示例：由于计算结果没有被使用，所以整个计算过程被优化（忽略），执行效果和baseLine()方法一样
    @Benchmark
    public void measureWrong() {
        // This is wrong: result is not used and the entire computation is optimized away.
        Math.log(x);
    }

    @Benchmark
    public double measureRight() {
        // This is correct: the result is being used.
        return Math.log(x);
    }

    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     *
     * You can see the unrealistically fast calculation in with measureWrong(),
     * while realistic measurement with measureRight().
     *
     * You can run this test:
     *
     * a) Via the command line:
     *    $ mvn clean install
     *    $ java -jar target/benchmarks.jar JMHSample_08 -f 1
     *    (we requested single fork; there are also other options, see -h)
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_08_DeadCode.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

}
```
:::

### BlackHole：防止DeadCode被优化
幸运的是，JMH提供了必要的基础设施来应对这一情况，返回计算结果将要求JMH处理结果以限制死代码消除。

首选需要确认的是你的基准测试是否返回多个结果，如果你只会产生一个结果，应该使用更易读的明确return，就像JMHSample_08_DeadCode。不要使用明确的Blackholes来降低您的基准代码的可读性！

::: details 代码示例
```java
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class JMHSample_09_Blackholes {

    double x1 = Math.PI;
    double x2 = Math.PI * 2;

    /*
     * Baseline measurement: how much single Math.sampleLog costs.
     */

    @Benchmark
    public double baseline() {
        return Math.log(x1);
    }

    /*
     * 虽然Math.log(x2)计算是完整的，但Math.log(x1)是冗余的，并被优化了。
     */

    @Benchmark
    public double measureWrong() {
        Math.log(x1);
        return Math.log(x2);
    }

    /*
     * 选择一: 合并多个结果并返回，这在Math.log()方法计算量比较大的情况下是可以的，合并结果不会对结果产生太大影响。
     */

    @Benchmark
    public double measureRight_1() {
        return Math.log(x1) + Math.log(x2);
    }

    /*
     * 选择二: 显示使用Blackhole对象，并将结果值传入Blackhole.consume方法中。
     * （Blackhole就像@State对象的一种特殊实现，JMH自动绑定）
     */

    @Benchmark
    public void measureRight_2(Blackhole bh) {
        bh.consume(Math.log(x1));
        bh.consume(Math.log(x2));
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_09_Blackholes.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
```
:::
### BlackHole#ConsumeCPU：消耗CPU的时钟周期
有时我们可能仅仅需要测试消耗CPU资源，这时也可以通过  Blackhole.consumeCPU方法实现。

::: details 代码示例
```java
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JMHSample_21_ConsumeCPU {

    /*
     * At times you require the test to burn some of the cycles doing nothing.
     * In many cases, you *do* want to burn the cycles instead of waiting.
     *
     * For these occasions, we have the infrastructure support. Blackholes
     * can not only consume the values, but also the time! Run this test
     * to get familiar with this part of JMH.
     *
     * (Note we use static method because most of the use cases are deep
     * within the testing code, and propagating blackholes is tedious).
     */

    @Benchmark
    public void consume_0000() {
        Blackhole.consumeCPU(0);
    }

    @Benchmark
    public void consume_0001() {
        Blackhole.consumeCPU(1);
    }

    @Benchmark
    public void consume_0002() {
        Blackhole.consumeCPU(2);
    }

    @Benchmark
    public void consume_0004() {
        Blackhole.consumeCPU(4);
    }

    @Benchmark
    public void consume_0008() {
        Blackhole.consumeCPU(8);
    }

    @Benchmark
    public void consume_0016() {
        Blackhole.consumeCPU(16);
    }

    @Benchmark
    public void consume_0032() {
        Blackhole.consumeCPU(32);
    }

    @Benchmark
    public void consume_0064() {
        Blackhole.consumeCPU(64);
    }

    @Benchmark
    public void consume_0128() {
        Blackhole.consumeCPU(128);
    }

    @Benchmark
    public void consume_0256() {
        Blackhole.consumeCPU(256);
    }

    @Benchmark
    public void consume_0512() {
        Blackhole.consumeCPU(512);
    }

    @Benchmark
    public void consume_1024() {
        Blackhole.consumeCPU(1024);
    }
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_21_ConsumeCPU.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

}
```
:::

### ConstantFold 常量折叠
死代码消除的另一种形式是常量折叠。

如果JVM发现不管怎么计算，计算结果都是不变的，它可以巧妙地优化它。在我们的case中，这就意味着我们可以把计算移到JMH之外。

可以通过@State对象的非final类型的字段读取输入，根据这些值计算结果，遵守这些规则就能防止DeadCode。

::: details 代码示例
```java
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JMHSample_10_ConstantFold {

 
    private double x = Math.PI;

    private final double wrongX = Math.PI;

    @Benchmark
    public double baseline() {
        // 基础case：简单返回一个常量结果
        return Math.PI;
    }

    @Benchmark
    public double measureWrong_1() {
        // 错误示例: 结果可预测，计算会被折叠 「参数为常量」
        return Math.log(Math.PI);
    }

    @Benchmark
    public double measureWrong_2() {
        // 错误示例: 结果可预测，计算会被折叠 「参数为常量」
        return Math.log(wrongX);
    }

    @Benchmark
    public double measureRight() {
        // 正确示例: 结果不可预测，参数作为变量传入
        return Math.log(x);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_10_ConstantFold.class.getSimpleName())
                .forks(1)
                .output("JMHSample_10_ConstantFold.sampleLog")
                .build();
        new Runner(opt).run();
    }
}
```
:::

### CompilerController： 编译控制
方法内联（Method Inlining），是JVM对代码的编译优化，常见的编译优化可以从参考http://www.importnew.com/2009.html
Java编程语言中虚拟方法调用的频率是一个重要的优化瓶颈。一旦Java HotSpot自适应优化器在执行期间收集有关程序热点的信息，它不仅将热点编译为本机代码，而且还对该代码执行大量方法内联。
内联有很多好处。 它大大降低了方法调用的动态频率，从而节省了执行这些方法调用所需的时间。但更重要的是，内联会产生更大的代码块供优化器处理，这就大大提高了传统编译器优化的效率，克服了提高Java编程语言性能的主要障碍。
内联与其他代码优化是协同的，因为它使它们更有效。随着Java HotSpot编译器的成熟，对大型内联代码块进行操作的能力将为未来一系列更高级的优化打开大门。

关于编译控制，JMH提供了三个选项供我们选择：TODO
1. CompilerControl.Mode.DONT_INLINE：
2. CompilerControl.Mode.INLINE：
3. CompilerControl.Mode.EXCLUDE：
我们使用HotSpot特定的功能来告诉编译器我们想对特定的方法做什么， 为了演示效果，我们在这个例子中写了三个测试方法。
::: details 代码示例
```java
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JMHSample_16_CompilerControl {

    /*
     * We can use HotSpot-specific functionality to tell the compiler what
     * do we want to do with particular methods. To demonstrate the effects,
     * we end up with 3 methods in this sample.
     *
     * 我们使用HotSpot特定的功能来告诉编译器我们想对特定的方法做怎么。
     * 为了证明这种效果，我们在这个例子中写了三个测试方法。
     */

    /**
     *
     * 这是我们的目标：
     *  - 第一个方法禁止内敛
     *  - 第二个方法强制内敛
     *  - 第三个方法禁止编译
     *
     * 我们甚至可以将注释直接放在基准测试方法中，但这更清楚地表达了意图。
     */

    public void target_blank() {
        // this method was intentionally left blank
        // 方法故意留空
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void target_dontInline() {
        // this method was intentionally left blank
    }

    @CompilerControl(CompilerControl.Mode.INLINE)
    public void target_inline() {
        // this method was intentionally left blank
    }

    /**
     * Exclude the method from the compilation.
     */
    @CompilerControl(CompilerControl.Mode.EXCLUDE)
    public void target_exclude() {
        // this method was intentionally left blank
    }

    /*
     * These method measures the calls performance.
     * 这些方法来测量调用性能。
     */

    @Benchmark
    public void baseline() {
        // this method was intentionally left blank
    }

    @Benchmark
    public void blank() {
        target_blank();
    }

    @Benchmark
    public void dontinline() {
        target_dontInline();
    }

    @Benchmark
    public void inline() {
        target_inline();
    }

    @Benchmark
    public void exclude() {
        target_exclude();
    }
}
```
:::

## Loops 循环
把你的基准代码放在你的基准方法中的一个循环里是很诱人的，以便在每次调用基准方法时重复更多的次数（以减少基准方法调用的开销）。然而，JVM非常善于优化循环，所以你最终得到的结果可能与你预期的不同。一般来说，你应该避免基准方法中的循环，除非它们是你想测量的代码的一部分（而不是在你想测量的代码周围）。
::: details 代码示例
```java
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JMHSample_11_Loops {

    /*
     * 假设我们要测试两个整数相加的性能
     */

    int x = 1;
    int y = 2;

    /*
     * This is what you do with JMH.
     */

    @Benchmark
    public int measureRight() {
        return (x + y);
    }

    /*
     * The following tests emulate the naive looping.
     * This is the Caliper-style benchmark.
     *
     * Caliper风格的基准测试
     */

    private int reps(int reps) {
        int s = 0;
        for (int i = 0; i < reps; i++) {
            s += (x + y);
        }
        return s;
    }

    /*
     * We would like to measure this with different repetitions count.
     * Special annotation is used to get the individual operation cost.
     */

    @Benchmark
    @OperationsPerInvocation(1)
    public int measureWrong_1() {
        return reps(1);
    }

    @Benchmark
    @OperationsPerInvocation(10)
    public int measureWrong_10() {
        return reps(10);
    }

    @Benchmark
    @OperationsPerInvocation(100)
    public int measureWrong_100() {
        return reps(100);
    }

    @Benchmark
    @OperationsPerInvocation(1000)
    public int measureWrong_1000() {
        return reps(1000);
    }

    @Benchmark
    @OperationsPerInvocation(10000)
    public int measureWrong_10000() {
        return reps(10000);
    }

    @Benchmark
    @OperationsPerInvocation(100000)
    public int measureWrong_100000() {
        return reps(100000);
    }

    /*
     *
     * 你可能已经注意到，循环次数越多，统计出来的时间越短。到目前为止，每次操作的时间已经是1/20 ns，远远超过硬件的实际能力。
     *
     * 发生这种情况是因为循环被大量展开。所以：不要过度使用循环，依靠JMH来正确测量。
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_11_Loops.class.getSimpleName())
                .forks(1)
                .output("JMHSample_11_Loops.sampleLog")
                .build();

        new Runner(opt).run();
    }

}
```
:::

### @OperationsPerInvocation
OperationsPerInvocation注解可以让基准测试进行不止一个操作，并让JMH适当地调整结果。

例如，一个使用内部循环的基准方法有多个操作，需要想测量单个操作的性能。通常OperationsPerInvocation设置值一般和for循环的次数一致。

```java
    @Benchmark
    @OperationsPerInvocation(10)
    public void test() {
         for (int i = 0; i < 10; i++) {
             // do something
         }
    }
```   
### BadCase：循环展开
循环展开能够降低循环开销，为具有多个功能单元的处理器提供指令级并行，也有利于指令流水线的调度。例如：
原始代码：
```java
     for (i = 1; i <= 60; i++) {
        a[i] = a[i] * b + c;
     }
```
展开后实际执行的代码：
```java
     for (i = 1; i <= 60; i+=3) {
       a[i] = a[i] * b + c;
       a[i+1] = a[i+1] * b + c;
       a[i+2] = a[i+2] * b + c;
     }
```

### SafeLooping
JMHSample_11_Loops 示例介绍了我们在 @Benchmark 方法中使用循环的危险性。然而，有时需要遍历数据集中的多个元素。没有循环就很难做到这一点，因此我们需要设计一个方案安全循环。

如何在基准测试中安全地循环？我们只需要进行简单地控制，通过检查基准成本随着任务规模的增加而线性增长，如果不是，那么就说明发生了"错误"。

假设我们要测试在不同的参数下，work()方法执行耗时情况。这里模拟了一个常见的用例，使用不同的参数调对同相同的方法实现进行测试。
::: details 代码示例
```java
@State(Scope.Thread)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JMHSample_34_SafeLooping {

    /*
     * JMHSample_11_Loops warns about the dangers of using loops in @Benchmark methods.
     * Sometimes, however, one needs to traverse through several elements in a dataset.
     * This is hard to do without loops, and therefore we need to devise a scheme for
     * safe looping.
     */

    /*
     * Suppose we want to measure how much it takes to execute work() with different
     * arguments. This mimics a frequent use case when multiple instances with the same
     * implementation, but different data, is measured.
     */

    static final int BASE = 42;

    static int work(int x) {
        return BASE + x;
    }

    /*
     * Every benchmark requires control. We do a trivial control for our benchmarks
     * by checking the benchmark costs are growing linearly with increased task size.
     * If it doesn't, then something wrong is happening.
     */

    @Param({"1", "10", "100", "1000"})
    int size;

    int[] xs;

    @Setup
    public void setup() {
        xs = new int[size];
        for (int c = 0; c < size; c++) {
            xs[c] = c;
        }
    }

    /*
     * First, the obviously wrong way: "saving" the result into a local variable would not
     * work. A sufficiently smart compiler will inline work(), and figure out only the last
     * work() call needs to be evaluated. Indeed, if you run it with varying $size, the score
     * will stay the same!
     *
     * 首先，明显错误的方式：将结果“保存”到局部变量中是行不通的。
     * 一个足够聪明的编译器将内联 work()方法，只需要测试最后一次 work() 调用即可。事实上，如果你用不同的 $size 运行它，结果都是一样的！
     */

    @Benchmark
    public int measureWrong_1() {
        int acc = 0;
        for (int x : xs) {
            acc = work(x);
        }
        return acc;
    }

    /*
     * Second, another wrong way: "accumulating" the result into a local variable. While
     * it would force the computation of each work() method, there are software pipelining
     * effects in action, that can merge the operations between two otherwise distinct work()
     * bodies. This will obliterate the benchmark setup.
     *
     * In this example, HotSpot does the unrolled loop, merges the $BASE operands into a single
     * addition to $acc, and then does a bunch of very tight stores of $x-s. The final performance
     * depends on how much of the loop unrolling happened *and* how much data is available to make
     * the large strides.
     *
     * 另外一种错误方式就是将结果累加到一个局部变量中。虽然他会强制计算每一个work（）方法，但是由于软件流水线的作用，可以合并两个原本不同的的work()之间的不同操作。这将使得基准测试的设置失效。
     * 
     * 这个例子中，HotSpot 执行展开循环，将 $BASE操作数合并一个操作加到$acc中，然后对$x-s做了一些非常紧凑的压缩。
     * 最终的性能取决于有多少循环被展开以及有多少数据可以用来做大跨度。张开的原理示意可以见上一小节「循环展开」
     */

    @Benchmark
    public int measureWrong_2() {
        int acc = 0;
        for (int x : xs) {
            acc += work(x);
        }
        return acc;
    }

    /*
     * Now, let's see how to measure these things properly. A very straight-forward way to
     * break the merging is to sink each result to Blackhole. This will force runtime to compute
     * every work() call in full. (We would normally like to care about several concurrent work()
     * computations at once, but the memory effects from Blackhole.consume() prevent those optimization
     * on most runtimes).
     * 
     * 现在，让我们看看如何正确测量这些东西。打破合并的一个非常直接的方法是将每个结果下沉到黑洞。
     * 这将迫使运行时完整地计算每个work()调用。我们通常希望同时关注多个并发 work() 计算，但是 Blackhole.consume() 的内存效应阻止了大多数运行时的优化）。
     */

    @Benchmark
    public void measureRight_1(Blackhole bh) {
        for (int x : xs) {
            bh.consume(work(x));
        }
    }

    /*
     * 注意事项：DANGEROUS AREA, PLEASE READ THE DESCRIPTION BELOW.
     *
     * Sometimes, the cost of sinking the value into a Blackhole is dominating the nano-benchmark score.
     * In these cases, one may try to do a make-shift "sinker" with non-inlineable method. This trick is
     * *very* VM-specific, and can only be used if you are verifying the generated code (that's a good
     * strategy when dealing with nano-benchmarks anyway).
     *
     * You SHOULD NOT use this trick in most cases. Apply only where needed.
     *
     * 有时，将值放入 Blackhole 的成本在**纳秒级别的基准测试**中占比很高，会影响测试结果。
     * 在这些情况下，人们可能会尝试使用不可内联的方法来做一个临时的“sinker” 。这个技巧是针对虚拟机的，只有在你验证生成的代码时才能使用（无论如何，在处理纳米基准时这是一个好的策略）。
     * 
     * 注意：在大多数情况下，你不应该使用这个技巧。只在需要的时候使用。
     */

    @Benchmark
    public void measureRight_2() {
        for (int x : xs) {
            sink(work(x));
        }
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public static void sink(int v) {
        // IT IS VERY IMPORTANT TO MATCH THE SIGNATURE TO AVOID AUTOBOXING.
        // The method intentionally does nothing.
    }


    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     *
     * You might notice measureWrong_1 does not depend on $size, measureWrong_2 has troubles with
     * linearity, and otherwise much faster than both measureRight_*. You can also see measureRight_2
     * is marginally faster than measureRight_1.
     * 
     * 从执行结果中可以发现，measureWrong_1的执行结果不依赖于 $size参数，而measureWrong_2 的执行性能不是增长，否则比 measureRight_ 快得多。
     * 您还可以看到 measureRight_2 略快于 measureRight_1。
     * 
     * You can run this test:
     *
     * a) Via the command line:
     *    $ mvn clean install
     *    $ java -jar target/benchmarks.jar JMHSample_34
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_34_SafeLooping.class.getSimpleName())
                .forks(3)
                .build();

        new Runner(opt).run();
    }

}
```
:::
 ::: tip Tips
    1. 观察测量结果是否随测量数据规模线性增长？如果不是，则可能有问题
    2. 使用 BlackHole 防止JVM循环展开优化
    3. 对于`纳秒`级的基准测试，可以会尝试使用不可内联的方法来做一个临时的“sinker"，必要时可以这样做！
 :::

## Asymmetric：非对称试验
目前为止，我们的测试都是对称的：所有的线程都运行相同的代码。接下来我们一起学习一下非对称测试。

JMH引入了Group的概念，并提供了@Group注解来把几个方法绑定到一起，所有线程都分布在测试方法中。

每个执行组包含一个或者多个线程。特定执行组中的每个线程执行一个@Group标记的 benchmark方法，多个执行组可以参与运行时，运行中的总线程数将四舍五入为执行组大小，这将保证所有执行组都是完整的。

注意那两个状态的作用域：Scope.Benchmark 和 Scope.Thread没有在这个用例中覆盖，表明你要么在状态中共享每个东西，要么不共享。我们使用Scope.Group状态用来表明在执行组内共享，而不在组间共享。

::: details 代码示例
```java
@State(Scope.Group)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JMHSample_15_Asymmetric {

    /*
     * So far all the tests were symmetric: the same code was executed in all the threads.
     * At times, you need the asymmetric test. JMH provides this with the notion of @Group,
     * which can bind several methods together, and all the threads are distributed among
     * the test methods.
     *
     * Each execution group contains of one or more threads. Each thread within a particular
     * execution group executes one of @Group-annotated @Benchmark methods. Multiple execution
     * groups may participate in the run. The total thread count in the run is rounded to the
     * execution group size, which will only allow the full execution groups.
     *
     * Note that two state scopes: Scope.Benchmark and Scope.Thread are not covering all
     * the use cases here -- you either share everything in the state, or share nothing.
     * To break this, we have the middle ground Scope.Group, which marks the state to be
     * shared within the execution group, but not among the execution groups.
     *
     * Putting this all together, the example below means:
     *  a) define the execution group "g", with 3 threads executing inc(), and 1 thread
     *     executing get(), 4 threads per group in total;
     *  b) if we run this test case with 4 threads, then we will have a single execution
     *     group. Generally, running with 4*N threads will create N execution groups, etc.;
     *  c) each execution group has one @State instance to share: that is, execution groups
     *     share the counter within the group, but not across the groups.
     *
     * 到目前位置，我们的测试都是对称的：所有的线程都运行相同的代码。
     * 是时候了解非对称测试了。JMH提供了@Group注解来把几个方法绑定到一起，所有线程都分布在测试方法中。
     *
     * 每个执行组包含一个或者多个线程。特定执行组中的每个线程执行一个@Group标记的 benchmark方法
     * 多个执行组可以参与运行时，运行中的总线程数将四舍五入为执行组大小，这将保证所有执行组都是完整的。
     *
     * 注意那两个状态的作用域：Scope.Benchmark 和 Scope.Thread没有在这个用例中覆盖，表明你要么在状态中共享每个东西，要么不共享。我们使用Scope.Group状态用来表明在执行组内共享，而不在组间共享。
     *
     * 以下事例含义：
     *  a)定义执行组"g"，它有3个线程来执行inc()，1个线程来执行get()，每个分组共有4个线程；
     *  b)如果我们用4个线程来运行这个测试用例，我们将会有一个单独的执行组。通常，用4*N个线程来创建N个执行组。
     *  c)每个执行组内共享一个@State实例：也就是执行组内共享counter，而不是跨组共享。
     */

    private AtomicInteger counter;

    @Setup
    public void up() {
        counter = new AtomicInteger();
    }

    @Benchmark
    @Group("g")
    @GroupThreads(3)
    public int inc() {
        return counter.incrementAndGet();
    }

    @Benchmark
    @Group("g")
    @GroupThreads(1)
    public int get() {
        return counter.get();
    }

    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     *
     * You will have the distinct metrics for inc() and get() from this run.
     *
     * 在此次运行中我们讲分别获得inc()和get()的指标。
     *
     * You can run this test:
     *
     * a) Via the command line:
     *    $ mvn clean install
     *    $ java -jar target/benchmarks.jar JMHSample_15 -f 1
     *    (we requested single fork; there are also other options, see -h)
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_15_Asymmetric.class.getSimpleName())
                .forks(1)
                .output("JMHSample_15_Asymmetric.sampleLog")
                .build();

        new Runner(opt).run();
    }

}
```
:::


 ## SyncIterations
事实证明如果你用多线程来跑benchmark，你启动和停止工作线程的方式会严重影响性能。

通常的做法是，让所有的线程都挂起在一些有序的屏障上，然后让他们一起开始。
然而，这种做法是不奏效的：没有谁能够保证工作线程在同一时间开始，这就意味着其他工作线程在更好的条件下运行，从而扭曲了结果。

更好的解决方案是引入虚假迭代，增加执行迭代的线程，然后将系统自动切换为测试任务。在减速期间可以做同样的事情，这听起来很复杂，但是JMH已经帮你处理好了。

syncIterations设置为true时，先让线程池预热，都预热完成后让所有线程同时进行基准测试，测试完等待所有线程都结束再关闭线程池。这样能够更加真实的模拟线上多线程并发执行的情况。
::: details 代码示例
```java
@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class JMHSample_17_SyncIterations {

    /*
     * This is the another thing that is enabled in JMH by default.
     *
     * Suppose we have this simple benchmark.
     */

    private double src;

    @Benchmark
    public double test() {
        double s = src;
        for (int i = 0; i < 1000; i++) {
            s = Math.sin(s);
        }
        return s;
    }

    /*
     * It turns out if you run the benchmark with multiple threads,
     * the way you start and stop the worker threads seriously affects
     * performance.
     *
     * The natural way would be to park all the threads on some sort
     * of barrier, and the let them go "at once". However, that does
     * not work: there are no guarantees the worker threads will start
     * at the same time, meaning other worker threads are working
     * in better conditions, skewing the result.
     *
     * The better solution would be to introduce bogus iterations,
     * ramp up the threads executing the iterations, and then atomically
     * shift the system to measuring stuff. The same thing can be done
     * during the rampdown. This sounds complicated, but JMH already
     * handles that for you.
     *
     */

    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     *
     * You will need to oversubscribe the system to make this effect
     * clearly visible; however, this effect can also be shown on the
     * unsaturated systems.*
     *
     * Note the performance of -si false version is more flaky, even
     * though it is "better". This is the false improvement, granted by
     * some of the threads executing in solo. The -si true version more stable
     * and coherent.
     *
     * -si true is enabled by default.
     *
     * Say, $CPU is the number of CPUs on your machine.
     *
     * You can run this test with:
     *
     * a) Via the command line:
     *    $ mvn clean install
     *    $ java -jar target/benchmarks.jar JMHSample_17 \
     *        -w 1s -r 1s -f 1 -t ${CPU*16} -si {true|false}
     *    (we requested shorter warmup/measurement iterations, single fork,
     *     lots of threads, and changeable "synchronize iterations" option)
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_17_SyncIterations.class.getSimpleName())
                .warmupTime(TimeValue.seconds(1))
                .measurementTime(TimeValue.seconds(1))
                .threads(Runtime.getRuntime().availableProcessors()*16)
                .forks(1)
                .syncIterations(true) // try to switch to "false"
                .build();

        new Runner(opt).run();
    }

}
```
:::

##  Annotations：配置基准测试

除了运行时所有的命令行选项外，我们还可以通过注解给一些基准测试提供默认值。在你处理大量基准测试时这个很有用，其中一些需要特别处理。

注解可以放在class上，来影响这个class中所有的基准测试方法。规则是，靠近作用域的注解有优先权：比如，方法上的注解可以覆盖类上的注解；命令行优先级最高。

::: details 代码示例
```java
@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(1)
public class JMHSample_20_Annotations {

    double x1 = Math.PI;

    /*
     * In addition to all the command line options usable at run time,
     * we have the annotations which can provide the reasonable defaults
     * for the some of the benchmarks. This is very useful when you are
     * dealing with lots of benchmarks, and some of them require
     * special treatment.
     *
     * Annotation can also be placed on class, to have the effect over
     * all the benchmark methods in the same class. The rule is, the
     * annotation in the closest scope takes the precedence: i.e.
     * the method-based annotation overrides class-based annotation,
     * etc.
     */

    @Benchmark
    @Warmup(iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS)
    public double measure() {
        return Math.log(x1);
    }

    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     *
     * Note JMH honors the default annotation settings. You can always override
     * the defaults via the command line or API.
     *
     * You can run this test:
     *
     * a) Via the command line:
     *    $ mvn clean install
     *    $ java -jar target/benchmarks.jar JMHSample_20
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_20_Annotations.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }

}
```
:::


## FalseSharing 

伪共享引发的错误可能让你大吃一惊。若干两个线程访问内存中两个相邻的值，它们很可能修改的是同一缓存行上的值，这就导致程序的执行明显变慢。

JMH能够使用@State自动填充解决这个问题。但是这种填充没有在@State内部实现，需要开发手动处理。


::: details 代码示例
```java
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
public class JMHSample_22_FalseSharing {

    /*
     * Suppose we have two threads:
     *   a) innocuous reader which blindly reads its own field
     *   b) furious writer which updates its own field
     */

    /*
     * BASELINE EXPERIMENT:
     * Because of the false sharing, both reader and writer will experience
     * penalties.
     */

    @State(Scope.Group)
    public static class StateBaseline {
        int readOnly;
        int writeOnly;
    }

    @Benchmark
    @Group("baseline")
    public int reader(StateBaseline s) {
        return s.readOnly;
    }

    @Benchmark
    @Group("baseline")
    public void writer(StateBaseline s) {
        s.writeOnly++;
    }

    /*
     * APPROACH 1: PADDING
     *
     * We can try to alleviate some of the effects with padding.
     * This is not versatile because JVMs can freely rearrange the
     * field order, even of the same type.
     */

    @State(Scope.Group)
    public static class StatePadded {
        int readOnly;
        int p01, p02, p03, p04, p05, p06, p07, p08;
        int p11, p12, p13, p14, p15, p16, p17, p18;
        int writeOnly;
        int q01, q02, q03, q04, q05, q06, q07, q08;
        int q11, q12, q13, q14, q15, q16, q17, q18;
    }

    @Benchmark
    @Group("padded")
    public int reader(StatePadded s) {
        return s.readOnly;
    }

    @Benchmark
    @Group("padded")
    public void writer(StatePadded s) {
        s.writeOnly++;
    }

    /*
     * APPROACH 2: CLASS HIERARCHY TRICK
     *
     * We can alleviate false sharing with this convoluted hierarchy trick,
     * using the fact that superclass fields are usually laid out first.
     * In this construction, the protected field will be squashed between
     * paddings.
     * It is important to use the smallest data type, so that layouter would
     * not generate any gaps that can be taken by later protected subclasses
     * fields. Depending on the actual field layout of classes that bear the
     * protected fields, we might need more padding to account for "lost"
     * padding fields pulled into in their superclass gaps.
     */

    public static class StateHierarchy_1 {
        int readOnly;
    }

    public static class StateHierarchy_2 extends StateHierarchy_1 {
        byte p01, p02, p03, p04, p05, p06, p07, p08;
        byte p11, p12, p13, p14, p15, p16, p17, p18;
        byte p21, p22, p23, p24, p25, p26, p27, p28;
        byte p31, p32, p33, p34, p35, p36, p37, p38;
        byte p41, p42, p43, p44, p45, p46, p47, p48;
        byte p51, p52, p53, p54, p55, p56, p57, p58;
        byte p61, p62, p63, p64, p65, p66, p67, p68;
        byte p71, p72, p73, p74, p75, p76, p77, p78;
    }

    public static class StateHierarchy_3 extends StateHierarchy_2 {
        int writeOnly;
    }

    public static class StateHierarchy_4 extends StateHierarchy_3 {
        byte q01, q02, q03, q04, q05, q06, q07, q08;
        byte q11, q12, q13, q14, q15, q16, q17, q18;
        byte q21, q22, q23, q24, q25, q26, q27, q28;
        byte q31, q32, q33, q34, q35, q36, q37, q38;
        byte q41, q42, q43, q44, q45, q46, q47, q48;
        byte q51, q52, q53, q54, q55, q56, q57, q58;
        byte q61, q62, q63, q64, q65, q66, q67, q68;
        byte q71, q72, q73, q74, q75, q76, q77, q78;
    }

    @State(Scope.Group)
    public static class StateHierarchy extends StateHierarchy_4 {
    }

    @Benchmark
    @Group("hierarchy")
    public int reader(StateHierarchy s) {
        return s.readOnly;
    }

    @Benchmark
    @Group("hierarchy")
    public void writer(StateHierarchy s) {
        s.writeOnly++;
    }

    /*
     * APPROACH 3: ARRAY TRICK
     *
     * This trick relies on the contiguous allocation of an array.
     * Instead of placing the fields in the class, we mangle them
     * into the array at very sparse offsets.
     */

    @State(Scope.Group)
    public static class StateArray {
        int[] arr = new int[128];
    }

    @Benchmark
    @Group("sparse")
    public int reader(StateArray s) {
        return s.arr[0];
    }

    @Benchmark
    @Group("sparse")
    public void writer(StateArray s) {
        s.arr[64]++;
    }

    /*
     * APPROACH 4:
     *
     * @Contended (since JDK 8):
     *  Uncomment the annotation if building with JDK 8.
     *  Remember to flip -XX:-RestrictContended to enable.
     */

    @State(Scope.Group)
    public static class StateContended {
        int readOnly;

//        @sun.misc.Contended
        int writeOnly;
    }

    @Benchmark
    @Group("contended")
    public int reader(StateContended s) {
        return s.readOnly;
    }

    @Benchmark
    @Group("contended")
    public void writer(StateContended s) {
        s.writeOnly++;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_22_FalseSharing.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

}

```
:::
## Inheritance :
在某些特殊情况下，   我们可以使用模版模式通过抽象方法来分离实现。

  经验法则是：如果一些类有@Benchmark方法，那么它所有的子类都继承@Benchmark方法。 注意，因为我们只知道编译期间的类型层次结构，所以只能在同一个编译会话期间使用。也就是说，在JMH编译之后混合扩展benchmark类的子类将不起作用。

注释现在有两个可能的地方，这时采用就近原则，离得近的生效。验证~~

::: details 代码示例
```java
public class JMHSample_24_Inheritance {

    /*
     * In very special circumstances, you might want to provide the benchmark
     * body in the (abstract) superclass, and specialize it with the concrete
     * pieces in the subclasses.
     *
     * The rule of thumb is: if some class has @Benchmark method, then all the subclasses
     * are also having the "synthetic" @Benchmark method. The caveat is, because we only
     * know the type hierarchy during the compilation, it is only possible during
     * the same compilation session. That is, mixing in the subclass extending your
     * benchmark class *after* the JMH compilation would have no effect.
     *
     * Note how annotations now have two possible places. The closest annotation
     * in the hierarchy wins.
     */

    @BenchmarkMode(Mode.AverageTime)
    @Fork(1)
    @State(Scope.Thread)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public static abstract class AbstractBenchmark {
        int x;

        @Setup
        public void setup() {
            x = 42;
        }

        @Benchmark
        @Warmup(iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS)
        @Measurement(iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS)
        public double bench() {
            return doWork() * doWork();
        }

        protected abstract double doWork();
    }

    public static class BenchmarkLog extends AbstractBenchmark {
        @Override
        protected double doWork() {
            return Math.log(x);
        }
    }

    public static class BenchmarkSin extends AbstractBenchmark {
        @Override
        protected double doWork() {
            return Math.sin(x);
        }
    }

    public static class BenchmarkCos extends AbstractBenchmark {
        @Override
        protected double doWork() {
            return Math.cos(x);
        }
    }

    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     *
     * You can run this test, and observe the three distinct benchmarks running the squares
     * of Math.log, Math.sin, and Math.cos, accordingly.
     *
     * a) Via the command line:
     *    $ mvn clean install
     *    $ java -jar target/benchmarks.jar JMHSample_24
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_24_Inheritance.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }

}
```
:::

## API_GA

这个例子展示了在复杂场景中利用 JMH API 的一种相当复杂但有趣的方式。
到目前为止，我们还没有以编程方式使用结果，因此我们错过了所有乐趣。

让我们考虑一下这个简单的代码，它显然受到性能异常的影响，因为当前的 HotSpot 无法进行尾调用优化

我们可能可以通过更好的内联策略来弥补 TCO 的缺失。 但是手动调整策略需要了解很多关于 VM 内部的知识。 相反，让我们构建外行遗传算法，该算法通过内联设置进行筛选，试图找到更好的策略。 如果您不熟悉遗传算法的概念，请先阅读维基百科文章：http://en.wikipedia.org/wiki/Genetic_algorithm VM 专家可以猜测应该调整哪个选项以获得最大性能。 尝试运行示例，看看它是否提高了性能

::: details 代码示例
```java
@State(Scope.Thread)
public class JMHSample_25_API_GA {

    /**
     * This example shows the rather convoluted, but fun way to exploit
     * JMH API in complex scenarios. Up to this point, we haven't consumed
     * the results programmatically, and hence we are missing all the fun.
     *
     * Let's consider this naive code, which obviously suffers from the
     * performance anomalies, since current HotSpot is resistant to make
     * the tail-call optimizations.
     */

    private int v;

    @Benchmark
    public int test() {
        return veryImportantCode(1000, v);
    }

    public int veryImportantCode(int d, int v) {
        if (d == 0) {
            return v;
        } else {
            return veryImportantCode(d - 1, v);
        }
    }

    /*
     * We could probably make up for the absence of TCO with better inlining
     * policy. But hand-tuning the policy requires knowing a lot about VM
     * internals. Let's instead construct the layman's genetic algorithm
     * which sifts through inlining settings trying to find the better policy.
     *
     * If you are not familiar with the concept of Genetic Algorithms,
     * read the Wikipedia article first:
     *    http://en.wikipedia.org/wiki/Genetic_algorithm
     *
     * VM experts can guess which option should be tuned to get the max
     * performance. Try to run the sample and see if it improves performance.
     */

    public static void main(String[] args) throws RunnerException {
        // These are our base options. We will mix these options into the
        // measurement runs. That is, all measurement runs will inherit these,
        // see how it's done below.
        Options baseOpts = new OptionsBuilder()
                .include(JMHSample_25_API_GA.class.getName())
                .warmupTime(TimeValue.milliseconds(200))
                .measurementTime(TimeValue.milliseconds(200))
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(1)
                .verbosity(VerboseMode.SILENT)
                .build();

        // Initial population
        Population pop = new Population();
        final int POPULATION = 10;
        for (int c = 0; c < POPULATION; c++) {
            pop.addChromosome(new Chromosome(baseOpts));
        }

        // Make a few rounds of optimization:
        final int GENERATIONS = 100;
        for (int g = 0; g < GENERATIONS; g++) {
            System.out.println("Entering generation " + g);

            // Get the baseline score.
            // We opt to remeasure it in order to get reliable current estimate.
            RunResult runner = new Runner(baseOpts).runSingle();
            Result baseResult = runner.getPrimaryResult();

            // Printing a nice table...
            System.out.println("---------------------------------------");
            System.out.printf("Baseline score: %10.2f %s%n",
                    baseResult.getScore(),
                    baseResult.getScoreUnit()
            );

            for (Chromosome c : pop.getAll()) {
                System.out.printf("%10.2f %s (%+10.2f%%) %s%n",
                        c.getScore(),
                        baseResult.getScoreUnit(),
                        (c.getScore() / baseResult.getScore() - 1) * 100,
                        c.toString()
                );
            }
            System.out.println();

            Population newPop = new Population();

            // Copy out elite solutions
            final int ELITE = 2;
            for (Chromosome c : pop.getAll().subList(0, ELITE)) {
                newPop.addChromosome(c);
            }

            // Cross-breed the rest of new population
            while (newPop.size() < pop.size()) {
                Chromosome p1 = pop.selectToBreed();
                Chromosome p2 = pop.selectToBreed();

                newPop.addChromosome(p1.crossover(p2).mutate());
                newPop.addChromosome(p2.crossover(p1).mutate());
            }

            pop = newPop;
        }

    }

    /**
     * Population.
     */
    public static class Population {
        private final List<Chromosome> list = new ArrayList<>();

        public void addChromosome(Chromosome c) {
            list.add(c);
            Collections.sort(list);
        }

        /**
         * Select the breeding material.
         * Solutions with better score have better chance to be selected.
         * @return breed
         */
        public Chromosome selectToBreed() {
            double totalScore = 0D;
            for (Chromosome c : list) {
                totalScore += c.score();
            }

            double thresh = Math.random() * totalScore;
            for (Chromosome c : list) {
                if (thresh >= 0) {
                    thresh -= c.score();
                } else {
                    return c;
                }
            }

            // Return the last
            return list.get(list.size() - 1);
        }

        public int size() {
            return list.size();
        }

        public List<Chromosome> getAll() {
            return list;
        }
    }

    /**
     * Chromosome: encodes solution.
     */
    public static class Chromosome implements Comparable<Chromosome> {

        // Current score is not yet computed.
        double score = Double.NEGATIVE_INFINITY;

        // Base options to mix in
        final Options baseOpts;

        // These are current HotSpot defaults.
        int freqInlineSize = 325;
        int inlineSmallCode = 1000;
        int maxInlineLevel = 9;
        int maxInlineSize = 35;
        int maxRecursiveInlineLevel = 1;
        int minInliningThreshold = 250;

        public Chromosome(Options baseOpts) {
            this.baseOpts = baseOpts;
        }

        public double score() {
            if (score != Double.NEGATIVE_INFINITY) {
                // Already got the score, shortcutting
                return score;
            }

            try {
                // Add the options encoded by this solution:
                //  a) Mix in base options.
                //  b) Add JVM arguments: we opt to parse the
                //     stringly representation to make the example
                //     shorter. There are, of course, cleaner ways
                //     to do this.
                Options theseOpts = new OptionsBuilder()
                        .parent(baseOpts)
                        .jvmArgs(toString().split("[ ]"))
                        .build();

                // Run through JMH and get the result back.
                RunResult runResult = new Runner(theseOpts).runSingle();
                score = runResult.getPrimaryResult().getScore();
            } catch (RunnerException e) {
                // Something went wrong, the solution is defective
                score = Double.MIN_VALUE;
            }

            return score;
        }

        @Override
        public int compareTo(Chromosome o) {
            // Order by score, descending.
            return -Double.compare(score(), o.score());
        }

        @Override
        public String toString() {
            return "-XX:FreqInlineSize=" + freqInlineSize +
                    " -XX:InlineSmallCode=" + inlineSmallCode +
                    " -XX:MaxInlineLevel=" + maxInlineLevel +
                    " -XX:MaxInlineSize=" + maxInlineSize +
                    " -XX:MaxRecursiveInlineLevel=" + maxRecursiveInlineLevel +
                    " -XX:MinInliningThreshold=" + minInliningThreshold;
        }

        public Chromosome crossover(Chromosome other) {
            // Perform crossover:
            // While this is a very naive way to perform crossover, it still works.

            final double CROSSOVER_PROB = 0.1;

            Chromosome result = new Chromosome(baseOpts);

            result.freqInlineSize = (Math.random() < CROSSOVER_PROB) ?
                    this.freqInlineSize : other.freqInlineSize;

            result.inlineSmallCode = (Math.random() < CROSSOVER_PROB) ?
                    this.inlineSmallCode : other.inlineSmallCode;

            result.maxInlineLevel = (Math.random() < CROSSOVER_PROB) ?
                    this.maxInlineLevel : other.maxInlineLevel;

            result.maxInlineSize = (Math.random() < CROSSOVER_PROB) ?
                    this.maxInlineSize : other.maxInlineSize;

            result.maxRecursiveInlineLevel = (Math.random() < CROSSOVER_PROB) ?
                    this.maxRecursiveInlineLevel : other.maxRecursiveInlineLevel;

            result.minInliningThreshold = (Math.random() < CROSSOVER_PROB) ?
                    this.minInliningThreshold : other.minInliningThreshold;

            return result;
        }

        public Chromosome mutate() {
            // Perform mutation:
            //  Again, this is a naive way to do mutation, but it still works.

            Chromosome result = new Chromosome(baseOpts);

            result.freqInlineSize = (int) randomChange(freqInlineSize);
            result.inlineSmallCode = (int) randomChange(inlineSmallCode);
            result.maxInlineLevel = (int) randomChange(maxInlineLevel);
            result.maxInlineSize = (int) randomChange(maxInlineSize);
            result.maxRecursiveInlineLevel = (int) randomChange(maxRecursiveInlineLevel);
            result.minInliningThreshold = (int) randomChange(minInliningThreshold);

            return result;
        }

        private double randomChange(double v) {
            final double MUTATE_PROB = 0.5;
            if (Math.random() < MUTATE_PROB) {
                if (Math.random() < 0.5) {
                    return v / (Math.random() * 2);
                } else {
                    return v * (Math.random() * 2);
                }
            } else {
                return v;
            }
        }

        public double getScore() {
            return score;
        }
    }

}
```
:::

## BatchSize
有时可能需要测试没有稳定状态的操作。基准操作的成本在不同调用时差异会很大。

在这种情况下基于时间来测试非常不靠谱。唯一可以接受的基准测试模式就是 single shot。另一方面，对于可靠的single shot测试，这种操作可能太小。？？？

我们可以使用“batch size”参数来描述每次调用执行的基准调用次数，而无需手动循环方法并防止JMHSample_11_Loops中描述的问题。

假设我们想测量列表中间的插入操作。

::: details 代码示例
```java
public class JMHSample_26_BatchSize {

    List<String> list = new LinkedList<>();

    @Benchmark
    @Warmup(iterations = 5, time = 1)
    @Measurement(iterations = 5, time = 1)
    @BenchmarkMode(Mode.AverageTime)
    public List<String> measureWrong_1() {
        list.add(list.size() / 2, "something");
        return list;
    }

    @Benchmark
    @Warmup(iterations = 5, time = 5)
    @Measurement(iterations = 5, time = 5)
    @BenchmarkMode(Mode.AverageTime)
    public List<String> measureWrong_5() {
        list.add(list.size() / 2, "something");
        return list;
    }

    /*
     * This is what you do with JMH.
     */

    @Benchmark
    @Warmup(iterations = 5, batchSize = 5000)
    @Measurement(iterations = 5, batchSize = 5000)
    @BenchmarkMode(Mode.SingleShotTime)
    public List<String> measureRight() {
        list.add(list.size() / 2, "something");
        return list;
    }

    @Setup(Level.Iteration)
    public void setup(){
        list.clear();
    }

    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     *
     * You can see completely different results for measureWrong_1 and measureWrong_5; this
     * is because the workload has no steady state. The result of the workload is dependent
     * on the measurement time. measureRight does not have this drawback, because it measures
     * the N invocations of the test method and measures it's time.
     *
     * We measure batch of 5000 invocations and consider the batch as the single operation.
     *
     * 您可以看到measureWrong_1和measureWrong_5的完全不同的结果;这是因为工作负载没有稳定状态。工作量的结果取决于测量时间。
     * measureRight没有这个缺点，因为它测量测试方法的N次调用并测量它的时间。
     *
     * 我们测量5000次调用批次并将批次视为单个操作。
     *
     * You can run this test:
     *
     * a) Via the command line:
     *    $ mvn clean install
     *    $ java -jar target/benchmarks.jar JMHSample_26 -f 1
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_26_BatchSize.class.getSimpleName())
                .forks(1)
                .output("JMHSample_26_BatchSize.sampleLog")
                .build();

        new Runner(opt).run();
    }

}
```
:::

## Params 
在很多场景下，一个基准测试需要再不同的配置下运行。这些需要额外的控制，或者需要验证在不同参数下程序的性能变化。

不同参数是否取笛卡尔积？？？

::: details 代码示例
```java
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
public class JMHSample_27_Params {

    /**
     * In many cases, the experiments require walking the configuration space
     * for a benchmark. This is needed for additional control, or investigating
     * how the workload performance changes with different settings.
     */

    @Param({"1", "31", "65", "101", "103"})
    public int arg;

    @Param({"0", "1", "2", "4", "8", "16", "32"})
    public int certainty;

    @Benchmark
    public boolean bench() {
        return BigInteger.valueOf(arg).isProbablePrime(certainty);
    }

    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     *
     * Note the performance is different with different parameters.
     *
     * You can run this test:
     *
     * a) Via the command line:
     *    $ mvn clean install
     *    $ java -jar target/benchmarks.jar JMHSample_27
     *
     *    You can juggle parameters through the command line, e.g. with "-p arg=41,42"
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_27_Params.class.getSimpleName())
//                .param("arg", "41", "42") // Use this to selectively constrain/override parameters
                .build();

        new Runner(opt).run();
    }

}
```
:::

## 

::: details 代码示例
```java
/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.openjdk.jmh.samples;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Thread)
public class JMHSample_28_BlackholeHelpers {

    /**
     * Sometimes you need the black hole not in @Benchmark method, but in
     * helper methods, because you want to pass it through to the concrete
     * implementation which is instantiated in helper methods. In this case,
     * you can request the black hole straight in the helper method signature.
     * This applies to both @Setup and @TearDown methods, and also to other
     * JMH infrastructure objects, like Control.
     *
     * Below is the variant of {@link org.openjdk.jmh.samples.JMHSample_08_DeadCode}
     * test, but wrapped in the anonymous classes.
     */

    public interface Worker {
        void work();
    }

    private Worker workerBaseline;
    private Worker workerRight;
    private Worker workerWrong;

    @Setup
    public void setup(final Blackhole bh) {
        workerBaseline = new Worker() {
            double x;

            @Override
            public void work() {
                // do nothing
            }
        };

        workerWrong = new Worker() {
            double x;

            @Override
            public void work() {
                Math.log(x);
            }
        };

        workerRight = new Worker() {
            double x;

            @Override
            public void work() {
                bh.consume(Math.log(x));
            }
        };

    }

    @Benchmark
    public void baseline() {
        workerBaseline.work();
    }

    @Benchmark
    public void measureWrong() {
        workerWrong.work();
    }

    @Benchmark
    public void measureRight() {
        workerRight.work();
    }

    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     *
     * You will see measureWrong() running on-par with baseline().
     * Both measureRight() are measuring twice the baseline, so the logs are intact.
     *
     * You can run this test:
     *
     * a) Via the command line:
     *    $ mvn clean install
     *    $ java -jar target/benchmarks.jar JMHSample_28
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_28_BlackholeHelpers.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }

}
```
:::

## BlackholeHelper
有时你不需要Blackhole在@Benchmark方法中，而是在helper(辅助？？)方法中，因为你想把它传递给在helper方法中的具体实现的实例. 在这种情况下，你可以通过helper方法签名获取Blackhole。这可以应用在被标注为@Setup和@TearDown的方法上，也包括其他JMH脚手架对象，比如Control。

{@link com.cxd.benchmark.JMHSample_08_DeadCode}是它的变种，但是他被包装在匿名类中。

::: details 代码示例
```java
/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.openjdk.jmh.samples;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Thread)
public class JMHSample_28_BlackholeHelpers {

    /**
     * Sometimes you need the black hole not in @Benchmark method, but in
     * helper methods, because you want to pass it through to the concrete
     * implementation which is instantiated in helper methods. In this case,
     * you can request the black hole straight in the helper method signature.
     * This applies to both @Setup and @TearDown methods, and also to other
     * JMH infrastructure objects, like Control.
     *
     * Below is the variant of {@link org.openjdk.jmh.samples.JMHSample_08_DeadCode}
     * test, but wrapped in the anonymous classes.
     */

    public interface Worker {
        void work();
    }

    private Worker workerBaseline;
    private Worker workerRight;
    private Worker workerWrong;

    @Setup
    public void setup(final Blackhole bh) {
        workerBaseline = new Worker() {
            double x;

            @Override
            public void work() {
                // do nothing
            }
        };

        workerWrong = new Worker() {
            double x;

            @Override
            public void work() {
                Math.log(x);
            }
        };

        workerRight = new Worker() {
            double x;

            @Override
            public void work() {
                bh.consume(Math.log(x));
            }
        };

    }

    @Benchmark
    public void baseline() {
        workerBaseline.work();
    }

    @Benchmark
    public void measureWrong() {
        workerWrong.work();
    }

    @Benchmark
    public void measureRight() {
        workerRight.work();
    }

    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     *
     * You will see measureWrong() running on-par with baseline().
     * Both measureRight() are measuring twice the baseline, so the logs are intact.
     *
     * You can run this test:
     *
     * a) Via the command line:
     *    $ mvn clean install
     *    $ java -jar target/benchmarks.jar JMHSample_28
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_28_BlackholeHelpers.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }

}


```
:::


## StatesDAG

>  THIS IS AN EXPERIMENTAL FEATURE, BE READY FOR IT BECOME REMOVED WITHOUT NOTICE!

当基准状态由一组@States 更清晰地描述时，存在一些奇怪的情况，并且这些@States 相互引用。 JMH 允许通过在辅助方法签名中引用 @States 来链接有向无环图 (DAG) 中的 @States。 （请注意， org.openjdk.jmh.samples.JMHSample_28_BlackholeHelpers只是其中的一个特例。遵循@Benchmark 调用的接口，所有引用@State-s 的@Setups 在当前@State 可以访问之前都会被触发。类似地，在完成当前@State 之前，不会为引用的@State 触发@TearDown 方法。
::: details 代码示例
```java
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Thread)
public class JMHSample_29_StatesDAG {

    /**
     * WARNING:
     * THIS IS AN EXPERIMENTAL FEATURE, BE READY FOR IT BECOME REMOVED WITHOUT NOTICE!
     */

    /**
     * There are weird cases when the benchmark state is more cleanly described
     * by the set of @States, and those @States reference each other. JMH allows
     * linking @States in directed acyclic graphs (DAGs) by referencing @States
     * in helper method signatures. (Note that {@link org.openjdk.jmh.samples.JMHSample_28_BlackholeHelpers}
     * is just a special case of that.
     *
     * Following the interface for @Benchmark calls, all @Setups for
     * referenced @State-s are fired before it becomes accessible to current @State.
     * Similarly, no @TearDown methods are fired for referenced @State before
     * current @State is done with it.
     */

    /*
     * This is a model case, and it might not be a good benchmark.
     * // TODO: Replace it with the benchmark which does something useful.
     */

    public static class Counter {
        int x;

        public int inc() {
            return x++;
        }

        public void dispose() {
            // pretend this is something really useful
        }
    }

    /*
     * Shared state maintains the set of Counters, and worker threads should
     * poll their own instances of Counter to work with. However, it should only
     * be done once, and therefore, Local state caches it after requesting the
     * counter from Shared state.
     * 共享状态维护计数器集，工作线程应该轮询自己的计数器实例来使用。 但是，它应该只执行一次，因此，本地状态在从共享状态请求计数器后缓存它。
     */

    @State(Scope.Benchmark)
    public static class Shared {
        List<Counter> all;
        Queue<Counter> available;

        @Setup
        public synchronized void setup() {
            all = new ArrayList<>();
            for (int c = 0; c < 10; c++) {
                all.add(new Counter());
            }

            available = new LinkedList<>();
            available.addAll(all);
        }

        @TearDown
        public synchronized void tearDown() {
            for (Counter c : all) {
                c.dispose();
            }
        }

        public synchronized Counter getMine() {
            return available.poll();
        }
    }

    @State(Scope.Thread)
    public static class Local {
        Counter cnt;

        @Setup
        public void setup(Shared shared) {
            cnt = shared.getMine();
        }
    }

    @Benchmark
    public int test(Local local) {
        return local.cnt.inc();
    }

    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     *
     * You can run this test:
     *
     * a) Via the command line:
     *    $ mvn clean install
     *    $ java -jar target/benchmarks.jar JMHSample_29
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_29_StatesDAG.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }


}
```
:::

## Interrupts
JMH还可以检测线程何时卡在基准测试中，并尝试强制中断基准线程。 在可以确定它不会影响测量时，JMH会尝试这样做。

在这个例子中，我们想测量ArrayBlockingQueue的简单性能特征。 不幸的是，在没有工具支持的情况下执行此操作会使其中一个线程死锁，因为take / put的执行不能完美配对。

 幸运的是，这两种方法都能很好地应对中断，因此我们可以依赖JMH来中断测量。 JMH将通知用户有关中断操作的信息，因此用户可以查看这些中断是否会影响测量。
 在达到默认或用户指定的超时后，JMH将开始发出中断。这是 {@link com.cxd.benchmark.JMHSample_18_Control}的一个变种，但是没有明确的控制对象。
这个例子很适合那些需要优雅应对中断的方法。


::: details 代码示例
```java
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Group)
@Timeout(time = 10)
public class JMHSample_30_Interrupts {

    /*
     * JMH can also detect when threads are stuck in the benchmarks, and try
     * to forcefully interrupt the benchmark thread. JMH tries to do that
     * when it is arguably sure it would not affect the measurement.
     *
     * JMH还可以检测线程何时卡在基准测试中，并尝试强制中断基准线程。
     * 在可以确定它不会影响测量时，JMH会尝试这样做。
     */

    /*
     * In this example, we want to measure the simple performance characteristics
     * of the ArrayBlockingQueue. Unfortunately, doing that without a harness
     * support will deadlock one of the threads, because the executions of
     * take/put are not paired perfectly. Fortunately for us, both methods react
     * to interrupts well, and therefore we can rely on JMH to terminate the
     * measurement for us. JMH will notify users about the interrupt actions
     * nevertheless, so users can see if those interrupts affected the measurement.
     * JMH will start issuing interrupts after the default or user-specified timeout
     * had been reached.
     *
     * This is a variant of org.openjdk.jmh.samples.JMHSample_18_Control, but without
     * the explicit control objects. This example is suitable for the methods which
     * react to interrupts gracefully.
     */

    private BlockingQueue<Integer> q;

    @Setup
    public void setup() {
        q = new ArrayBlockingQueue<>(1);
    }

    @Group("Q")
    @Benchmark
    public Integer take() throws InterruptedException {
        return q.take();
    }

    @Group("Q")
    @Benchmark
    public void put() throws InterruptedException {
        q.put(42);
    }

    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     *
     * You can run this test:
     *
     * a) Via the command line:
     *    $ mvn clean install
     *    $ java -jar target/benchmarks.jar JMHSample_30 -t 2 -f 5 -to 10
     *    (we requested 2 threads, 5 forks, and 10 sec timeout)
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_30_Interrupts.class.getSimpleName())
                .threads(2)
                .forks(5)
//                .timeout(TimeValue.seconds(10))
                .output("JMHSample_30_Interrupts_annotation.sampleLog")
                .build();

        new Runner(opt).run();
    }

}
```
:::
## InfraParams
 通过JMH提供的脚手架获取JMH的一些运行信息

有一种方式用来查JMH并发运行的模型。通过请求注入以下三个脚手架对象我们就可以做到：
     - BenchmarkParams: 涵盖了benchmark的全局配置
     - IterationParams: 涵盖了当前迭代的配置
     - ThreadParams: 涵盖了指定线程的配置
假设我们想检查ConcurrentHashMap如何在不同的并行级别下差异。我们可以可以把并发级别通过@Param传入， 但有时不方便，比如，我们想让他和@Threads一致。以下是我们如何查询JMH关于当前运行请求的线程数， 并将其放入ConcurrentHashMap构造函数的concurrencyLevel参数中。

::: details 代码示例
```java
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class JMHSample_31_InfraParams {

    /**
     * There is a way to query JMH about the current running mode. This is
     * possible with three infrastructure objects we can request to be injected:
     *   - BenchmarkParams: covers the benchmark-global configuration
     *   - IterationParams: covers the current iteration configuration
     *   - ThreadParams: covers the specifics about threading
     *
     * Suppose we want to check how the ConcurrentHashMap scales under different
     * parallelism levels. We can put concurrencyLevel in @Param, but it sometimes
     * inconvenient if, say, we want it to follow the @Threads count. Here is
     * how we can query JMH about how many threads was requested for the current run,
     * and put that into concurrencyLevel argument for CHM constructor.
     *
     * 有一种方式用来查JMH并发运行的模型。通过请求注入以下三个脚手架对象我们就可以做到：
     *   - BenchmarkParams: 涵盖了benchmark的全局配置
     *   - IterationParams: 涵盖了当前迭代的配置
     *   - ThreadParams: 涵盖了指定线程的配置
     *
     * 假设我们想检查ConcurrentHashMap如何在不同的并行级别下扩展。我们可以可以把concurrencyLevel通过@Param传入，
     * 但有时不方便，比如，我们想让他和@Threads一致。以下是我们如何查询JMH关于当前运行请求的线程数，
     * 并将其放入ConcurrentHashMap构造函数的concurrencyLevel参数中。
     */

    static final int THREAD_SLICE = 1000;

    private ConcurrentHashMap<String, String> mapSingle;
    private ConcurrentHashMap<String, String> mapFollowThreads;

    @Setup
    public void setup(BenchmarkParams params) {
        int capacity = 16 * THREAD_SLICE * params.getThreads();
        // 并发级别数量似乎只会影响initcapacity（仅在initcapacity小于并发数量时）。这么测试好像没什么意义。
        mapSingle        = new ConcurrentHashMap<>(capacity, 0.75f, 1);
        mapFollowThreads = new ConcurrentHashMap<>(capacity, 0.75f, params.getThreads());
    }

    /*
     * Here is another neat trick. Generate the distinct set of keys for all threads:
     *
     * 这是另一个巧妙的伎俩。为所有线程生成不同的密钥集：
     */

    @State(Scope.Thread)
    public static class Ids {
        private List<String> ids;

        @Setup
        public void setup(ThreadParams threads) {
            ids = new ArrayList<>();
            for (int c = 0; c < THREAD_SLICE; c++) {
                ids.add("ID" + (THREAD_SLICE * threads.getThreadIndex() + c));
            }
        }
    }

    @Benchmark
    public void measureDefault(Ids ids) {
        for (String s : ids.ids) {
            mapSingle.remove(s);
            mapSingle.put(s, s);
        }
    }

    @Benchmark
    public void measureFollowThreads(Ids ids) {
        for (String s : ids.ids) {
            mapFollowThreads.remove(s);
            mapFollowThreads.put(s, s);
        }
    }

    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     *
     * You can run this test:
     *
     * a) Via the command line:
     *    $ mvn clean install
     *    $ java -jar target/benchmarks.jar JMHSample_31 -t 4 -f 5
     *    (we requested 4 threads, and 5 forks; there are also other options, see -h)
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_31_InfraParams.class.getSimpleName())
                .threads(4)
                .forks(5)
                .output("JMHSample_31_InfraParams.sampleLog")
                .build();

        new Runner(opt).run();
    }

}
```
:::
## BulkWarmup
这是JMHSample_12_Forking测试的补充。

预热方式不同测量的结果大不一样。

有时你想要一个相反的配置：您可以将它们混合在一起以测试最坏情况，而不是分离不同基准的配置文件。

 JMH有一个批量预热特性：它首先预热所有测试，然后测量他们。JMH仍然为每个测试fork出一个JVM，但当新的JVM启动，所有的预热在测量开始前都会执行。
 这有助于避免类型配置文件偏差，因为每个测试仍然在不同的JVM中执行，我们只“混合”我们想要的预热代码。
::: details 代码示例
```java
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JMHSample_32_BulkWarmup {

    /*
     * This is an addendum to JMHSample_12_Forking test.
     *
     * Sometimes you want an opposite configuration: instead of separating the profiles
     * for different benchmarks, you want to mix them together to test the worst-case
     * scenario.
     *
     * JMH has a bulk warmup feature for that: it does the warmups for all the tests
     * first, and then measures them. JMH still forks the JVM for each test, but once the
     * new JVM has started, all the warmups are being run there, before running the
     * measurement. This helps to dodge the type profile skews, as each test is still
     * executed in a different JVM, and we only "mix" the warmup code we want.
     *
     * 这是JMHSample_12_Forking测试的附录。
     *
     * 有时你想要一个相反的配置：您可以将它们混合在一起以测试最坏情况，而不是分离不同基准的配置文件。
     *
     * JMH有一个批量预热特性：它首先预热所有测试，然后测量他们。JMH仍然为每个测试fork出一个JVM，但当新的JVM启动，
     * 所有的预热在测量开始前都会执行。
     * 这有助于避免类型配置文件偏差？？？，因为每个测试仍然在不同的JVM中执行，我们只“混合”我们想要的预热代码。
     */

    /*
     * These test classes are borrowed verbatim from JMHSample_12_Forking.
     */

    public interface Counter {
        int inc();
    }

    public class Counter1 implements Counter {
        private int x;

        @Override
        public int inc() {
            return x++;
        }
    }

    public class Counter2 implements Counter {
        private int x;

        @Override
        public int inc() {
            return x++;
        }
    }

    Counter c1 = new Counter1();
    Counter c2 = new Counter2();

    /*
     * And this is our test payload. Notice we have to break the inlining of the payload,
     * so that in could not be inlined in either measure_c1() or measure_c2() below, and
     * specialized for that only call.
     */

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public int measure(Counter c) {
        int s = 0;
        for (int i = 0; i < 10; i++) {
            s += c.inc();
        }
        return s;
    }

    @Benchmark
    public int measure_c1() {
        return measure(c1);
    }

    @Benchmark
    public int measure_c2() {
        return measure(c2);
    }

    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     *
     * Note how JMH runs the warmups first, and only then a given test. Note how JMH re-warmups
     * the JVM for each test. The scores for C1 and C2 cases are equally bad, compare them to
     * the scores from JMHSample_12_Forking.
     *
     * You can run this test:
     *
     * a) Via the command line:
     *    $ mvn clean install
     *    $ java -jar target/benchmarks.jar JMHSample_32 -f 1 -wm BULK
     *    (we requested a single fork, and bulk warmup mode; there are also other options, see -h)
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_32_BulkWarmup.class.getSimpleName())
                // .includeWarmup(...) <-- this may include other benchmarks into warmup
//                .warmupMode(WarmupMode.BULK) // see other WarmupMode.* as well
                .warmupMode(WarmupMode.INDI) // see other WarmupMode.* as well
                .forks(1)
                .output("JMHSample_32_BulkWarmup_indi.sampleLog")
                .build();

        new Runner(opt).run();
    }

}
```
:::

## SecurityManager

一些有针对性的测试可能会关心是否安装了 SecurityManager。由于 JMH 本身需要执行特权操作，因此盲目安装 SecurityManager 是不够的，因为 JMH 基础架构会失败。

在这个例子中，我们想要测量 System.getProperty 在安装 SecurityManager 的情况下的性能。为此，我们有两个带有辅助方法的状态类。一种读取默认的 JMH 安全策略（我们随 JMH 提供一种），并安装安全管理器；另一个确保未安装 SecurityManager。

如果您需要测试的受限安全策略，建议您获取 /jmh-security-minimal.policy，其中包含运行 JMH 基准测试所需的最低权限，在那里合并新权限，在临时生成新策略文件位置，并改为加载该策略文件。还有 /jmh-security-minimal-runner.policy，它包含运行 JMH 工具的最小权限，如果你想使用 JVM 参数来装备 SecurityManager。

::: details 代码示例
```java
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JMHSample_33_SecurityManager {

    /*
     * Some targeted tests may care about SecurityManager being installed.
     * Since JMH itself needs to do privileged actions, it is not enough
     * to blindly install the SecurityManager, as JMH infrastructure will fail.
     */

    /*
     * In this example, we want to measure the performance of System.getProperty
     * with SecurityManager installed or not. To do this, we have two state classes
     * with helper methods. One that reads the default JMH security policy (we ship one
     * with JMH), and installs the security manager; another one that makes sure
     * the SecurityManager is not installed.
     *
     * If you need a restricted security policy for the tests, you are advised to
     * get /jmh-security-minimal.policy, that contains the minimal permissions
     * required for JMH benchmark to run, merge the new permissions there, produce new
     * policy file in a temporary location, and load that policy file instead.
     * There is also /jmh-security-minimal-runner.policy, that contains the minimal
     * permissions for the JMH harness to run, if you want to use JVM args to arm
     * the SecurityManager.
     */

    @State(Scope.Benchmark)
    public static class SecurityManagerInstalled {
        @Setup
        public void setup() throws IOException, NoSuchAlgorithmException, URISyntaxException {
            URI policyFile = JMHSample_33_SecurityManager.class.getResource("/jmh-security.policy").toURI();
            Policy.setPolicy(Policy.getInstance("JavaPolicy", new URIParameter(policyFile)));
            System.setSecurityManager(new SecurityManager());
        }

        @TearDown
        public void tearDown() {
            System.setSecurityManager(null);
        }
    }

    @State(Scope.Benchmark)
    public static class SecurityManagerEmpty {
        @Setup
        public void setup() throws IOException, NoSuchAlgorithmException, URISyntaxException {
            System.setSecurityManager(null);
        }
    }

    @Benchmark
    public String testWithSM(SecurityManagerInstalled s) throws InterruptedException {
        return System.getProperty("java.home");
    }

    @Benchmark
    public String testWithoutSM(SecurityManagerEmpty s) throws InterruptedException {
        return System.getProperty("java.home");
    }

    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     *
     * You can run this test:
     *
     * a) Via the command line:
     *    $ mvn clean install
     *    $ java -jar target/benchmarks.jar JMHSample_33 -f 1
     *    (we requested 5 warmup iterations, 5 forks; there are also other options, see -h))
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_33_SecurityManager.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

}
```
:::

## Profilers
这个例子是profiler（分析器）的概览。

JMH有一些很便利的profiler来帮助我们理解benchmark。虽然这些分析器不能替代成熟的外部分析器，但在许多情况下，这些分析器很容易快速发现基准测试行为。

当你正在对基准代码本身进行多次调整时，快速获得结果非常重要。

可以使用-lprof列出所有profiler。本例中只展示常用的一些。许多profiler有他们自己的选项，通过-prof `<profiler-name/>:help`获取。

因为不同的profiler做不同的事情，很难在一个基准测试中展示所有profiler的行为。因为有好几个例子。

## BranchPrediction： 分支预测

## What is ?
xxx运行优化

此示例用作针对常规数据集的警告。

由于简单的生成策略，或者只是因为对常规数据集感觉更好，因此很容易将常规数据集用于基准测试。

不幸的是，它经常事与愿违：已知常规数据集可以通过软件和硬件很好地进行优化。

这个例子来展示分支预测优化。

想象一下我们的基准测试根据数组内容选择分支，因为我们正在通过它进行流式传输。

::: details 代码示例
```java
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
@State(Scope.Benchmark)
public class JMHSample_36_BranchPrediction {

    /*
     * This sample serves as a warning against regular data sets.
     *
     * It is very tempting to present a regular data set to benchmark, either due to
     * naive generation strategy, or just from feeling better about regular data sets.
     * Unfortunately, it frequently backfires: the regular datasets are known to be
     * optimized well by software and hardware. This example exploits one of these
     * optimizations: branch prediction.
     *
     * Imagine our benchmark selects the branch based on the array contents, as
     * we are streaming through it:
     */

    private static final int COUNT = 1024 * 1024;

    private byte[] sorted;
    private byte[] unsorted;

    @Setup
    public void setup() {
        sorted = new byte[COUNT];
        unsorted = new byte[COUNT];
        Random random = new Random(1234);
        random.nextBytes(sorted);
        random.nextBytes(unsorted);
        Arrays.sort(sorted);
    }

    @Benchmark
    @OperationsPerInvocation(COUNT)
    public void sorted(Blackhole bh1, Blackhole bh2) {
        for (byte v : sorted) {
            if (v > 0) {
                bh1.consume(v);
            } else {
                bh2.consume(v);
            }
        }
    }

    @Benchmark
    @OperationsPerInvocation(COUNT)
    public void unsorted(Blackhole bh1, Blackhole bh2) {
        for (byte v : unsorted) {
            if (v > 0) {
                bh1.consume(v);
            } else {
                bh2.consume(v);
            }
        }
    }

    /*
        There is a substantial difference in performance for these benchmarks!
        It is explained by good branch prediction in "sorted" case, and branch mispredicts in "unsorted"
        case. -prof perfnorm conveniently highlights that, with larger "branch-misses", and larger "CPI"
        for "unsorted" case:

        这些基准测试的性能存在显着差异！
        它可以通过“已排序”情况下的良好分支预测和“未排序”情况下的分支预测错误来解释。 -prof perfnorm 可以明显看出，具有更多的“分支未命中”和更多的“CPI”对于“未分类”的情况：
        Benchmark                                                       Mode  Cnt   Score    Error  Units
        JMHSample_36_BranchPrediction.sorted                            avgt   25   2.160 ±  0.049  ns/op
        JMHSample_36_BranchPrediction.sorted:·CPI                       avgt    5   0.286 ±  0.025   #/op
        JMHSample_36_BranchPrediction.sorted:·branch-misses             avgt    5  ≈ 10⁻⁴            #/op
        JMHSample_36_BranchPrediction.sorted:·branches                  avgt    5   7.606 ±  1.742   #/op
        JMHSample_36_BranchPrediction.sorted:·cycles                    avgt    5   8.998 ±  1.081   #/op
        JMHSample_36_BranchPrediction.sorted:·instructions              avgt    5  31.442 ±  4.899   #/op
        JMHSample_36_BranchPrediction.unsorted                          avgt   25   5.943 ±  0.018  ns/op
        JMHSample_36_BranchPrediction.unsorted:·CPI                     avgt    5   0.775 ±  0.052   #/op
        JMHSample_36_BranchPrediction.unsorted:·branch-misses           avgt    5   0.529 ±  0.026   #/op  <--- OOPS
        JMHSample_36_BranchPrediction.unsorted:·branches                avgt    5   7.841 ±  0.046   #/op
        JMHSample_36_BranchPrediction.unsorted:·cycles                  avgt    5  24.793 ±  0.434   #/op
        JMHSample_36_BranchPrediction.unsorted:·instructions            avgt    5  31.994 ±  2.342   #/op
        It is an open question if you want to measure only one of these tests. In many cases, you have to measure
        both to get the proper best-case and worst-case estimate!
     */


    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     *
     * You can run this test:
     *
     * a) Via the command line:
     *    $ mvn clean install
     *    $ java -jar target/benchmarks.jar JMHSample_36
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + JMHSample_36_BranchPrediction.class.getSimpleName() + ".*")
                .build();
        new Runner(opt).run();
    }

}
```
:::
## CacheAccess

此示例用作来提醒缓存访问模式中的细微差异。 许多性能差异可以通过测试访问内存的方式来解释。 在下面的示例中，我们以行优先或列优先遍历矩阵：

::: details 代码示例
```java
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
@State(Scope.Benchmark)
public class JMHSample_37_CacheAccess {

    /*
     * This sample serves as a warning against subtle differences in cache access patterns.
     *
     * Many performance differences may be explained by the way tests are accessing memory.
     * In the example below, we walk the matrix either row-first, or col-first:
     */

    private final static int COUNT = 4096;
    private final static int MATRIX_SIZE = COUNT * COUNT;

    private int[][] matrix;

    @Setup
    public void setup() {
        matrix = new int[COUNT][COUNT];
        Random random = new Random(1234);
        for (int i = 0; i < COUNT; i++) {
            for (int j = 0; j < COUNT; j++) {
                matrix[i][j] = random.nextInt();
            }
        }
    }

    @Benchmark
    @OperationsPerInvocation(MATRIX_SIZE)
    public void colFirst(Blackhole bh) {
        for (int c = 0; c < COUNT; c++) {
            for (int r = 0; r < COUNT; r++) {
                bh.consume(matrix[r][c]);
            }
        }
    }

    @Benchmark
    @OperationsPerInvocation(MATRIX_SIZE)
    public void rowFirst(Blackhole bh) {
        for (int r = 0; r < COUNT; r++) {
            for (int c = 0; c < COUNT; c++) {
                bh.consume(matrix[r][c]);
            }
        }
    }

    /*
        Notably, colFirst accesses are much slower, and that's not a surprise: Java's multidimensional
        arrays are actually rigged, being one-dimensional arrays of one-dimensional arrays. Therefore,
        pulling n-th element from each of the inner array induces more cache misses, when matrix is large.
        -prof perfnorm conveniently highlights that, with >2 cache misses per one benchmark op:
        Benchmark                                                 Mode  Cnt   Score    Error  Units
        JMHSample_37_MatrixCopy.colFirst                          avgt   25   5.306 ±  0.020  ns/op
        JMHSample_37_MatrixCopy.colFirst:·CPI                     avgt    5   0.621 ±  0.011   #/op
        JMHSample_37_MatrixCopy.colFirst:·L1-dcache-load-misses   avgt    5   2.177 ±  0.044   #/op <-- OOPS
        JMHSample_37_MatrixCopy.colFirst:·L1-dcache-loads         avgt    5  14.804 ±  0.261   #/op
        JMHSample_37_MatrixCopy.colFirst:·LLC-loads               avgt    5   2.165 ±  0.091   #/op
        JMHSample_37_MatrixCopy.colFirst:·cycles                  avgt    5  22.272 ±  0.372   #/op
        JMHSample_37_MatrixCopy.colFirst:·instructions            avgt    5  35.888 ±  1.215   #/op
        JMHSample_37_MatrixCopy.rowFirst                          avgt   25   2.662 ±  0.003  ns/op
        JMHSample_37_MatrixCopy.rowFirst:·CPI                     avgt    5   0.312 ±  0.003   #/op
        JMHSample_37_MatrixCopy.rowFirst:·L1-dcache-load-misses   avgt    5   0.066 ±  0.001   #/op
        JMHSample_37_MatrixCopy.rowFirst:·L1-dcache-loads         avgt    5  14.570 ±  0.400   #/op
        JMHSample_37_MatrixCopy.rowFirst:·LLC-loads               avgt    5   0.002 ±  0.001   #/op
        JMHSample_37_MatrixCopy.rowFirst:·cycles                  avgt    5  11.046 ±  0.343   #/op
        JMHSample_37_MatrixCopy.rowFirst:·instructions            avgt    5  35.416 ±  1.248   #/op
        So, when comparing two different benchmarks, you have to follow up if the difference is caused
        by the memory locality issues.
     */

    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     *
     * You can run this test:
     *
     * a) Via the command line:
     *    $ mvn clean install
     *    $ java -jar target/benchmarks.jar JMHSample_37
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + JMHSample_37_CacheAccess.class.getSimpleName() + ".*")
                .build();

        new Runner(opt).run();
    }

}
```
:::
## PerInvokeSetup
这个例子突出了非稳态基准测试中的常见错误。

假设我们要测试对数组进行冒泡排序的耗时。
我们天真地认为可以使用随机（未排序）值填充数组，并一遍又一遍地调用sort测试：

::: details 代码示例
```java
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
public class JMHSample_38_PerInvokeSetup {

    /*
     * This example highlights the usual mistake in non-steady-state benchmarks.
     *
     * Suppose we want to test how long it takes to bubble sort an array. Naively,
     * we could make the test that populates an array with random (unsorted) values,
     * and calls sort on it over and over again:
     *
     * 此示例突显非稳态基准测试(non-steady-state benchmarks)中的常见错误。
     *
     * 假设我们要测试对数组进行冒泡排序的耗时。
     * 天真地，我们可以使用随机（未排序）值填充数组，并一遍又一遍地调用sort测试：
     */

    private void bubbleSort(byte[] b) {
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int c = 0; c < b.length - 1; c++) {
                if (b[c] > b[c + 1]) {
                    byte t = b[c];
                    b[c] = b[c + 1];
                    b[c + 1] = t;
                    changed = true;
                }
            }
        }
    }

    // Could be an implicit State instead, but we are going to use it
    // as the dependency in one of the tests below
    // 可能是一个隐式状态，但我们将在下面的一个测试中依赖它

    @State(Scope.Benchmark)
    public static class Data {

        @Param({"1", "16", "256"})
        int count;

        byte[] arr;

        @Setup
        public void setup() {
            arr = new byte[count];
            Random random = new Random(1234);
            random.nextBytes(arr);
        }
    }

    @Benchmark
    public byte[] measureWrong(Data d) {
        bubbleSort(d.arr);
        return d.arr;
    }

    /*
     * The method above is subtly wrong: it sorts the random array on the first invocation
     * only. Every subsequent call will "sort" the already sorted array. With bubble sort,
     * that operation would be significantly faster!
     *
     * This is how we might *try* to measure it right by making a copy in Level.Invocation
     * setup. However, this is susceptible to the problems described in Level.Invocation
     * Javadocs, READ AND UNDERSTAND THOSE DOCS BEFORE USING THIS APPROACH.
     *
     * 上面的方法是巧妙的错误：它只在第一次调用时对随机数组进行排序。
     * 后续每个调用都将"排序"已排序的数组。
     * 通过冒泡排序，操作速度会明显加快！
     *
     * 我们可以尝试通过在Level.Invocation中制作数组的副本来正确测量它。
     * 在使用Level.Invocation这些方法之前请阅读并理解这些文档。
     */

    @State(Scope.Thread)
    public static class DataCopy {
        byte[] copy;

        @Setup(Level.Invocation)
        public void setup2(Data d) {
            copy = Arrays.copyOf(d.arr, d.arr.length);
        }
    }

    @Benchmark
    public byte[] measureNeutral(DataCopy d) {
        bubbleSort(d.copy);
        return d.copy;
    }

    /*
     * In an overwhelming majority of cases, the only sensible thing to do is to suck up
     * the per-invocation setup costs into a benchmark itself. This work well in practice,
     * especially when the payload costs dominate the setup costs.
     *
     * 在绝大多数情况下，唯一明智的做法是将每次调用设置成本吸收到基准测试本身。
     *
     * 这在实践中很有效，特别是当有效载荷成本主导设置成本时
     * （即：测试本身耗时远远大于准备数据时，准备数据时间对测试本身的影响可以忽略）。
     */

    @Benchmark
    public byte[] measureRight(Data d) {
        byte[] c = Arrays.copyOf(d.arr, d.arr.length);
        bubbleSort(c);
        return c;
    }

    /*
        Benchmark                                   (count)  Mode  Cnt      Score     Error  Units

        JMHSample_38_PerInvokeSetup.measureWrong          1  avgt   25      2.408 Â±   0.011  ns/op
        JMHSample_38_PerInvokeSetup.measureWrong         16  avgt   25      8.286 Â±   0.023  ns/op
        JMHSample_38_PerInvokeSetup.measureWrong        256  avgt   25     73.405 Â±   0.018  ns/op

        JMHSample_38_PerInvokeSetup.measureNeutral        1  avgt   25     15.835 Â±   0.470  ns/op
        JMHSample_38_PerInvokeSetup.measureNeutral       16  avgt   25    112.552 Â±   0.787  ns/op
        JMHSample_38_PerInvokeSetup.measureNeutral      256  avgt   25  58343.848 Â± 991.202  ns/op

        JMHSample_38_PerInvokeSetup.measureRight          1  avgt   25      6.075 Â±   0.018  ns/op
        JMHSample_38_PerInvokeSetup.measureRight         16  avgt   25    102.390 Â±   0.676  ns/op
        JMHSample_38_PerInvokeSetup.measureRight        256  avgt   25  58812.411 Â± 997.951  ns/op

        We can clearly see that "measureWrong" provides a very weird result: it "sorts" way too fast.
        "measureNeutral" is neither good or bad: while it prepares the data for each invocation correctly,
        the timing overheads are clearly visible. These overheads can be overwhelming, depending on
        the thread count and/or OS flavor.

        明显可以看出"measureWrong"跑出来一个奇怪的结果：它的排序太快了。
        "measureNeutral"一般般：它正确地为每个调用准备数据，时间开销清晰可见。在不同的线程数 和/或 OS风格下，这些开销可能会非常大。
     */

    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     *
     * You can run this test:
     *
     * a) Via the command line:
     *    $ mvn clean install
     *    $ java -jar target/benchmarks.jar JMHSample_38
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + JMHSample_38_PerInvokeSetup.class.getSimpleName() + ".*")
                .output("JMHSample_38_PerInvokeSetup.sampleLog")
                .build();

        new Runner(opt).run();
    }

}
```
:::
