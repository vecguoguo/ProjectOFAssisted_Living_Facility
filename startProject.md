# 东软颐养中心管理系统 · 启动指南

本系统提供**两种运行形态**，二者共用同一套 Service / DAO / 实体与同一个 MySQL 库：

| 形态 | 入口 | 说明 |
| ---- | ---- | ---- |
| **Web 版**（前后端分离） | 后端 `com.neusoft.yyzx.web.WebApplication` + 前端 Vue3 独立进程 | 浏览器操作，推荐 |
| **控制台版**（教学实现） | `com.neusoft.yyzx.Main` | 命令行菜单交互 |

> 日常开发/演示走 **Web 版**：需要分别启动「后端 API」和「前端页面」两个进程。本指南以 Web 版为主线，最后附控制台版启动方式。

---

## 1 环境要求

| 组件 | 版本 | 说明 |
| ---- | ---- | ---- |
| JDK | 21 | 后端编译与运行 |
| Maven | 3.6+ | 后端构建 |
| MySQL | 8.0 | 数据库（本项目使用 docker 容器 `mysql`） |
| Node.js | 18+（实测 24.x 可用） | 前端构建 |
| npm | 随 Node | 前端包管理 |

Windows PowerShell 下若 `npm` 命令找不到，可临时把 Node 目录加入当前会话 PATH：

```powershell
$env:Path = "D:\205355\biancheng_tool\nvm\nodejs;" + $env:Path
node -v; npm -v
```

---

## 2 数据库准备（一次性）

### 2.1 确保 MySQL 可连

本项目数据库跑在 docker 容器（容器名 `mysql`，宿主机 `localhost:3306`）。启动/检查：

```powershell
docker start mysql
# 确认 3306 已监听
Test-NetConnection -ComputerName localhost -Port 3306
```

### 2.2 连接配置

后端沿用自研 JDBC，读取 `src/main/resources/db.properties`（**不是** Spring 的 datasource）：

```properties
jdbc.driver=com.mysql.cj.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/yyzx?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
jdbc.username=root
jdbc.password=1234

# 连接池（自研，纯 JDK，可选；不写则用下列默认值）
pool.maxSize=32
pool.borrowTimeoutMs=10000
```

> 若你的 MySQL 账号密码不同，改这两行后**重新编译**即可。
>
> 后端已内置自研连接池（`com.neusoft.yyzx.db.ConnectionPool`），`getConnection()` 借出代理连接、用完由 `TxManager` 归还，避免每请求新建物理连接。`pool.maxSize` 为池上限、`pool.borrowTimeoutMs` 为池满时的借出等待毫秒数，均有默认值，一般无需改动。

### 2.3 建库建表 + 灌测试数据

```
sql/建表语句.sql      # 建 yyzx 库 + 18 张表（脚本自带 DROP IF EXISTS）
sql/clean_table.sql   # 清空全部 18 张表（可重复执行）
sql/data_insert.sql   # 联测数据（10 客户 + 5 健康管家，可重复执行）
```

用客户端导入：

```powershell
docker cp sql/建表语句.sql mysql:/tmp/schema.sql
docker cp sql/clean_table.sql mysql:/tmp/clean_table.sql
docker cp sql/data_insert.sql mysql:/tmp/data_insert.sql
docker exec mysql sh -c "mysql -uroot -p1234 --default-character-set=utf8mb4 -e 'source /tmp/schema.sql; source /tmp/clean_table.sql; source /tmp/data_insert.sql'"
```

或在 IDEA / Navicat 里直接执行这三个脚本文件。

---

## 3 启动后端（API，端口 8080）

后端为**纯 JDK `com.sun.net.httpserver.HttpServer` + 自研极简 MVC（不依赖 Spring）**，入口 `WebApplication.main`，监听 8080（固定 16 线程池，仅注册 `/api` 一个上下文）。

### 方式 A：IntelliJ IDEA（推荐）

打开 `src/main/java/com/neusoft/yyzx/web/WebApplication.java` → 点击类旁的绿色运行按钮。
控制台出现 `东软颐养中心 Web 服务（纯 JDK HttpServer）已启动，监听端口 8080` 即成功。

### 方式 B：命令行

后端无 Spring，`pom.xml` 也**没有** `spring-boot-maven-plugin`（故不能用 `mvn spring-boot:run`）；改用 classpath 直跑：

```powershell
cd D:\Desktop\文件\java_project\ddsy\ddsy
mvn -q clean compile
mvn -q dependency:build-classpath "-Dmdep.outputFile=target/cp.txt"
$cp = (Get-Content target/cp.txt -Raw).Trim()
java "-Dfile.encoding=UTF-8" -cp "target/classes;$cp" com.neusoft.yyzx.web.WebApplication
```

### 后端接口约定

- 所有接口前缀 `/api`，例如登录 `POST /api/auth/login`。
- 除登录外，`/api/**` 都需在请求头携带 `token` 字段（由 `WebApplication.handle` 统一拦截并经 `TokenManager` 校验，未登录返回业务码 `NOT_LOGIN`）。
- 密码采用 PBKDF2-HMAC-SHA256 加盐哈希存储（`com.neusoft.yyzx.common.utils.PasswordUtil`）；历史明文账号在**首次登录成功时会自动透明升级为哈希**，故下表凭据照常使用。
- 已开放 CORS（开发期允许任意来源），前端也可通过 Vite 代理直连。

---

## 4 启动前端（页面，端口 5173）

前端为 Vue3 + Vite + Element Plus，独立目录 `frontend/`。

```powershell
cd D:\Desktop\文件\java_project\ddsy\ddsy\frontend
npm run dev
```

看到 `Local: http://localhost:5173/` 即成功。

> **报错 `‘vite’ 不是内部或外部命令`**：说明还没 `npm install`，本地缺 `node_modules\.bin\vite`。先装依赖再 `npm run dev`。
> `.npmrc` 已指向国内镜像 `https://registry.npmmirror.com`；若安装卡住：
> ```powershell
> npm install --registry=https://registry.npmmirror.com --no-audit --no-fund
> ```

Vite 已配置代理：前端请求 `/api/xxx` 会转发到 `http://localhost:8080/api/xxx`，无需关心跨域。**所以务必先起后端再起前端。**

---

## 5 访问与测试账号

浏览器打开 <http://localhost:5173>，用以下账号登录（对应 `data_insert.sql`）：

| 角色 | 账号 | 密码 | 姓名 | 备注 |
| ---- | ---- | ---- | ---- | ---- |
| 系统管理员 | `admin` | `admin123` | 赵敏 | 全部管理功能 |
| 健康管家 | `guanjia01` | `345678` | 陈静 | 名下有张伟、李秀兰 |
| 健康管家 | `guanjia02` | `654321` | 刘强 | 名下有王德海、赵凤英 |
| 健康管家 | `guanjia03` | `112222` | 孙莉 | 名下有周淑芬、吴桂英 |
| 健康管家 | `guanjia04` | `223344` | 王芳 | 名下有郑国安、冯玉珍 |
| 健康管家 | `guanjia05` | `445566` | 李国栋 | 名下无客户（测空态分支）|

> **关于密码存储**：`data_insert.sql` 里的种子密码仍是明文，但后端已改用 PBKDF2-HMAC-SHA256（`PasswordUtil`）存储与校验。老账号在**首次登录成功时会被「透明升级」**——数据库中的 `password` 字段自动改写为 `pbkdf2$迭代数$盐$哈希`，此后登录一律走哈希比对；因此上表的明文密码依旧可直接登录，无需重灌数据。若你手动往库里写入哈希值，或想验证升级结果，登录一次后查 `user` 表即可看到字段已变。登录响应体会把 `password` 置空，不外泄。

> 凭据与 `data_insert.sql` 一致。库中 `password` 现为明文，任一账号首次登录成功后会被自动改写为 `pbkdf2$...` 哈希，后续登录走哈希校验，凭据（密码本身）不变。

---

## 6 启动顺序小结

```
1. docker start mysql           # 数据库
2. （首次）导入 建表语句.sql + clean_table.sql + data_insert.sql
3. 启动后端 WebApplication       # 8080
4. cd frontend; npm install; npm run dev   # 5173
5. 浏览器访问 http://localhost:5173
```

停止：前端终端 `Ctrl+C`；后端在 IDEA 点停止 / 命令行 `Ctrl+C`；数据库 `docker stop mysql`。

---

## 7 控制台版启动（可选）

不依赖 Web，直接跑菜单程序：

```powershell
cd D:\Desktop\文件\java_project\ddsy\ddsy
mvn -q clean compile
mvn -q dependency:build-classpath "-Dmdep.outputFile=target/cp.txt"
$cp = (Get-Content target/cp.txt -Raw).Trim()
java "-Dfile.encoding=UTF-8" -cp "target/classes;$cp" com.neusoft.yyzx.Main
```

或在 IDEA 运行 `src/main/java/com/neusoft/yyzx/Main.java`。账号同上。

---

## 8 前端生产构建（Web 版，可选）

后端是纯 JDK `HttpServer`，**只注册了 `/api` 一个上下文，不托管任何静态资源**，因此不存在「把 `dist/` 拷进后端、单进程部署」的做法。前端产物需由独立静态服务器提供，仍与后端两进程并存。

```powershell
cd D:\Desktop\文件\java_project\ddsy\ddsy\frontend
npm run build      # 产物在 frontend/dist/
npm run preview    # 本地预览打包结果（仅验证产物，默认不启代理）
```

> - ⚠️ `vite.config.js` 只配了 `server.proxy`，**仅对 `npm run dev` 生效**；`preview` 读的是 `preview.proxy`（未配），故预览页直接请求 `/api` 会 404。需要预览接口联调时，在 `vite.config.js` 补上 `preview.proxy`（内容同 `server.proxy`），或直接用 `npm run dev`。
> - 正式部署请把 `dist/` 交给 Nginx 等静态服务器，并将 `/api` 反向代理到后端 8080。
> - 后端 `pom.xml` 未引入 `spring-boot-maven-plugin`，`mvn package` 打的是**不含依赖、无 `Main-Class` 清单**的普通 thin jar，`java -jar` 无法直接运行。后端启动请沿用第 3 节的 classpath 直跑方式。

---

## 9 常见问题排查

| 现象 | 原因 | 处理 |
| ---- | ---- | ---- |
| 登录报数据库连接失败 | MySQL 未启动 / 密码不符 | `docker start mysql`；核对 `db.properties` 的 `jdbc.password` |
| 前端 `'vite' 不是内部或外部命令` | 未装依赖 | `frontend/` 下 `npm install` |
| 前端页面能开但接口全 404/超时 | 后端没起或端口不是 8080 | 先启动 `WebApplication`；确认 8080 空闲 |
| 接口返回未登录/无权限 | 未带 token，或 token 失效（当前 Token 存内存，后端重启即失效）| 重新登录；前端拿到 `NOT_LOGIN` 业务码时会跳回登录页 |
| 端口被占用（8080 / 5173） | 上一进程未退出 | `netstat -ano | findstr :8080` 找 PID 后结束 |
| 中文乱码 | 编码未设 UTF-8 | 启动加 `-Dfile.encoding=UTF-8`；库用 utf8mb4 |
