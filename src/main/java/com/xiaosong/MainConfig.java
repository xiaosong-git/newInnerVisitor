package com.xiaosong;

import com.alibaba.druid.filter.logging.Log4jFilter;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.plugin.redis.RedisPlugin;
import com.xiaosong.cache.DictionaryCache;
import com.xiaosong.constant.Constant;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log4jLogFactory;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.server.undertow.UndertowServer;
import com.jfinal.template.Engine;
import com.jfinal.template.source.ClassPathSourceFactory;
import com.xiaosong.model._MappingKit;
import com.xiaosong.routes.GlobalRoutes;
import com.xiaosong.util.ESRedisPlugin;
import com.xiaosong.websocket.WebSocketEndPoint;

/**
 * 本 demo 仅表达最为粗浅的 jfinal 用法，更为有价值的实用的企业级用法
 * 详见 JFinal 俱乐部: http://jfinal.com/club
 * 
 * API 引导式配置
 */
public class MainConfig extends JFinalConfig {
	
	static Prop p;
	
	/**
	 * 启动入口，运行此 main 方法可以启动项目，此 main 方法可以放置在任意的 Class 类定义中，不一定要放于此
	 */
	public static void main(String[] args) {
		UndertowServer.create(MainConfig.class).configWeb(builder -> {
			builder.addWebSocketEndpoint(WebSocketEndPoint.class);
		}).start();
//		UndertowServer.start(DemoConfig.class);
	}
	
	/**
	 * PropKit.useFirstFound(...) 使用参数中从左到右最先被找到的配置文件
	 * 从左到右依次去找配置，找到则立即加载并立即返回，后续配置将被忽略
	 */
	static void loadConfig() {
//		if (p == null) {
//			p = PropKit.useFirstFound("db_develop.properties", "demo-config-dev.txt");
//		}
		if(Constant.DEV_MODE){
			p = PropKit.use("db_develop.properties").append("config_develop.properties");
		}else{
			p = PropKit.use("db_product.properties").append("config_product.properties");
		}
	}
	
	/**
	 * 配置常量
	 */
	public void configConstant(Constants me) {
		loadConfig();
		me.setDevMode(Constant.DEV_MODE);//是否开发模式 上生产是需要改变 与JFInal框架有关
		me.setMaxPostSize(1024 * 1024 * 20);//默认最大上传数据大小
		me.setLogFactory(new Log4jLogFactory());//日志配置
		me.setBaseUploadPath(Constant.BASE_UPLOAD_PATH);//文件上传路径
		me.setBaseDownloadPath(Constant.BASE_DOWNLOAD_PATH);//文件下载路径
//		me.setDevMode(p.getBoolean("devMode", false));
		/**
		 * 支持 Controller、Interceptor、Validator 之中使用 @Inject 注入业务层，并且自动实现 AOP
		 * 注入动作支持任意深度并自动处理循环注入
		 */
		me.setInjectDependency(true);
		// 配置对超类中的属性进行注入
		me.setInjectSuperClass(true);

		//slf4j
//		me.setToSlf4jLogFactory();

	}
	
	/**
	 * 配置路由
	 */
	public void configRoute(Routes me) {
//		me.add("/", IndexController.class, "/index");	// 第三个参数为该Controller的视图存放路径
//		me.add("/blog", BlogController.class);			// 第三个参数省略时默认与第一个参数值相同，在此即为 "/blog"

		me.add(new GlobalRoutes());
	}
	
	public void configEngine(Engine me) {
		me.addSharedFunction("/common/_layout.html");
		me.addSharedFunction("/common/_paginate.html");
		me.setDevMode(Constant.DEV_MODE);
		me.addSharedObject("sk", new com.jfinal.kit.StrKit());
	}
	
	/**
	 * 配置插件
	 */
	public void configPlugin(Plugins me) {
		System.out.println(PropKit.get("password").trim());
		// 配置 druid 数据库连接池插件
		DruidPlugin druidPlugin = new DruidPlugin(p.get("jdbcUrl"), p.get("user"), p.get("password").trim());
		// 配置ActiveRecord插件
		ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
		arp.setShowSql(true);
		arp.setShowSql(Constant.DEV_MODE);
		arp.setDialect(new MysqlDialect());
		//		arp.setBaseSqlTemplatePath(PathKit.getRootClassPath());//该方法会把资源目录定义到test-class目录下
		arp.getEngine().setSourceFactory(new ClassPathSourceFactory());
		arp.addSqlTemplate("jxt.sql");//复杂sql写在这个文件中 以namespace和sqlname获取对应sql，sql条件可用质量写
		//获取sql可以使用dao.getSqlPara或Db.getSqlPara获取
		arp.getSqlKit().getEngine().addSharedObject("sk", new com.jfinal.kit.StrKit());//添加共享sql引擎
		// 所有映射在 MappingKit 中自common动化搞定
		//注意，如果有数据库的话，这里还是需要添加其他的配置的
		//数据库映射统一使用GeneratorModel生成，无需而外配置
		_MappingKit.mapping(arp);
		// 配置log插件
		Log4jFilter logFilter = new Log4jFilter();
		logFilter.setStatementLogEnabled(false);
		logFilter.setStatementLogErrorEnabled(true);
		logFilter.setStatementExecutableSqlLogEnable(true);
		druidPlugin.addFilter(logFilter);
      	//添加到插件列表中
		me.add(druidPlugin);
		me.add(arp);
		me.add(new EhCachePlugin());
		String cacheType = PropKit.get("cache.type").trim();
		if("redis".equals(cacheType)){

			RedisPlugin redisPlugin = new ESRedisPlugin().config();//默认配置
			RedisPlugin code = new ESRedisPlugin().config("code",1);//库1 验证码
			RedisPlugin db2 = new ESRedisPlugin().config("db2",2);//库2 实名认证 公告
//			RedisPlugin db31 = new ESRedisPlugin().config("db31",31);//库8
//			RedisPlugin db33 = new ESRedisPlugin().config("db33",33);//库33
			me.add(redisPlugin).add(code).add(db2);
		}
	}
	
	public static DruidPlugin createDruidPlugin() {
		loadConfig();
		
		return new DruidPlugin(p.get("jdbcUrl"), p.get("user"), p.get("password").trim());
	}
	
	/**
	 * 配置全局拦截器
	 */
	public void configInterceptor(Interceptors me) {
		
	}
	//项目启动后操作，常用场景可以加载一些定时任务JOB类可在此处加载启动
	@Override
	public void onStart() {
		DictionaryCache dic = new DictionaryCache();
		dic.intoCache();
	}

	/**
	 * 配置处理器
	 */
	public void configHandler(Handlers me) {
		
	}
}
