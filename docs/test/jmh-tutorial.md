# JMH使用教程
## QuickStart
### 构建Benchmark应用
Step 1. 配置基准测试项目。使用以下命令，可以基于 maven 模板，在test文件夹下生成一个 JMH 驱动的项目。
```shell
$ mvn archetype:generate \
  -DinteractiveMode=false \
  -DarchetypeGroupId=org.openjdk.jmh \
  -DarchetypeArtifactId=jmh-java-benchmark-archetype \
  -DgroupId=org.sample \
  -DartifactId=test \
  -Dversion=1.0
```
如果您想要对其他的JVM语言进行基准测试，只需要将DarchetypeArtifactId参数值改成即可，具体可选项参考已有的模板列表中[https://repo.maven.apache.org/maven2/org/openjdk/jmh/]

Step 2: 构建基准测试。在项目生成后，可以通过一下命令进行构建项目。

```shell
cd test/
mvn clean verify
```

Step 3: 运行基准测试。当项目构建完成后，你会得到包含所有的JMH基础代码以及你的基准测试代码的可执行jar包。运行它即可。

```shell
java -jar target/benchmarks.jar
```
使用-h运行以查看可用的命令行选项。

在处理大型项目时，通常将基准测试保存在单独的子项目（模块）中，通过构建依赖关系来依赖测试模块。

虽推荐使用命令行的方式，但是很多人仍然你喜欢使用IDE。用户体验因不同的ide而异，但我们在这里将概一般情况。通常不建议在IDE中运行基准测试，因为基准测试运行的环境通常不受控制。

### Hello World: 让JMH程序跑起来

```java
public class JMHSample_01_HelloWorld {

    /* This is our first benchmark method.
     * JMH works as follows: users annotate the methods with @Benchmark, and then JMH produces the generated code to run this particular benchmark as reliably as possible. In general one might think about @Benchmark methods as the benchmark "payload", the things we want to measure. The surrounding infrastructure is provided by the harness itself.
     * Read the Javadoc for @Benchmark annotation for complete semantics and restrictions. At this point we only note that the methods names are non-essential, and it only matters that the methods are marked with @Benchmark. You can have multiple benchmark methods within the same class.
     * Note: if the benchmark method never finishes, then JMH run never finishes as well. If you throw an exception from the method body the JMH run ends abruptly for this benchmark and JMH will run the next benchmark down the list.
     * Although this benchmark measures "nothing" it is a good showcase for the overheads the infrastructure bear on the code you measure in the method. There are no magical infrastructures which incur no overhead, and it is important to know what are the infra overheads you are dealing with. You might find this thought unfolded in future examples by having the "baseline" measurements to compare against.
     */

    @Benchmark
    public void wellHelloThere() {
        // this method was intentionally left blank.
    }

    /* ============================== HOW TO RUN THIS TEST: ====================================
     * You are expected to see the run with large number of iterations, and very large throughput numbers. You can see that as the estimate of the harness overheads per method call. In most of our measurements, it is down to several cycles per call.
     * a) Via command-line:    $ mvn clean install    $ java -jar target/benchmarks.jar JMHSample_01
     * JMH generates self-contained JARs, bundling JMH together with it. The runtime options for the JMH are available with "-h":    $ java -jar target/benchmarks.jar -h
     * b) Via the Java API:    (see the JMH homepage for possible caveats when running from IDE:      http://openjdk.java.net/projects/code-tools/jmh/)
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_01_HelloWorld.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

}
```

## BenchmarkModes： 你想要哪些指标，测试吞吐量还是执行时间？

进行基准测试时我们可以根据测试目标，设定一些指标。用户可以通过注解选择默认的执行模式，也可以通过运行时选择其他的模式。

需要注意的是，有时我们的代码会因为性能抛出异常，我们需要声明将它们抛出去即可。如果代码抛出了实际的异常，此次测试将会因为报错而立马终止。

当你对代码执行行为或结果感到疑惑时，需要检查生成的代码是否和你的预期相符。准确的测试结果往往需要正确得试验配置，所以交叉检查生成的代码对试验的成功至关重要。生成的代码位置一般在/target/generated-sources/annotations/.../XXXX.java。

BenchmarkMode 和 OutputTimeUnit通常一起使用。用来设置我们的测试指标和时间单位。

OutputTimeUnit，执行结果测试报告中的时间单位，JMH支持的精读范围是 纳秒 到 天。 1000纳秒= 1 微秒，1000微秒=1毫秒 ，1秒=1000毫秒。

吞吐量（Throughput），表示在单位时间内的执行次数。通过在一段时间内（time）不断调用基准方法，统计该方法（ops）的执行次数进而计算吞吐量，即throughput = ops/time。

平均时间（AverageTime）， AverageTime= time/ops, 表示每次执行所需要的平均时间。和Throughput类似，此模式是基于时间的，通过在一段时间内（time）不断调用基准方法，统计该方法（ops）的执行次数，即AverageTime = time/ops。
```java
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void measureAvgTime() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(100);
    }
```

取样时间（SampleTime）, 采样统计方法执行时间。此模式也是基于时间的，通过在一段时间内不断调用基准方法，然后对方法的执行进行采样统计，以方便我们可以推算出**分布、百分位数**等。JMH会尝试自动调整采样频率，如果方法执行时间足够长，最终将会采集所有的样本。
```java
    @Benchmark
    @BenchmarkMode(Mode.SampleTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void measureSamples() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(100);
    }
```


单次调用时间（SingleShotTime）, 测试方法执行一次所需要的时间。此模式是基于调用次数的，测试时，只会调用一次 @Benchmark 方法，并记录执行时间。这种模式通常用来测试冷启动时的性能。
```java
   @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void measureSingleShot() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(100);
    }
```

当然我们也可以一次选择多个模式，只需要将参数换成数组即可；也可以通过Mode.All来选择全部模式。
```java
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
## 状态

大多数情况下，需要在基准运行时维护一些状态。因为JMH大多数情况下用于构建并发基准测试，所以JMH 选择了一个明确的 "state-bearing objects" 的概念。

以下是两个是两个状态对象， 类名可以任意发挥，我们需要关注的是在类上一定要加上@State。这些类的实例会在需要是初始化，并在整个基准测试过程中重复使用。

需要注意的是，这些状态对象只会在一个基准测试线程中被初始化，然后该线程有权限访问该状态对象。这意味着你可以像在工作线程中那样初始化字段？？？。

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

基准方法可以引用这些状态，JMH将在调用这些方法时注入适当的状态。可以完全没有状态，或者只有一个状态，或者引用多个状态。状态的引入使得我们进行多线程并发基准测试更加简单。

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

### Default State
幸运的是，大多数情况我们只需要使用一个状态对象，这是我们可以直接将benchmark的实例标记@State，在benchmark方法中直接引用即可。
```java
@State(Scope.Thread)
public class JMHSample_04_DefaultState {

    double x = Math.PI;

    @Benchmark
    public void measure() {
        x++;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_04_DefaultState.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

}
```

当然，你可以选择单独定义一个状态对象。
```java
@State(Scope.Thread)
public class JMHSample_04_DefaultState {

    @Benchmark
    public void measure(CountState state) {
        state.addOne();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_04_DefaultState.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
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

## State Fixtures：隔离资源初始化和销毁操作
由于状态对象会伴随着在整个基准测试生命周期，所以JMH为我们提供了一些非常有用的状态管理的方法。这些方法一般都是一些固定的方法，和JUnit和TestNG中的方法很像。

@Setup, 表示被该注解标记的方法会在基准测试方法执行之前执行，而被@Teardown标记的方法则会在方法执行之后执行。与Junit中的Before和Teardown的语义是类似的。**fixture方法的耗时不会被统计进性能指标，所以可以用它来做一些比较重的操作。**
 > Setup->Before
   Teardown-> After

这些状态管理的方法只能在对状态对象起作用， 否则编译时会报错！

和State对象一样，这些固定的方法只会被使用State对象的基准测试线程调用。也就是说，你可以在ThreadLocal上下文中操作，不用对这些方法加锁同步。

注意：这些固定方法也能够操作静态字段，尽管这些操作语意已经超过了状态对象的范畴，但是这仍然符合Java的语法规则。（比如，每个类中都定义一个静态字段）

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

### FixureLevel：状态生命周期方法被执行多少次？
上一节讲到了@Setup和@Teardown，被它们标记的方法分别会在基准测试方法执行前和执行后执行。

但是无论是基于时间还是次数的基准测试，往往都会执行很多次才能统计出一个较为准确的结果。
TODO：绘图

那么状态生命周期方法究竟是执行多少次呢，这是就有了FixureLevel。Fixure方法在运行时一共可以分为三个等级：
1. Level.Trial：整个基准测试（一个@Benchmark注解为一个基准测试）运行之前或之后（多个迭代）
2. Level.Iteration：在基准测试迭代之前或之后（每一轮迭代）
3. Level.Invocation：在每次基准测试方法调用之前或之后。（每一次调用调用）

```java
@State(Scope.Thread)
public class JMHSample_06_FixtureLevel {

    double x;

    /* Fixture methods have different levels to control when they should be run. There are at least three Levels available to the user. These are, from top to bottom:
     * Level.Trial: before or after the entire benchmark run (the sequence of iterations) Level.Iteration: before or after the benchmark iteration (the sequence of invocations) Level.Invocation; before or after the benchmark method invocation (WARNING: read the Javadoc before using)
     * Time spent in fixture methods does not count into the performance metrics, so you can use this to do some heavy-lifting.
     */

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

    /* ============================== HOW TO RUN THIS TEST: ====================================
     * You can see measureRight() yields the result, and measureWrong() fires the assert at the end of first iteration! This will not generate the results for measureWrong(). You can also prevent JMH for proceeding further by requiring "fail on error".
     * You can run this test:
     * a) Via the command line:    $ mvn clean install    $ java -ea -jar target/benchmarks.jar JMHSample_06 -f 1    (we requested single fork; there are also other options, see -h)
     *    You can optionally supply -foe to fail the complete run.
     * b) Via the Java API:    (see the JMH homepage for possible caveats when running from IDE:      http://openjdk.java.net/projects/code-tools/jmh/)
     */

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

## FixureLevelInvocation
在运行的时候fixture有不同的等级控制。Level.Invocation在一些场景下有用，因为这些fixure方法的执行不被统计为有效负载。
需要注意的是 对于 Leavel.Invocation 等级下的时间戳和方法同步调用  可能会明显抵消度量值，一定要谨慎使用。

这个等级只适用于每次benchmark方法执行时间超过1ms的场景， 在特别的基础上验证对您的案例的影响也是一个好主意???

1. 由于我们必须从基准测试时间中减去setup和teardown方法的耗时，在这个级别上，我们必须为每次基准测试方法调用设置时间戳。如果基准测试方法耗时很短，那么大量的时间戳请求会使系统饱和，这将引入虚假的延迟、吞吐量和可伸缩性瓶颈。
2. 由于我们使用这个级别度量单独的调用计时，因此我们可能会为（协调的）遗漏做好准备。这意味着测量中的小问题可以计时测试中隐藏起来，并可以带来令人惊讶的结果。例如，当我们使用计时来理解基准吞吐量时，省略的计时测量将导致更低的聚合时间和虚构的更大的吞吐量。

3. 为了维持与其他级别相同的共享行为，我们有时必须同步的方式对{@link State}对象的访问。其他级别在测量之外这样做，但是在这个级别，我们必须在*关键链路*上同步，进一步抵消测量。

4. 当前实现允许此级别上的辅助方法执行与基准调用本身重叠，以简化arbitrage。关键是在多线程基准测试中，当一个执行{@link Benchmark}方法的工作线程可能会观察到其他已经在为同一个对象调用{@link TearDown}的工作线程时。「并发竞争」
```java
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
     * 在他们运行的时候fixture有不同的等级进行控制。
     * Level.Invocation一些情况下很有用，比如：每次调用前做一些工作，并且这些工作不会被统计为有效载荷。
     * 请注意Level.Invocation助手的时间戳和同步可能会显着抵消测量，请谨慎使用。
     *
     * Consider this sample:
     * 看下面的例子：
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
            System.out.println("up");
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
     *
     * @State 是可继承的
     */

    public static class LaggingState extends NormalState {
        static final int SLEEP_TIME = Integer.getInteger("sleepTime", 10);

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

        private double doWork() {
            p = Math.log(p);
            return p;
        }
    }

    public static class Task implements Callable<Double> {
        private Scratch s;

        private Task(Scratch s) {
            this.s = s;
        }

        @Override
        public Double call() {
            return s.doWork();
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_07_FixtureLevelInvocation.class.getSimpleName())
                .forks(1)
                .output("JMHSample_07_FixtureLevelInvocation.sampleLog")
                .build();

        new Runner(opt).run();
    }

}
```

## DeadCode
许多基准测试的失败是因为Dead-Code的消除：编译机非常智能可以推断出一些冗余的计算并彻底消除。如果被消除的部分是我们的基准测试代码，我们的基准测试将是无效的。

### BlackHole
幸运的是，JMH提供了必要的基础设施来应对这一情况，返回计算结果将要求JMH处理结果以限制死代码消除。
```java
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JMHSample_08_DeadCode {

    private double x = Math.PI;

    @Benchmark
    public void baseline() {
        // do nothing, this is a baseline
        // 基准线
    }

    @Benchmark
    public void measureWrong() {
        // This is wrong: result is not used and the entire computation is optimized away.
        // 这是错误的：结果没有被使用，整个计算将会被编译器优化。
        Math.log(x);
    }

    @Benchmark
    public double measureRight() {
        // This is correct: the result is being used.
        return Math.log(x);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_08_DeadCode.class.getSimpleName())
                .output("JMHSample_08_DeadCode.sampleLog")
                .forks(1)
                .build();

        new Runner(opt).run();
    }

}
```

首选需要确认的是你的基准测试是否返回多个结果，你应该考虑一下两个问题（注意：如果你只会产生一个结果，应该使用更易读的明确return，就像JMHSample_08_DeadCode。不要使用明确的Blackholes来降低您的基准代码的可读性！）

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
     * 选择二: 显示使用Blackhole对象，并将结果值传入Blackhole.consumer方法中。
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
```
### ConstantFold 常量折叠
死代码消除的另一种形式是常量折叠。

如果JVM发现不管怎么计算，计算结果都是不变的，它可以巧妙地优化它。在我们的case中，这就意味着我们可以把计算移到JMH之外。

可以通过@State对象的非final类型的字段读取输入，根据这些值计算结果，准售这些规则就能防止DCE。

```java
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JMHSample_10_ConstantFold {

 
    private double x = Math.PI;

    private final double wrongX = Math.PI;

    @Benchmark
    public double baseline() {
        // simply return the value, this is a baseline
        return Math.PI;
    }

    @Benchmark
    public double measureWrong_1() {
        // This is wrong: the source is predictable, and computation is foldable.
        return Math.log(Math.PI);
    }

    @Benchmark
    public double measureWrong_2() {
        // This is wrong: the source is predictable, and computation is foldable.
        return Math.log(wrongX);
    }

    @Benchmark
    public double measureRight() {
        // This is correct: the source is not predictable.
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

## Loops循环
在基准测试方法中进行循环操作是一种不好的做法！

循环是为了最大限度地减少调用测试方法，通过在循环内部而不是内部进行操作方法调用？？？。不要相信这个论点，当我们允许优化器合并循环迭代时，你会看到很多奇怪的事情发生。

```java
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JMHSample_11_Loops {

    /*
     * It would be tempting for users to do loops within the benchmarked method.
     * (This is the bad thing Caliper taught everyone). These tests explain why
     * this is a bad idea.
     *
     * Looping is done in the hope of minimizing the overhead of calling the
     * test method, by doing the operations inside the loop instead of inside
     * the method call. Don't buy this argument; you will see there is more
     * magic happening when we allow optimizers to merge the loop iterations.
     *
     * 在基准测试方法中做循环是很诱人的。这是Caliper教给大家的坏事情。以下测试告诉你原因。
     *
     * 循环是为了通过在循环内而不是在方法调用内部进行操作来最小化调用测试方法的开销。？？？
     * 不要买这个论点;当我们允许优化器合并循环迭代时，你会发现有更多的魔法发生。
     */

    /*
     * Suppose we want to measure how much it takes to sum two integers:
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
     * 发生这种情况是因为循环被大量unrolled/pipelined，并且要从循环中提升要测量的操作。规则：不要过度使用循环，依靠JMH来正确测量。
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

## Forking
使用non-forked运行仅用于调试目的，而不是用于实际性能运行

众所周知，JVM 擅长配置 profile-guided 的优化。但是这对进准测试并不友好，因为不同的测试可以将它们的 profile-guided 混合在一起， 然后为每个测试呈现“一致坏”的代码。forking（运行在不同的进程中）每个测试都可以帮助规避这个问题。

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
     * JVM擅长profile-guided优化。但这对基准不友好，因为不同的测试可以把他们的profiles混合在一起，
     * 然后为每一个测试渲染"uniformly bad"代码？？？。forking（运行在不同的进程）每个测试可以避免这个问题。
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
### RunToRun 
Forking 还允许估计运行到运行的差异。JVM 是复杂的系统，它们固有的不确定性。
这要求我们始终将运行间差异视为我们实验中的影响之一。幸运的是，foking模式下聚合了多个JVM启动的结果。



为了方便地引入易于量化的运行间差异，we build the workload which performance differs from run to run. ？？？。请注意，许多工作负载将具有类似的行为，但我们人为地这样做是为了说明这一点。

```java
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class JMHSample_13_RunToRun {

    @State(Scope.Thread)
    public static class SleepyState {
        public long sleepTime;

        @Setup
        public void setup() {
            // 每次fork都会执行，fork相当于重复多次某个被标记@Benchmark注解的方法，但它会合并结果。
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
 
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_13_RunToRun.class.getSimpleName())
                .warmupIterations(0)
                .measurementIterations(3)
                .output("JMHSample_13_RunToRun.sampleLog")
                .build();

        new Runner(opt).run();
    }

}
```

## Asymmetric
目前为止，我们的测试都是对称的：所有的线程都运行相同的代码。是时候了解非对称测试了。JMH提供了@Group注解来把几个方法绑定到一起，所有线程都分布在测试方法中。

每个执行组包含一个或多个线程。特定执行组中的每个线程执行 @Group 注释的 @Benchmark 方法之一。多个执行组可以参与运行。运行中的总线程数四舍五入到执行组大小，这将只允许完整的执行组。

      以下示例含义：
       a)定义执行组"g"，它有3个线程来执行inc()，1个线程来执行get()，每个分组共有4个线程；
       b)如果我们用4个线程来运行这个测试用例，我们将会有一个单独的执行组。通常，用4*N个线程来创建N个执行组。
       c)每个执行组内共享一个@State实例：也就是执行组内共享counter，而不是跨组共享。

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
     * 每个执行组包含一个或者多个线程。特定执行组中的每个线程执行@Group-annotated @Benchmark方法之一。
     * 多个执行组可以参与运行。运行中的总线程数将四舍五入为执行组大小，这将只允许完整的执行组。？？？
     *
     * 注意那两个状态的作用域：Scope.Benchmark 和 Scope.Thread没有在这个用例中覆盖 -- 你要么在状态中共享每个东西，要么不共享。
     * 我们使用Scope.Group状态用来表明在执行组内共享，而不在组间共享。
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

## CompilerController

Method Inlining: 方法内联，是JVM对代码的编译优化，常见的编译优化 @see http://www.importnew.com/2009.html

Java编程语言中虚拟方法调用的频率是一个重要的优化瓶颈。一旦Java HotSpot自适应优化器在执行期间收集有关程序热点的信息，它不仅将热点编译为本机代码，而且还对该代码执行大量方法内联。

内联有很多好处。 它大大降低了方法调用的动态频率，从而节省了执行这些方法调用所需的时间。但更重要的是，内联会产生更大的代码块供优化器处理，这就大大提高了传统编译器优化的效率，克服了提高Java编程语言性能的主要障碍。

内联与其他代码优化是协同的，因为它使它们更有效。随着Java HotSpot编译器的成熟，对大型内联代码块进行操作的能力将为未来一系列更高级的优化打开大门。


我们使用HotSpot特定的功能来告诉编译器我们想对特定的方法做什么， 为了演示效果，我们在这个例子中写了三个测试方法。

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
 
 ## SyncIterations
 事实证明如果你用多线程来跑benchmark，你启动和停止工作线程的方式会严重影响性能。

 通常的做法是，让所有的线程都挂起在一些有序的屏障上，然后让他们一起开始。然而，这种做法是不奏效的：没有谁能够保证工作线程在同一时间开始，这就意味着其他工作线程在更好的条件下运行，从而扭曲了结果。

更好的解决方案是引入虚假迭代，增加执行迭代的线程，然后将系统自动切换为测试任务。在减速期间可以做同样的事情，这听起来很复杂，但是JMH已经帮你处理好了。
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
## Control

有时你需要进入控制思想以获得关于过渡变化的信息。为此，我们有实验状态对象，Control，它在我们运行时被JMH更新。 ？？？

 在这个例子中，我们想要估计简单的AtomicBoolean的ping / pong速度。幸的是，以天真的方式执行此操作将会锁定其中一个线程，因为ping / pong的执行不能完美配对。 如果线程即结束执行，我们需要"逃生舱口"来终止循环。
 ```java
 @State(Scope.Group)
public class JMHSample_18_Control {

    /*
     * Sometimes you need the tap into the harness mind to get the info
     * on the transition change. For this, we have the experimental state object,
     * Control, which is updated by JMH as we go.
     */

    /*
     * In this example, we want to estimate the ping-pong speed for the simple
     * AtomicBoolean. Unfortunately, doing that in naive manner will livelock
     * one of the threads, because the executions of ping/pong are not paired
     * perfectly. We need the escape hatch to terminate the loop if threads
     * are about to leave the measurement.
     */

    public final AtomicBoolean flag = new AtomicBoolean();

    @Benchmark
    @Group("pingpong")
    public void ping(Control cnt) {
        while (!cnt.stopMeasurement && !flag.compareAndSet(false, true)) {
            // this body is intentionally left blank
        }
    }

    @Benchmark
    @Group("pingpong")
    public void pong(Control cnt) {
        while (!cnt.stopMeasurement && !flag.compareAndSet(true, false)) {
            // this body is intentionally left blank
        }
    }

    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     *
     * You can run this test:
     *
     * a) Via the command line:
     *    $ mvn clean install
     *    $ java -jar target/benchmarks.jar JMHSample_18 -t 2 -f 1
     *    (we requested 2 threads and single fork; there are also other options, see -h)
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_18_Control.class.getSimpleName())
                .threads(2)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

}
```

##  Annotations
除了运行时所有的命令行选项外，我们还可以通过注解给一些基准测试提供默认值。在你处理大量基准测试时这个很有用，其中一些需要特别处理。

注解可以放在class上，来影响这个class中所有的基准测试方法。规则是，靠近作用域的注解有优先权：比如，方法上的注解可以覆盖类上的注解；命令行优先级最高。

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


## ConsumeCPU
有时您需要测试烧毁一些循环而不做任何事情。在许多情况下，您“确实”希望烧毁循环而不是等待。

对于这种场景，我们有基础设施支持。Blackhole不仅可以消耗数值，还可以消耗时间！运行此测试以熟悉JMH的这一部分。
(注意：在使用静态方法时，因为大多数用例都在测试代码的深处，传递Blackhole会变得很繁琐)


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

## FalseSharing 

伪共享引发的错误可能让你大吃一惊。若干两个线程访问内存中两个相邻的值，它们很可能修改的是同一缓存行上的值，这就导致程序的执行明显变慢。

JMH能够使用@State自动填充解决这个问题。但是这种填充没有在@State内部实现，需要开发手动处理。


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

## AuxCounters <Badge text="beta" type="warning"/>


在一些奇怪的情况下，您需要根据当前代码的结果为基准代码获得单独的吞吐量/时间度量。
为了适配这种情况，JMH提供了可选的注解 @AuxCounters， 它能够将@State对象作为承载用户计数器的对象。


AuxCounters注释可用于将State对象标记为辅助辅助结果的承载者。 使用此注释标记类将使 JMH 将其公共字段和返回结果的公共方法视为二级基准指标的基础。
特性：

并非每个BenchmarkMode都可以使用辅助计数器，因为并非每个模式都计算时间或操作。 始终支持Mode.AverageTime和Mode.Throughput 。
AuxCounters注解仅适用于Scope.Thread状态对象。 将它与其他状态一起使用是编译时错误。 这意味着计数器本质上是线程本地的。

只有公共字段和方法被视为指标。 如果您不希望将字段/方法捕获为指标，请不要将其设置为pulic。

只有数字字段和数字返回方法被视为指标。 这些包括所有原语及其相应的盒装 counterType，除了boolean / Boolean和char / Character 。 使用类型不兼容的公共字段/方法是编译时错误。

具有void返回类型的方法免于类型检查。 这意味着辅助Setup和TearDown方法在AuxCounters中AuxCounters 。
AuxCounters实例中的公共字段将在开始迭代之前重置，并在迭代结束时回读。 这允许基准代码避免对这些对象进行复杂的生命周期处理。
计数器名称是从字段/方法名称生成的。 计数器的命名空间在参与运行的所有状态之间共享。 如果对于来自AuxCounters类的计数器存在歧义，JMH 将无法编译基准测试。

警告：这是一个实验性的 API，将来可能会随时会被更改或删除。 请小心使用。

AuxCounters可以设置的值共两个选项。
Type.OPERATIONS: 统计benchmark方法执行次数。如果每次@Benchmark方法被调用时都增加计数值，最终的得到的指标回合benchmark 的结果相近。

Type.EVENTS: 计算“事件”，即工作负载生命周期中一次性的事情。此计数器不会按时间标准化。

```java
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class JMHSample_23_AuxCounters {

    /*
     * In some weird cases you need to get the separate throughput/time
     * metrics for the benchmarked code depending on the outcome of the
     * current code. Trying to accommodate the cases like this, JMH optionally
     * provides the special annotation which treats @State objects
     * as the object bearing user counters. See @AuxCounters javadoc for
     * the limitations.
     */

    @State(Scope.Thread)
    @AuxCounters(AuxCounters.Type.OPERATIONS)
    public static class OpCounters {
        // These fields would be counted as metrics
        public int case1;
        public int case2;

        // This accessor will also produce a metric
        public int total() {
            return case1 + case2;
        }
    }

    @State(Scope.Thread)
    @AuxCounters(AuxCounters.Type.EVENTS)
    public static class EventCounters {
        // This field would be counted as metric
        public int wows;
    }

    /*
     * This code measures the "throughput" in two parts of the branch.
     * The @AuxCounters state above holds the counters which we increment
     * ourselves, and then let JMH to use their values in the performance
     * calculations.
     */

    @Benchmark
    public void splitBranch(OpCounters counters) {
        if (Math.random() < 0.1) {
            counters.case1++;
        } else {
            counters.case2++;
        }
    }

    @Benchmark
    public void runSETI(EventCounters counters) {
        float random = (float) Math.random();
        float wowSignal = (float) Math.PI / 4;
        if (random == wowSignal) {
            // WOW, that's unusual.
            counters.wows++;
        }
    }

    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     *
     * You can run this test:
     *
     * a) Via the command line:
     *    $ mvn clean install
     *    $ java -jar target/benchmarks.jar JMHSample_23
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_23_AuxCounters.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }

}
```

## Inheritance :
在某些特殊情况下，   我们可以使用模版模式通过抽象方法来分离实现。

  经验法则是：如果一些类有@Benchmark方法，那么它所有的子类都继承@Benchmark方法。 注意，因为我们只知道编译期间的类型层次结构，所以只能在同一个编译会话期间使用。也就是说，在JMH编译之后混合扩展benchmark类的子类将不起作用。
   
注释现在有两个可能的地方，这时采用就近原则，离得近的生效。验证~~

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

## API_GA

这个例子展示了在复杂场景中利用 JMH API 的一种相当复杂但有趣的方式。
到目前为止，我们还没有以编程方式使用结果，因此我们错过了所有乐趣。

让我们考虑一下这个简单的代码，它显然受到性能异常的影响，因为当前的 HotSpot 无法进行尾调用优化

我们可能可以通过更好的内联策略来弥补 TCO 的缺失。 但是手动调整策略需要了解很多关于 VM 内部的知识。 相反，让我们构建外行遗传算法，该算法通过内联设置进行筛选，试图找到更好的策略。 如果您不熟悉遗传算法的概念，请先阅读维基百科文章：http://en.wikipedia.org/wiki/Genetic_algorithm VM 专家可以猜测应该调整哪个选项以获得最大性能。 尝试运行示例，看看它是否提高了性能

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

## BatchSize
有时可能需要测试没有稳定状态的操作。基准操作的成本在不同调用时差异会很大。

在这种情况下基于时间来测试非常不靠谱。唯一可以接受的基准测试模式就是 single shot。另一方面，对于可靠的single shot测试，这种操作可能太小。？？？

我们可以使用“batch size”参数来描述每次调用执行的基准调用次数，而无需手动循环方法并防止JMHSample_11_Loops中描述的问题。

假设我们想测量列表中间的插入操作。

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

## Params 
在很多场景下，一个基准测试需要再不同的配置下运行。这些需要额外的控制，或者需要验证在不同参数下程序的性能变化。

不同参数是否取笛卡尔积？？？

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

## 

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

## BlackholeHelper
有时你不需要Blackhole在@Benchmark方法中，而是在helper(辅助？？)方法中，因为你想把它传递给在helper方法中的具体实现的实例. 在这种情况下，你可以通过helper方法签名获取Blackhole。这可以应用在被标注为@Setup和@TearDown的方法上，也包括其他JMH脚手架对象，比如Control。

{@link com.cxd.benchmark.JMHSample_08_DeadCode}是它的变种，但是他被包装在匿名类中。

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


## StatesDAG

>  THIS IS AN EXPERIMENTAL FEATURE, BE READY FOR IT BECOME REMOVED WITHOUT NOTICE!

当基准状态由一组@States 更清晰地描述时，存在一些奇怪的情况，并且这些@States 相互引用。 JMH 允许通过在辅助方法签名中引用 @States 来链接有向无环图 (DAG) 中的 @States。 （请注意， org.openjdk.jmh.samples.JMHSample_28_BlackholeHelpers只是其中的一个特例。遵循@Benchmark 调用的接口，所有引用@State-s 的@Setups 在当前@State 可以访问之前都会被触发。类似地，在完成当前@State 之前，不会为引用的@State 触发@TearDown 方法。
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

## Interrupts
JMH还可以检测线程何时卡在基准测试中，并尝试强制中断基准线程。 在可以确定它不会影响测量时，JMH会尝试这样做。

在这个例子中，我们想测量ArrayBlockingQueue的简单性能特征。 不幸的是，在没有工具支持的情况下执行此操作会使其中一个线程死锁，因为take / put的执行不能完美配对。

 幸运的是，这两种方法都能很好地应对中断，因此我们可以依赖JMH来中断测量。 JMH将通知用户有关中断操作的信息，因此用户可以查看这些中断是否会影响测量。
 在达到默认或用户指定的超时后，JMH将开始发出中断。这是 {@link com.cxd.benchmark.JMHSample_18_Control}的一个变种，但是没有明确的控制对象。
这个例子很适合那些需要优雅应对中断的方法。


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
## InfraParams
 通过JMH提供的脚手架获取JMH的一些运行信息

有一种方式用来查JMH并发运行的模型。通过请求注入以下三个脚手架对象我们就可以做到：
     - BenchmarkParams: 涵盖了benchmark的全局配置
     - IterationParams: 涵盖了当前迭代的配置
     - ThreadParams: 涵盖了指定线程的配置
假设我们想检查ConcurrentHashMap如何在不同的并行级别下差异。我们可以可以把并发级别通过@Param传入， 但有时不方便，比如，我们想让他和@Threads一致。以下是我们如何查询JMH关于当前运行请求的线程数， 并将其放入ConcurrentHashMap构造函数的concurrencyLevel参数中。

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
## BulkWarmup
这是JMHSample_12_Forking测试的补充。

预热方式不同测量的结果大不一样。

有时你想要一个相反的配置：您可以将它们混合在一起以测试最坏情况，而不是分离不同基准的配置文件。

 JMH有一个批量预热特性：它首先预热所有测试，然后测量他们。JMH仍然为每个测试fork出一个JVM，但当新的JVM启动，所有的预热在测量开始前都会执行。
 这有助于避免类型配置文件偏差，因为每个测试仍然在不同的JVM中执行，我们只“混合”我们想要的预热代码。
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

## SecurityManager


