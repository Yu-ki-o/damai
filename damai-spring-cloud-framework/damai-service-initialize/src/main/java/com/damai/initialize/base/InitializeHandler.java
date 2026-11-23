package com.damai.initialize.base;

import org.springframework.context.ConfigurableApplicationContext;

/**
 * @program: 极度真实还原大麦网高并发实战项目。 添加 阿星不是程序员 微信，添加时备注 大麦 来获取项目的完整资料 
 * @description: 初始化执行 顶级抽象 接口
 * @author: 阿星不是程序员
 **/

// Handler类的顶级抽象接口，定义了初始化执行的类型、执行顺序和执行逻辑方法。
//在此接口的基础上又往下拆分出四个抽象类，分别对应不同的初始化执行方式：
// AbstractApplicationCommandLineRunnerHandler：用于处理 CommandLineRunner 类型的初始化执行。
//四个抽象类中都实现了 type() 方法，返回对应的初始化执行类型常量。这样做的好处是可以通过类型来区
//分不同的初始化执行方式，从而在实际使用中根据需要选择合适的抽象类进行继承和实现。
public interface InitializeHandler {
    /**
     * 初始化执行 类型
     * @return 类型
     * */
    String type();
    
    /**
     * 执行顺序
     * @return 顺序
     * */
    Integer executeOrder();
    
    /**
     * 执行逻辑
     * @param context 容器上下文
     * */
    void executeInit(ConfigurableApplicationContext context);
    
}
