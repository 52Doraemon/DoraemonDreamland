*************************************************************************
//1、整型数据类型
*************************************************************************
//整型所占字节大小
*************************************************************************
//用sizeof关键词来测量大小。和int一样，sizeof是C语言中的一个关键词。它是英文size of连起来的合成词。翻译成中文就是什么东西的大小的意思。它能够测量C语言各种实体所占用的字节大小。想看int所占用的字节大小，可以这样写sizeof(int)。

//执行后这段代码后，它的测量结果是一个整型。我们可以借助printf函数将测量结果显示在控制台上。我们现在可以假设sizeof返回的结果是int类型的，在printf函数中使用占位符%d。而更准确的用法，应该用%zu。测量int类型所占用的字节大小，并将结果打印在控制台上的代码如下：
printf("%d\n", sizeof(int));

//sizeof后面既可以跟类型，也可以跟变量、常量。
//1. 跟类型，测类型所占用字节的大小。
//2. 跟变量，测变量的类型所占用字节大小。
//3. 跟常量，测常量的类型所占用字节大小。
//下面是以上三种情况的示例代码。
int a;
printf("sizeof int = %d\n", sizeof(int)); // 1.测类型所占用字节的大小
printf("sizeof a = %d\n", sizeof(a)); // 1.测变量的类型所占用字节大小
printf("sizeof 123 = %d\n", sizeof(123)); // 1.测常量的类型所占用字节大小

//查看C语言中提供的各种整型类型的大小。
printf("sizeof char=%d\n", sizeof(char));
printf("sizeof short=%d\n", sizeof(short));
printf("sizeof int=%d\n", sizeof(int));
printf("sizeof long=%d\n", sizeof(long));
printf("sizeof long long=%d\n", sizeof(long long));
//结果：char，short，int，long，long long分别占用了1，2，4，4，8个字节。至此，已经得知了它们所占字节大小，并且验证了可以表示越大范围的数据类型所占用的字节越多。值得注意的是在VisualStudio2019中，int和long均占用4个字节。这并未违反C语言标准，C语言标准规定高级别的类型取值范围不得小于低级别的类型，但是它们可以是一致的。
*************************************************************************
//数据表示范围：假设，字节为n，则数据范围从【0】开始，到【2的n次方-1】的数值范围。
*************************************************************************
//2、浮点数据类型

*************************************************************************
//浮点类型：float
*************************************************************************
#include <stdio.h>
int main()
{
	float a = 1.234567;
	float b = 0.00001;
	float c = 365.12345;
	printf("%f\n", a);
	printf("%f\n", b);
	printf("%f\n", c);
	return 0;
}
//将int替换成float之后，大部分的数据都是正确的。但是365.12345变成了365.123444 ,很明显精度出现了误差。这是因为，浮点数并不能表示无限的精确，它会存在着一定的误差。C标准规定，float类型必须至少能表示6位有效数字，并且取值范围至少是10^-37~10+37。所以，使用float来装365.12345时，前面六位数值是准确的，但是后面的数值略有误差。
*************************************************************************
//浮点类型：double
*************************************************************************
#include <stdio.h>
int main()
{
	double a = 1.234567;
	double b = 0.00001;
	double c = 365.12345;
	printf("%f\n", a);
	printf("%f\n", b);
	printf("%f\n", c);
	return 0;
}
//这次 365.12345 也是正确的了。但是，请注意double类型也是有精度范围的。如果是更高精度的数据，double也会出现误差。
*************************************************************************
//浮点类型所占字节大小
*************************************************************************
//按照之前对整型的经验，越大范围的整型类型所占的空间越大。那么对于浮点类型来说，越高精度、越大范围的浮点类型，应该也会占用越大的空间。
#include <stdio.h>
int main()
{
	printf("sizeof float = %d\n", sizeof(float));
	printf("sizeof double = %d\n", sizeof(double));
	return 0;
}
//float，double分别为4，8个字节。