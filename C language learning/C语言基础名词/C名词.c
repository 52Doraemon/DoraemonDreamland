//0、注释：

//一般来说，注释用于标注这段代码的用途或解释思路等。因为注释不会被当作代码进行编译，所以无论添加什么注释内容，都不会对代码的实际运行产生任何影响。这个文件里双斜杠后面的就是注释的一种 

//1、主函数main：

//main翻译成中文是主要的、最重要的意思，而在C语言里面表示一个主函数。主函数是整个C语言程序的入口。所有的C语言代码都有一个起始入口，而这个入口就是 主函数main 。进入了主函数以后，才能经由主函数来调用其他函数。这也意味着，每个C语言代码，只能有且只有一个main函数。

//2、函数：

//数学领域中的函数，与编程语言中的函数完全不同。在编程语言里面，可以把函数看做一个盒子，这个盒子有如下几个特性：
//1. 开始执行时，函数可以被输入一些值
//2. 执行过程中，函数可以做一些事情
//3. 执行完成后，函数可以返回一些值

//3、函数的定义：

//写/定义一个自己的函数

//4、要调用函数，必须先知道函数：

//编译器会从代码开始，按照从上往下的顺序阅读代码。编译器首先看到了一个函数的定义，描述了一个叫 xxx 的函数。接着，在 main中需要使用 xxx ，由于编译器已经知道了 xxx 的定义，因此编译器可以正常编译通过。如果在使用函数前即没有函数定义也没有说明，编译器无法理解xxx究竟是什么。因此，编译器将报错，并停止编译。

//5、变量：

//什么是变量呢？你可以把它看做一个空箱子，里面可以装任何其他的和它类型一致的值。变量必须先声明后使用。

//6、标识符：

//标识符由我们自己命名的一个特殊标识，用于表示一个变量、函数或其他实体的名称。并且，要让编译器能够识别标识符，必须进行声明或定义。标识符可以用小写字母、大写字母、数字和下划线来命名。但是，标识符的第一个字符必须是字母或下划线，而不是数字。并且，标识符区分大小写。

//7、关键字：

//关键词 是在语言标准中规定的，并且在代码中有特殊意义和用途。因此，关键词不能作为一个标识符来使用。

//8、字面常量：

//像2，3，这种数值，需不需要声明呢？不需要，他们是常量，无法被更改。并且一旦被写出来，就已经知道它们是整型int类型的常量了。同样的，字符串字面常量也不需要被声明，例如："Hello World\n"。被双引号包裹的，我们认为它是一个字符串，以区别于数值。变量我们可以通过赋值来更改，常量不能更改，所以不能对它进行赋值。
2 = 3; // 错误
"Hello" = "World"; // 错误

//9、#include命令

//printf函数并不是我们定义的函数，而是系统自带的函数。这个函数被写在文件stdio.h 中，我们要使用printf就必须先让编译器理解printf。我们假定printf的函数定义写在文件 stdio.h中，用 #include 命令，可以将文件 stdio.h 的代码复制到我们的代码中。TIPS：stdio.h里面并未定义printf函数，但是它里面有printf函数的函数声明。但是，在当前阶段里面，可以理解为stdio.h里面，写了printf的函数定义。

//10、占位符

//printf("XXX占位1 XXX 占位2 XXX占位3", 替换1, 替换2, 替换3);printf 的第一个参数必须是字符串，这里我们传入了一个字符串字面常量（被双引号包裹）。其中，占位用 %转换操作表示。例如：整型int的占位符为 %d 。后面的替换参数，会依次替换前面的占位。printf是一个变长参数函数，只要第一个字符串参数占位符写对了，后面可以加任意多的替换参数。