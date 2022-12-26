//Mapper代理开发:
//目的：
//解决原生方式中的硬编码
//简化后期执行SQL

//问题所在：
//3.执行sql
List<User> users = sqlSession.selectList("test.selectAll");
//传入对应sql的唯一标识,使用sql语句：namespace.id，硬编码问题，mapper代理解决

//使用Mapper代理开发规则：
//1.定义与SQL映射文件同名的Mapper接口，并且将Mapper接口和SQL映射文件放置在同一目录下
//2.设置SQL映射文件的namespace属性为Mapper接口全限定名
//3.在Mapper接口中定义方法，方法名就是SQL映射文件中sql语句的id，并保持参数类型和返回值类型一致
//4.编码
//4.1通过SqlSession的getMapper方法获取Mapper接口的代理对象
//4.2调用对应方法完成sql的执行

//细节:如果Mapper接口名称和SQL映射文件名称相同，并在同一目录下，则可以使用包扫描的方式简化SQL映射文件的加载
//mybatis核心配置文件中<package name="com.cym.mapper"/>取代<mapper resource="com/cym/mapper/UserMapper.xml"/>
//意思是将com.cym.mapper包下的所有SQL映射文件全部加载