package com.damai.initialize.execute.base;

import com.damai.initialize.base.InitializeHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Comparator;
import java.util.Map;

/**
 * @program: 极度真实还原大麦网高并发实战项目。 添加 阿星不是程序员 微信，添加时备注 大麦 来获取项目的完整资料 
 * @description: 用于处理应用程序启动执行的基类
 * @author: 阿星不是程序员
 **/

// 该抽象类用于处理应用程序启动时的初始化执行逻辑。它通过依赖注入获取 Spring 的应用上下文，
// 并在 execute() 方法中查找所有实现了 InitializeHandler 接口的 Bean。
// 然后根据type类型过滤出符合条件的 Handler(因为对应的Handler中也设置了type类型)，按照执行顺序进行排序，
// 并依次调用它们的 executeInit() 方法来执行具体的初始化逻辑。
// 子类需要实现 type() 方法，以指定该 Handler 的类型，从而在执行过程中进行正确的过滤和处理。
@AllArgsConstructor
public abstract class AbstractApplicationExecute {
    
    private final ConfigurableApplicationContext applicationContext;
    
    //之类不管是 CommandLineRunner 还是 PostConstruct 的Execute都会调用这个方法，去执行对应的初始化逻辑
    //不同的类别的Execute会通过各自的机制和时机去触发这个方法的调用，例如 CommandLineRunner 会在应用启动后自动调用 run() 方法，
    // 而 PostConstruct 会在 Bean 初始化后自动调用带有postConstruct注解的方法，这个方法中带有execute方法。
    //注意这里的execute方法是一个通用的执行逻辑，它会根据子类实现的type()方法来过滤出对应类型的InitializeHandler，并按照执行顺序进行排序和执行。
    //因为不同的业务应用是隔离的，一个业务的所有初始化逻辑使用相同的type()方法就可以了，其他业务的初始化逻辑也可以使用相同的type()方法，这样就实现了不同业务之间的隔离。
    //如果一个应用中涉及了不同业务的handler，并且这些handler的type()方法返回了相同的类型，那么在执行execute()方法时，这些handler就会被一起执行，可能会导致不同业务之间的逻辑混乱和冲突。
    public void execute(){
        Map<String, InitializeHandler> initializeHandlerMap = applicationContext.getBeansOfType(InitializeHandler.class);
        initializeHandlerMap.values()
                .stream()
                .filter(initializeHandler -> initializeHandler.type().equals(type()))
                .sorted(Comparator.comparingInt(InitializeHandler::executeOrder))
                .forEach(initializeHandler -> {
                    initializeHandler.executeInit(applicationContext);
                });
    }
    /**
     * 初始化执行 类型
     * @return 类型
     * */
    public abstract String type();
}
