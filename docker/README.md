# 大麦本地中间件

按[项目安装文档](https://javaup.chat/damai/getting-started/install-dependencies/)配置，使用 Docker Desktop 的 Linux 容器。
在项目根目录执行：

```powershell
docker compose --parallel 1 pull --ignore-buildable
docker compose build elasticsearch
docker compose up -d --wait --wait-timeout 300
docker compose ps
```

| 服务 | 版本 | 本机连接地址 | 用户名 / 密码 |
| --- | --- | --- | --- |
| MySQL | 5.7.44 | 127.0.0.1:3306 | root / root |
| Nacos | 2.3.0 | http://localhost:8848/nacos | root / root |
| Redis | 6.0.8 | 127.0.0.1:6379 | root / root；也支持只输入密码 root |
| ZooKeeper | 3.8.4 | 127.0.0.1:2181 | 文档默认模式，无账号认证 |
| Kafka | 2.8.1 | 127.0.0.1:9092 | 文档默认 PLAINTEXT 模式，无账号认证 |
| Elasticsearch + IK | 8.5.2 | http://localhost:9200 | root / root |
| Kibana | 8.5.2 | http://localhost:5601 | root / root |
| Logstash | 8.5.2 | 127.0.0.1:5047，TCP JSON 日志输入 | 接收端无登录，连接 ES 使用 root / root |

所有宿主机端口只绑定 127.0.0.1。这套 root 凭据用于本地开发。

## 数据与账号初始化

- MySQL 数据卷首次为空时，自动创建 Nacos 数据库，并依次执行 `sql/cloud/*.sql`，创建 10 个业务库及示例数据。已有数据卷不会重复导入。
- `docker/mysql/0_nacos.sql` 来自 Nacos 2.3.0 官方 MySQL schema，初始管理员调整为 `root/root`。Nacos 使用 MySQL 持久化。
- 节目 SQL 的 `important_notice` 已改为 `text`，以完整保存示例数据中超过 100 字符的通知，保留 MySQL 严格模式。
- Elasticsearch 使用官方支持的 file realm，账号定义在 `docker/elasticsearch/users` 和 `users_roles`，密码以 bcrypt 哈希保存。这样支持指定的四位密码 `root`；常规创建用户 API 要求至少六位。此用户无法通过 Kibana 用户管理页面修改。
- 本地 Elasticsearch 的 root 同时具有 `superuser` 和 `kibana_system` 角色，供项目、Kibana 后台和 Logstash 连接使用。
- Redis 配置 root ACL 用户，并为 default 用户设置相同密码，以兼容项目中只传密码的 Redisson 客户端。
- 服务 `application.yml` 中的 Nacos、Redis 和 Elasticsearch 凭据已同步；本地 JDBC 原有配置已是 root/root。
- 项目的 Logstash Socket appender 默认被注释；中间件安装不会自动启用应用日志发送。
- 本次拉取的 Kibana 8.5.2 镜像中 `/bin/tini` 为空文件，因此 Compose 使用 `init: true` 和原始 `kibana-docker` 启动脚本，由 Docker 自带 init 接管进程管理。

## 常用命令

```powershell
# 查看状态和日志
docker compose ps
docker compose logs --tail 100 nacos

# 停止，保留数据
docker compose stop

# 重新启动
docker compose up -d

# Redis 认证检查
docker compose exec -e REDISCLI_AUTH=root redis redis-cli --user root ping

# Elasticsearch 和 IK 检查
curl.exe -u root:root http://localhost:9200
docker compose exec elasticsearch bin/elasticsearch-plugin list
```

数据保存在 `damai_` 前缀的 Docker 命名卷中。`docker compose down` 保留数据；不要使用 `down -v`，除非确定要删除本项目中间件数据。
