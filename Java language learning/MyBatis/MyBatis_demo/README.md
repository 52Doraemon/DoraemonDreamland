该demo sql语句id地方
List<User> users = sqlSession.selectList("test.selectAll");
仍然存在硬编码问题，需要用mapper代理继续优化

该demo步骤

查询user表中所有的数据

1.创建user表，添加数据
2.创建模块，导入坐标
3.编写MyBatis核心配置文件-->连接信息解决硬编码问题
4.编写SQL映射文件-->统一管理sql语句，解决硬编码问题
5.编码
	5.1定义pojo类
	5.2加载核心配置文件，获取SqlSessionFactory对象
	5.3获取SqlSession对象，执行SQL语句
	5.4释放资源