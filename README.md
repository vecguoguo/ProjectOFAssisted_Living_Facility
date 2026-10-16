# 东软颐养中心管理系统

| 项目名称 | 东软颐养中心管理系统                 |
| -------- | ------------------------------------ |
| Maven 坐标 | `com.neusoft:yyzx-javase:1.0-SNAPSHOT` |
| 运行形态 | 纯 JDK HttpServer + Vue3 前后端分离（Web）＋ JavaSE 控制台（双入口），后端零第三方 Web 框架 |
| 开发语言 | Java 21                              |
| 数据库   | MySQL 8.0（InnoDB / utf8mb4）        |
| 密级     | 秘密                                 |

> 本工程实现《东软颐养中心-需求规格说明书》的核心业务。业务层采用
> **手写三层架构 + 原生 JDBC**，在其之上提供两种运行形态：
> ① **Web 版**——纯 JDK `HttpServer` + 自研极简 MVC 提供 REST API（8080）+ Vue3 前端（5173），为需求文档的目标形态；
> ② **控制台版**——`com.neusoft.yyzx.Main` 菜单交互，为教学演示实现。
> 两者共用同一套 Service / DAO / 实体，互不影响。启动方式见
> [startProject.md](startProject.md)。

---

## 1 目录结构

```
ddsy/
├── pom.xml                       # Maven 构建（Java 21 + MySQL 驱动 + 日志；Web 层纯 JDK，无 Spring）
├── README.md                     # 本文档
├── startProject.md               # 启动指南（Web 前后端 / 控制台）
├── 东软颐养中心-测试数据记录.xlsx  # 测试数据说明
├── sql/
│   ├── 建表语句.sql              # 全库建表脚本（18 张表）
│   ├── clean_table.sql          # 清空全部 18 张表
│   └── data_insert.sql          # 联测数据（10 客户 + 5 健康管家）
├── project_guide/                # 需求与设计资料
├── frontend/                     # Vue3 + Vite + Element Plus 前端
│   ├── package.json             # 前端依赖与脚本（dev/build/preview）
│   ├── vite.config.js           # 端口 5173 + /api 代理到 8080
│   └── src/                     # api/ router/ stores/ views/ layout/
├── tools/                        # 本地辅助脚本（PowerShell：启动/重启、接口冒烟与压测、数据重载等）
└── src/
    ├── main/java/com/neusoft/yyzx/
    │   ├── Main.java            # 控制台入口（com.neusoft.yyzx.Main）
    │   ├── view/                # 控制台会话 Session
    │   ├── web/                 # ★ Web 层（纯 JDK HttpServer + 自研极简 MVC，无 Spring）
    │   │   ├── WebApplication.java     # 启动类（com.sun.net.httpserver，REST API 端口 8080）
    │   │   ├── controller/             # 各模块控制器（@RestApi/@Route，/api 前缀，10 个）
    │   │   ├── mvc/                    # 自研极简 MVC：Router/Json + 注解(@RestApi/@Route/@Http/@Path/@Query/@Body/@Token/@CurrentUser/@Bind)
    │   │   ├── security/TokenManager   # 内存 token 登录态（WebApplication.handle 统一校验）
    │   │   ├── config/ exception/      # 预留空目录（未使用）
    │   │   └── vo/ util/               # 视图对象(LoginVO) / 参数工具(ParamUtil)
    │   ├── service/             # 业务层接口 + impl/（事务处理）
    │   ├── dao/                 # 数据访问层接口 + impl/ 实现
    │   ├── db/                  # DBManager + ConnectionPool + TxManager + JdbcTemplate（原生 JDBC + 自研连接池）
    │   ├── mapping/             # ResultSet → 实体 的映射工厂
    │   ├── entity/              # 实体类（18 个，对应 18 张表）
    │   └── common/              # 常量、统一返回结果、异常、工具（含 utils/PasswordUtil：PBKDF2 密码哈希）
    ├── main/resources/
    │   ├── db.properties          # 数据库连接 + 连接池配置（纯 JDK 读取，非 Spring datasource）
    │   └── log4j.properties       # 日志配置（控制台 + 滚动文件）
    └── test/                      # 单元测试目录
```

---

## 2 技术栈与依赖

| 类别     | 说明                                                     |
| -------- | -------------------------------------------------------- |
| 语言/ JDK | Java 21（`maven.compiler.source/target=21`）             |
| 构建工具 | Maven（后端） + Vite（前端）                             |
| Web 框架 | 无第三方框架：JDK 内置 `com.sun.net.httpserver.HttpServer` + 自研极简 MVC（`Router`/`Json`/注解）提供 REST API |
| 前端     | Vue 3 + Vite 6 + Element Plus + Pinia + Vue Router + Axios |
| 数据库   | MySQL，驱动 `com.mysql:mysql-connector-j:9.0.0`          |
| 数据访问 | 原生 JDBC + 自研 `JdbcTemplate`/`ConnectionPool`/`TxManager`（无 MyBatis / Spring Data / HikariCP） |
| 日志     | `log4j:1.2.16` + `commons-logging:1.1.1`                 |
| 运行形态 | ① Web 前后端分离（8080 API + 5173 页面，token 鉴权）② 控制台菜单交互 |

---

## 3 分层架构

采用经典三层结构，事务通过自研模板 `TxManager.tx/query` 在 Service 层统一管理
（借连接 → 执行 → `commit`/`rollback` → `finally` 归还连接池）：

```
表现层      Web：WebApplication + controller/（REST，/api 前缀，token 鉴权）
            控制台：Main / Session / ConsoleUtil
      │  调用（两种入口共用同一套业务层）
service（业务层） LoginService / CustomerService / BedService / Nurse... 
      │  组装事务、校验业务规则
dao（数据层）    UserDAO / CustomerDAO / BedDAO / BedDetailsDAO / ...
      │  继承 BaseDao，注入 Connection
db + mapping     DBManager + ConnectionPool + TxManager / JdbcTemplate（SQL 执行）
                 MappingFactory（工厂，ResultSet→Entity 映射）
      │
   MySQL（yyzx 库，18 张表）
```

关键设计：

- **`DBManager`**：单例，读取 `db.properties` 后委托自研 `ConnectionPool` 借出连接；
  获取连接失败直接抛异常（fail-fast），不再返回 `null` 掩盖故障。
- **`ConnectionPool`**：纯 JDK 自研连接池（`synchronized`+`wait/notify`），用动态代理
  包装物理连接，把 `close()` 拦截为「归还入池」而非真关闭；`pool.maxSize` 控上限、
  `pool.borrowTimeoutMs` 控池满时借出等待超时。
- **`TxManager`**：事务模板 `tx(...)` / `query(...)`，统一负责借连接 → 执行 →
  `commit`/`rollback` → `finally` 归还，杜绝连接泄漏；Service 层以 lambda 方式使用。
- **`JdbcTemplate`**：提供 `insert` / `update` / `query` / `queryOne` / `queryDouble`
  等 JDBC 封装，接收当前事务连接执行。
- **`PasswordUtil`**：PBKDF2-HMAC-SHA256 加盐哈希（`pbkdf2$迭代数$盐$哈希`），
  常量时间比对；历史明文在首次登录成功时自动透明升级为哈希。
- **`MappingFactory`**：工厂模式注册 `UserMapping`、`BedMapping`、
  `BedDetailsMapping`、`CustomerMapping`、`OutwardMapping`、`BackdownMapping`，
  负责结果集到实体的映射。
- **`BaseDao`**：DAO 基类，通过 `setConnection(conn)` 注入当前事务连接。
- **`Result<T>` / `ResultCode`**：统一返回结构（code + message + data），
  业务失败通过状态码表达（如 1001 用户名或密码错误、2001 床位非空闲、
  3001 合同到期时间不合法）。
- **`Session`**：控制台会话，保存当前登录用户，模拟 Web 版登录态，
  提供 `isAdmin()` 等角色判断（1-管理员，2-健康管家）。
- **`web/`（Web 层）**：`WebApplication` 基于 JDK `HttpServer`（16 线程池）启动，仅注册
  `/api` 上下文；`controller/` 经自研 `Router` 反射派发（`@RestApi`/`@Route` 等注解）暴露
  REST 接口，直接复用同一套 `service`；登录态由 `TokenManager` 校验（除 `/api/auth/login`
  外，请求头须带 `token`），异常在 `WebApplication.handle` 内统一转 `Result`（全链路 HTTP 200）；
  CORS 亦在此处放行任意来源。持久化仍走原生 JDBC + 自研连接池，Web 层无任何 Spring 依赖。

---

## 4 已实现功能

入口类：`com.neusoft.yyzx.Main`

| 菜单项     | 功能说明                                                       |
| ---------- | -------------------------------------------------------------- |
| 登录       | 账号密码校验，最多尝试 3 次；登录成功写入 `Session`；按角色进入管理员/健康管家菜单 |
| 床位示意图 | 列出全部床位的房间号、床位号及状态（空闲/有人/外出）           |
| 入住登记   | 录入客户信息 → 校验合同日期与身份证唯一性 → 联动选择房间空闲床位 → 保存客户 + 床位置"有人" + 新增床位详情（同一事务） |
| 床位调换   | 仅可调换到空闲床位：旧床位详情置失效（结束时间=当天）→ 新增新床位详情 → 旧床位空闲、新床位有人 → 更新客户房间号/床位号 |
| 退住登记   | 管家为自己服务的客户提交退住申请 → 管理员审核：正常/死亡退住通过后床位改"空闲"，保留床位状态不变（生成审批人/审批时间） |
| 外出登记   | 管家为自己服务的客户提交外出申请 → 管理员审核：通过后床位改"外出"；老人回院后登记实际回院时间并把床位恢复"有人" |
| 护理管理（管理员） | 护理级别（查询/新增/仅改状态/级别项目配置增删）、护理项目（状态+名称组合查询/新增/修改，停用或逻辑删除时级联剔除 nurselevelitem，不影响客户已购项目）、客户护理设置（姓名模糊查客户/仅无级别可设置并批量购项/移除级别二次确认并逻辑删除该级别项目）、护理记录（选客户按时间倒序查看/逻辑删除） |
| 日常护理（健康管家） | 只查自己服务的客户（customer.user_id=登录用户）→ 查看该客户护理项目并展示服务状态（到期/欠费/数量正常）→ 执行护理：同一事务新增 nurserecord + 扣减 customernurseitem.nurse_number（数量不足回滚并报 4007） |
| 退出登录   | 清空 `Session` 并退出程序                                     |

**业务规则（与需求文档一致）：**

- 合同到期时间必须晚于入住时间；年龄根据出生日期自动计算。
- 楼栋信息不维护，固定为 `606`（见 `CustomerService.BUILDING_NO`）。
- 床位状态：1-空闲、2-有人、3-外出；审批状态：0-已提交、1-同意、2-拒绝。
- 所有业务表带 `is_deleted` 逻辑删除标记，删除后保留历史痕迹。

**实现范围**：客户管理、床位管理、护理模块、健康管家（服务对象 + 服务关注
购买/续费/移除）、用户管理，均已在**控制台与 Web 双形态**实现（Web 层经
`web/controller/` 以 REST 接口暴露，供 Vue 前端调用）；膳食管理为后续扩展项。

---

## 5 数据库

### 5.1 建库

执行 `sql/建表语句.sql`（依据 `project_guide/yyzx数据字典.xls`，共 18 张表）：

| 分类     | 数据表                                                     |
| -------- | ---------------------------------------------------------- |
| 用户权限 | `user` `role` `menu` `rolemenu`                            |
| 客户管理 | `customer` `outward` `backdown`                            |
| 床位管理 | `bed` `beddetails` `room`                                  |
| 护理管理 | `nursecontent` `nurselevel` `nurselevelitem` `nurserecord` `customernurseitem` |
| 膳食管理 | `food` `customerpreference` `meal`                         |

脚本会自动创建 `yyzx` 库并建表；与数据字典的差异（密码字段放宽为
varchar(255)、时间字段改 datetime、仅建普通索引不建外键等）见脚本末尾注释。
建表脚本不含初始数据，测试/演示数据由 `sql/clean_table.sql`（清空表）+
`sql/data_insert.sql`（灌数据）提供（含登录账号、
床位、客户、护理项目/级别、外出与退住登记等，可重复执行）。

### 5.2 连接配置

`src/main/resources/db.properties`：

```properties
jdbc.driver=com.mysql.cj.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/yyzx?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
jdbc.username=root
jdbc.password=1234

# 连接池（自研，可选；不写则用默认值）
pool.maxSize=32
pool.borrowTimeoutMs=10000
```

> 本项目 MySQL 跑在 docker 容器（容器名 `mysql`，root 密码 `1234`）。若你的
> 环境用户名/密码不同，改以上两行后重新编译即可。连接池为纯 JDK 自研，参数均有默认值，一般无需改动。

---

## 6 构建与运行

完整分步说明见 **[startProject.md](startProject.md)**，此处给速览。
**环境要求**：JDK 21、Maven 3.6+、MySQL 8.0、Node.js 18+。

### 6.1 Web 版（前后端分离，推荐）

先建库：`sql/建表语句.sql` + `sql/clean_table.sql` + `sql/data_insert.sql`（连 docker 容器 `mysql`）。

```bash
# 后端：IDEA 运行 com.neusoft.yyzx.web.WebApplication（命令行监听 8080）
mvn -q clean compile
mvn -q dependency:build-classpath -Dmdep.outputFile=target/cp.txt
java "-Dfile.encoding=UTF-8" -cp "target/classes;<cp.txt内容>" com.neusoft.yyzx.web.WebApplication

# 前端：独立进程 5173，/api 代理到 8080
cd frontend
npm install      # 首次必须（node_modules 未纳入版本库）
npm run dev
```

浏览器访问 <http://localhost:5173>，用 `admin` / `admin123` 登录。

> 注意：后端为纯 JDK 应用（非 Spring），本就没有 `mvn spring-boot:run`；请用 IDEA 运行主类
> 或上面的 classpath 方式。若报 `‘vite’ 不是内部或外部命令`，是因为还没 `npm install`。

### 6.2 控制台版

```bash
mvn -q clean compile
mvn -q dependency:build-classpath -Dmdep.outputFile=target/cp.txt
java "-Dfile.encoding=UTF-8" -cp "target/classes;<cp.txt内容>" com.neusoft.yyzx.Main
```

或在 IDEA 运行 `src/main/java/com/neusoft/yyzx/Main.java`，进入菜单交互。

日志配置见 `log4j.properties`：当前 root 级别仅输出控制台（stdout）；文件中 appender
（`logs/NEUSOFT_YYZX.log` 滚动文件、按日 HTML）已在配置中定义但未挂载到 rootCategory，需要落盘时补上即可。

---

## 7 业务需求概要

完整需求见 `project_guide/` 下的《东软颐养中心-需求规格说明书.pdf》与
《yyzx数据字典.xls》。

**角色**

| 角色           | 说明                                       | 测试账号（见 `sql/data_insert.sql`）        |
| -------------- | ------------------------------------------ | --------------------------------------- |
| 系统管理员     | 颐养中心管理人员，由后台录入，不能自行注册 | `admin` / `admin123`（赵敏）            |
| 健康管家（护工） | 养老院员工，由系统管理员统一管理           | `guanjia01`/`345678`（陈静）、`guanjia02`/`654321`（刘强）、`guanjia03`/`112222`（孙莉）、`guanjia04`/`223344`（王芳）、`guanjia05`/`445566`（李国栋） |

**模块范围**

| 模块     | 需求名称                                                     |
| -------- | ------------------------------------------------------------ |
| 客户管理 | 入住登记、退住登记、外出登记                                 |
| 床位管理 | 床位示意图、床位管理（查询/修改/床位调换）                   |
| 护理模块 | 护理项目设置、护理级别定义、客户护理设置、护理记录           |
| 健康管家 | 设置服务对象、服务关注（购买/续费/移除）                     |
| 用户管理 | 系统用户、角色基础数据维护                                   |

**核心流程**

```
入住登记：登记客户 → 校验合同日期/身份证 → 选择空闲床位
         → 保存客户 + 床位置"有人" + 新增床位详情（事务）

床位调换：选择空闲床位 → 旧床位详情失效（结束时间=当天）
         → 新增新床位详情 → 旧床位空闲/新床位有人 → 更新客户床位信息

退住审批：管家提交申请 → 管理员审核
         → 正常/死亡退住：床位置"可用"；保留床位：状态不变

外出审批：管家提交申请 → 管理员审核通过：床位置"外出"
         → 老人回院后登记实际回院时间
```

---

## 8 非功能性要求

| 类别       | 要求                                                     |
| ---------- | -------------------------------------------------------- |
| 易用性     | 浏览器在线操作模式，无需安装（本 JavaSE 版为控制台教学实现） |
| 可扩展性   | 后续可拓展用药管理、健康档案、护士站等功能               |
| 安全性     | 逻辑删除保留历史痕迹；密码 PBKDF2-HMAC-SHA256 加盐哈希存储（varchar(255)），登录响应不回传密码；抢床等读-改-写用 `SELECT ... FOR UPDATE` 行锁兜底 |
| 性能       | 多条件组合查询，查询响应时间≤2秒                         |
| 兼容性     | MySQL 8.0 / JDK 21，IDEA / 命令行均可运行                |

---

## 9 相关文档

| 文档                       | 位置                                     |
| -------------------------- | ---------------------------------------- |
| 需求规格说明书             | `project_guide/东软颐养中心-需求规格说明书.pdf` |
| 项目需求清单               | `project_guide/东软颐养中心.xlsx`        |
| 数据字典                   | `project_guide/yyzx数据字典.xls`         |
| 启动指南                   | [startProject.md](startProject.md)         |
| 建表脚本                   | `sql/建表语句.sql`                       |
| 测试数据                   | `sql/data_insert.sql`（插入）、`sql/clean_table.sql`（清空）、`东软颐养中心-测试数据记录.xlsx` |
| 辅助脚本                   | `tools/`（PowerShell 启动/冒烟/压测脚本、`reload_seed.ps1`、`verify_swap.sql`） |
| 分层架构示意图             | `project_guide/三层结构.png`、`project_guide/项目分层结构.png` |
| 业务流程图                 | `project_guide/项目流程图.png`           |
| Mapping 工厂示意图         | `project_guide/工厂的创建.png`           |

---

**文档结束**
