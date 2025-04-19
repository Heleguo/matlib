## 本仓库内容

此lib为针对Paper环境的服务器的Spigot插件开发所提供的工具库合集

### 使用方法

1. 在依赖仓库中加入jitpack
2. 按照jitpack的标准加入依赖 <br>maven配置项如下<br>记得手动刷新,或者将版本号改成指定版本,或者打开maven的配置always update snapshots
```
<dependency>
   <groupId>com.github.m1919810</groupId>
   <artifactId>matlib</artifactId>
   <version>版本号自己在github找最新commit</version>
   <scope>compile</scope>
</dependency>
```

3. 最好执行relocation,因为我们是compile进入jar包(同路径会观感不好) 除非你决定调用某个包中已经编译好的matlib
```
<build>
    <plugins>
        <plugin>
            <configuration>
                <relocations>
                    <relocation>
                        <pattern>me.matl114.matlib</pattern>
                        <shadedPattern>your.own.proj.matlib</shadedPattern>
                    </relocation>
                </relocations>
            </configuration>
        </plugin>
    </plugins>
</build>
```
4. (可选)在插件启动(onEnable)中,创建一个Initialization对象并调用onEnable<br>你可以使用displayName()方法为你的插件设置别称/中文名<br>在插件卸载(onDisable)中,调用这个对象的onDisable()<br>你可以根据所需要的工具库范围不同来决定使用core中的哪个等级的Initialization子类,<br>你也可以不使用Initialization,因为大部分工具都在clinit中自动执行所需的初始化
5. 关于不同的插件中编译了不同的lib,如何进行信息互通<br>我们提供了matlibAdaptor,里面拥有部分主要互通会涉及的类所抽象出的主要接口.我们通过注解标注了通用的类和可安全调用的方法,<br>你可以用ProxyBuilder中的方法创建一个关联目标对象的实现该接口的适配器<br>以此调用目标对象的方法<br>你也可以自行使用反射工具调用目标对象,但是**注意cast的安全性**

