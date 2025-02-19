# 高效本地缓存系统

## 项目简介
本项目是一个高效的本地缓存系统，使用 Java 语言实现，具备强大的缓存管理能力，支持多种缓存功能，确保高效、可靠的数据存取。

## 核心功能
### 1. **支持多种内存淘汰策略**
- **LRU（Least Recently Used）**：基于双向链表 + 哈希表实现，适用于访问频繁的数据淘汰。
- **LFU（Least Frequently Used）**：基于双哈希表实现，适用于基于访问频率的缓存优化。
- **WTinyLFU**：优化的 LFU 变体，提高缓存命中率。

### 2. **高效的缓存管理与过期数据清理**
- **惰性删除（Lazy Deletion）**：在访问时检查并清理过期数据。
- **周期删除（Periodic Deletion）**：通过后台定时任务清理过期缓存，减少内存占用。
- **多层次时间轮过期策略**：优化定时删除机制，提高缓存管理效率。

### 3. **数据持久化支持**
- **AOF（Append-Only File）**：追加日志文件，记录缓存操作，保证数据安全。
- **RDB（Redis Database）**：定期快照持久化，提高数据恢复能力。
- **AOF 重写机制**：减少磁盘空间占用，优化持久化过程。
- **支持从 AOF 或 RDB 文件加载缓存数据**，提升系统的恢复能力与稳定性。

### 4. **监控与监听机制**
- **慢操作监听器**：监控长时间执行的缓存操作，优化系统性能。
- **删除监听器**：监听缓存删除事件，提供通知或日志支持。
- **自定义监听器**：支持用户自定义监听逻辑，提高系统可扩展性。

### 5. **AOP（面向切面编程）支持**
- 通过 **动态代理** 实现 AOP 切面，确保以下功能的高效执行：
  - 内存淘汰
  - 数据过期
  - AOF 持久化
  - 慢操作监听

## 项目优势
- **高效缓存淘汰**：支持 LRU、LFU、WTinyLFU 算法，适用于不同业务场景。
- **强大的持久化能力**：AOF + RDB 双持久化方案，确保数据安全。
- **灵活的监听机制**：支持自定义监听器，增强系统监控能力。
- **高扩展性**：通过 AOP 实现核心功能解耦，便于扩展和优化。

## 适用场景
- 需要 **高效缓存** 的应用
- 需要 **数据持久化** 的缓存场景，确保数据不丢失。
- 需要 **灵活监控** 和 **自定义监听** 的应用，优化系统性能。
