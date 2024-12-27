## 本仓库内容

此lib为针对SlimefunAddon开发的工具库合集

### 使用方法

1. 在依赖仓库中加入jitpack
2. 按照jitpack的标准加入依赖 <br>maven配置项如下<br>记得手动刷新,或者将版本号改成指定版本,或者打开maven的配置always update snapshots
```
<dependency>
   <groupId>com.github.m1919810</groupId>
   <artifactId>matlib</artifactId>
   <version>-SNAPSHOT</version>
   <scope>compile</scope>
</dependency>
```

3. 最好执行relocation,因为初始化时会在类的static成员中存入你的plugin信息
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
4. 在插件启动(onEnable)中,创建一个AddonInitialization对象并调用onEnable<br>你可以使用displayName()方法为你的插件设置别称/中文名<br>在插件卸载(onDisable)中,调用这个对象的onDisable()


