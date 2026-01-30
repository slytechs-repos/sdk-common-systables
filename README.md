# SDK Common System Tables

**High-performance, cross-platform Java library for accessing system tables and monitoring system resources.**

Part of the [jNetworks SDK](https://www.slytechs.com/) - Professional network packet capture and analysis toolkit.

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![Java](https://img.shields.io/badge/Java-22%2B-orange.svg)](https://openjdk.java.net/) [![Maven Central](https://img.shields.io/badge/Maven%20Central-3.0.0--SNAPSHOT-blue.svg)](https://search.maven.org/artifact/com.slytechs.sdk/sdk-common-systables)

## Features

### Network Monitoring

- **Network Interfaces** - Enumerate interfaces, detect virtual vs physical NICs, link speeds, MTU, MAC addresses
- **ARP Cache** - Read ARP table entries, filter by interface, lookup by IP address
- **Routing Table** - Access IPv4 and IPv6 routes, default gateway detection, per-interface routing
- **DNS Configuration** - Read nameservers, search domains, resolver options

### System Monitoring

- **CPU Monitoring** - Per-core usage statistics, CPU affinity tracking, hardware information
- **Memory Monitoring** - System and per-process memory usage, swap monitoring
- **Process Monitoring** - List all processes, filter by state/user/name, track resource usage

## Key Advantages

- **Cross-Platform Design** - Abstracted API with platform-specific implementations
- **Pure Java** - No JNI overhead, uses Java NIO for filesystem operations where applicable
- **Zero-Copy** - Direct access to system interfaces and data structures
- **High Performance** - Optimized for real-time monitoring and dashboards
- **Type-Safe** - Immutable records with proper value types (MemorySize, DataRate, etc.)
- **Production Ready** - Battle-tested on high-performance packet capture systems

## Requirements

- Java 22 or later
- Supported operating systems:
  - Linux (fully implemented)
  - Windows (planned)
  - macOS (planned)
- Maven 3.8+ (for building)

## Installation

### Maven

```xml
<dependency>
    <groupId>com.slytechs.sdk</groupId>
    <artifactId>sdk-common-systables</artifactId>
    <version>3.0.0-SNAPSHOT</version>
</dependency>
```

### Gradle

```gradle
implementation 'com.slytechs.sdk:sdk-common-systables:3.0.0-SNAPSHOT'
```

**Note:** Version 3.0.0-SNAPSHOT is currently available from Maven Central snapshots repository. The first stable 3.0.0 release will be deployed once all SDK modules are finalized.

## SDK Version Synchronization

All jNetworks SDK modules maintain synchronized version numbers for consistency across deployments:

- **sdk-common** - 3.0.0-SNAPSHOT
- **sdk-common-systables** - 3.0.0-SNAPSHOT (this module)
- **jnetpcap** - 3.0.0-SNAPSHOT
- **jnetworks** - 3.0.0-SNAPSHOT

This ensures that all modules are compatible and tested together as a complete SDK release. The 3.0.0 release represents a major milestone with enhanced system monitoring capabilities, improved performance, and expanded platform support.

## Quick Start

All examples use the same API across platforms. The factory methods (`IfTable.current()`, `CpuTable.current()`, etc.) automatically select the appropriate platform-specific implementation.

### Network Interfaces

```java
import com.slytechs.sdk.common.systables.iface.*;

IfTable ifTable = IfTable.current();

// List all interfaces
ifTable.list().forEach(iface -> {
    System.out.printf("%s: %s, %s%n",
        iface.name(),
        iface.isUp() ? "UP" : "DOWN",
        iface.linkSpeed().orElse(DataRate.ZERO));
});

// Check if interface is virtual (docker, kubernetes, etc.)
boolean isVirtual = IfTable.isVirtual("docker0");

// Get link speed for capacity planning
Optional<DataRate> speed = IfTable.linkSpeed("eth0");
```

### ARP Cache

```java
import com.slytechs.sdk.common.systables.arp.*;

ArpTable arpTable = ArpTable.current();

// List all ARP entries
arpTable.list().forEach(entry -> {
    System.out.printf("%s -> %s (%s) on %s%n",
        entry.ipAddress().getHostAddress(),
        formatMac(entry.hardwareAddress()),
        entry.state(),
        entry.interfaceName());
});

// Lookup specific IP
Optional<ArpEntry> gateway = arpTable.lookup(
    InetAddress.getByName("192.168.1.1")
);
```

### Routing Table

```java
import com.slytechs.sdk.common.systables.route.*;

RouteTable routeTable = RouteTable.current();

// Get default gateway
routeTable.getDefault().ifPresent(route -> {
    System.out.printf("Default gateway: %s via %s%n",
        route.gateway().map(InetAddress::getHostAddress).orElse("direct"),
        route.interfaceName());
});

// List all routes
List<RouteEntry> routes = routeTable.list();

// Separate IPv4 and IPv6
List<RouteEntry> ipv4 = routeTable.listIPv4();
List<RouteEntry> ipv6 = routeTable.listIPv6();
```

### CPU Monitoring

```java
import com.slytechs.sdk.common.systables.cpu.*;

CpuTable cpuTable = CpuTable.current();

// Get CPU information
CpuInfo info = cpuTable.getInfo();
System.out.printf("CPU: %s - %d cores%n", 
    info.model(), info.logicalCores());

// Calculate CPU usage
List<CpuStats> snapshot1 = cpuTable.getStats();
Thread.sleep(1000);
List<CpuStats> snapshot2 = cpuTable.getStats();

// Overall usage
double usage = snapshot2.get(0).usagePercent(snapshot1.get(0));
System.out.printf("CPU Usage: %.1f%%%n", usage);

// Per-core usage
for (int i = 0; i < info.logicalCores(); i++) {
    double coreUsage = snapshot2.get(i + 1)
        .usagePercent(snapshot1.get(i + 1));
    System.out.printf("Core %d: %.1f%%%n", i, coreUsage);
}

// Check CPU affinity
CpuAffinity affinity = cpuTable.getProcessAffinity(pid).orElseThrow();
System.out.println("Allowed CPUs: " + affinity.toCpuList()); // e.g., "0-7,16-23"
```

### Memory Monitoring

```java
import com.slytechs.sdk.common.systables.memory.*;

MemoryTable memTable = MemoryTable.current();

// System memory
MemoryInfo info = memTable.getInfo();
System.out.printf("Memory: %s / %s (%.1f%% used)%n",
    info.used(),
    info.total(),
    info.usagePercent());

// Per-process memory
ProcessMemoryInfo procMem = memTable.getProcessMemory(pid).orElseThrow();
System.out.printf("Process RSS: %s%n", procMem.rss());
```

### Process Monitoring

```java
import com.slytechs.sdk.common.systables.process.*;

ProcessTable procTable = ProcessTable.current();

// List all processes
List<ProcessInfo> processes = procTable.list();
System.out.printf("Total processes: %d%n", processes.size());

// Find by name
List<ProcessInfo> nginx = procTable.listByName("nginx");

// Filter by state
List<ProcessInfo> running = procTable.listByState(ProcessState.RUNNING);

// Get specific process
ProcessInfo proc = procTable.lookup(pid).orElseThrow();
System.out.printf("PID: %d, Name: %s, State: %s%n",
    proc.pid(), proc.name(), proc.state());
System.out.printf("RSS: %s, Threads: %d%n",
    proc.rss(), proc.threads());
```

## API Overview

### Network Tables

| Class        | Description        | Key Methods                                                  |
| ------------ | ------------------ | ------------------------------------------------------------ |
| `IfTable`    | Network interfaces | `list()`, `lookup(name)`, `linkSpeed(name)`, `isVirtual(name)` |
| `ArpTable`   | ARP cache          | `list()`, `lookup(ip)`, `listByInterface(name)`              |
| `RouteTable` | Routing table      | `list()`, `getDefault()`, `listIPv4()`, `listIPv6()`         |
| `DnsTable`   | DNS resolver       | `getConfig()`                                                |

### System Tables

| Class          | Description        | Key Methods                                                  |
| -------------- | ------------------ | ------------------------------------------------------------ |
| `CpuTable`     | CPU monitoring     | `getInfo()`, `getStats()`, `getProcessAffinity(pid)`         |
| `MemoryTable`  | Memory monitoring  | `getInfo()`, `getProcessMemory(pid)`                         |
| `ProcessTable` | Process monitoring | `list()`, `lookup(pid)`, `listByName(name)`, `listByState(state)` |

## Architecture

### Design Principles

- **Platform Abstraction** - Abstract base classes (`IfTable`, `CpuTable`, etc.) with platform-specific implementations
- **Pure Java** - No native code, uses Java NIO to read `/proc` and `/sys` directly
- **Immutable Data** - All data classes are immutable records for thread-safety
- **Value Types** - Proper types for units (`MemorySize`, `DataRate`, `Count`)
- **Optional Returns** - Methods return `Optional<T>` when data may not be available

### Package Structure

```
com.slytechs.sdk.common.systables/
├── iface/          # Network interface table
│   ├── IfTable.java
│   ├── LinuxIfTable.java
│   └── IfDevice.java
├── arp/            # ARP cache table
│   ├── ArpTable.java
│   ├── LinuxArpTable.java
│   └── ArpEntry.java
├── route/          # Routing table
│   ├── RouteTable.java
│   ├── LinuxRouteTable.java
│   └── RouteEntry.java
├── dns/            # DNS configuration
│   ├── DnsTable.java
│   ├── LinuxDnsTable.java
│   └── DnsConfig.java
├── cpu/            # CPU monitoring
│   ├── CpuTable.java
│   ├── LinuxCpuTable.java
│   ├── CpuInfo.java
│   ├── CpuStats.java
│   └── CpuAffinity.java
├── memory/         # Memory monitoring
│   ├── MemoryTable.java
│   ├── LinuxMemoryTable.java
│   ├── MemoryInfo.java
│   └── ProcessMemoryInfo.java
└── process/        # Process monitoring
    ├── ProcessTable.java
    ├── LinuxProcessTable.java
    ├── ProcessInfo.java
    └── ProcessState.java
```

## Implementation Details

### Platform-Specific Data Sources

The module uses platform-appropriate methods to access system information:

**Linux Implementation:**

| Feature            | Data Source                              | Method                              |
| ------------------ | ---------------------------------------- | ----------------------------------- |
| Network Interfaces | `/sys/class/net/`                        | NIO directory listing + sysfs files |
| Link Speed         | `/sys/class/net/{iface}/speed`           | NIO file read                       |
| Virtual Detection  | `/sys/class/net/{iface}/device` symlink  | File existence check                |
| ARP Cache          | `/proc/net/arp`                          | Line parsing                        |
| Routing Table      | `ip route show`                          | Command execution + parsing         |
| DNS Config         | `/etc/resolv.conf`                       | Line parsing                        |
| CPU Info           | `/proc/cpuinfo`                          | Line parsing                        |
| CPU Stats          | `/proc/stat`                             | Line parsing                        |
| CPU Affinity       | `/proc/[pid]/status`                     | Line parsing (Cpus_allowed_list)    |
| Memory Info        | `/proc/meminfo`                          | Line parsing                        |
| Process Memory     | `/proc/[pid]/status`                     | Line parsing                        |
| Process List       | `/proc/[0-9]*`                           | Directory enumeration               |
| Process Info       | `/proc/[pid]/stat`, `/proc/[pid]/status` | Line parsing                        |

**Windows Implementation (Planned):**

- WMI (Windows Management Instrumentation)
- Performance Counters
- Registry access
- netsh commands

**macOS Implementation (Planned):**

- sysctl
- IOKit framework
- BSD kqueue
- netstat/route commands

### Performance Characteristics (Linux)

Typical performance on modern hardware (AMD Ryzen 9 7950X, 128GB RAM):

- **Interface enumeration**: ~1ms for 20 interfaces
- **ARP cache read**: <1ms for 100 entries
- **Route table read**: ~5ms for 50 routes (command execution overhead)
- **CPU stats read**: <1ms for 32 cores
- **Process enumeration**: ~50ms for 500 processes
- **Memory overhead**: <1MB for all tables combined

Performance on other platforms will be measured and documented as implementations are completed.

## Building

```bash
# Clone the repository
git clone https://github.com/slytechs-repos/jnetworks.git
cd jnetworks/sdk-common-systables

# Build with Maven
mvn clean install

# Run tests
mvn test

# Generate Javadoc
mvn javadoc:javadoc
```

## Deployment

This module is published to Maven Central as part of the jNetworks SDK suite.

### Maven Central Coordinates

```xml
<groupId>com.slytechs.sdk</groupId>
<artifactId>sdk-common-systables</artifactId>
<version>3.0.0-SNAPSHOT</version>
```

### Repository Configuration

For snapshot versions, add the Maven Central snapshot repository:

```xml
<repositories>
    <repository>
        <id>maven-central-snapshots</id>
        <url>https://oss.sonatype.org/content/repositories/snapshots</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>
```

Stable releases are available directly from Maven Central without additional repository configuration.

## Testing

The module includes comprehensive JUnit 5 tests for all tables. Current test suite is Linux-based and will be expanded for Windows and macOS as implementations are completed.

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=CpuTableTest

# Run with verbose output
mvn test -X
```

**Test Coverage (Linux):**

- ✅ Network interface detection (physical vs virtual)
- ✅ ARP cache parsing and lookup
- ✅ IPv4 and IPv6 routing tables
- ✅ DNS resolver configuration
- ✅ Per-core CPU usage calculation
- ✅ Memory usage tracking (system and per-process)
- ✅ Process enumeration and filtering

**Test Results:**

- 36 test cases
- ~200ms total execution time
- Tested on systems with 16-32 CPU cores, 64-128GB RAM
- Validated with Docker, Kubernetes, and bare metal configurations

## Use Cases

### Network Packet Capture Systems

- **Interface Discovery** - Find physical NICs for high-speed packet capture
- **Link Capacity Planning** - Determine maximum capture rates based on link speed
- **Virtual Interface Filtering** - Exclude virtual/container interfaces from capture operations
- **Network Configuration Validation** - Verify routing and DNS setup for capture infrastructure

### System Monitoring and Dashboards

- **Real-Time CPU Monitoring** - Display per-core usage with sub-second refresh rates
- **Memory Tracking** - Visualize system and per-process memory consumption
- **Process Health Monitoring** - Track critical services (packet capture daemons, analysis engines)
- **Performance Metrics** - Collect system statistics for capacity planning

### High-Performance Computing

- **CPU Affinity Management** - Verify thread pinning for performance-critical workloads
- **NUMA Awareness** - Monitor CPU and memory topology for optimization
- **Resource Allocation** - Track resource usage across multi-tenant systems
- **Performance Profiling** - Correlate application performance with system metrics

### Infrastructure Management

- **Automated Discovery** - Detect network interfaces and system capabilities
- **Configuration Validation** - Verify system setup meets requirements
- **Health Checks** - Monitor system resources for alerting
- **Capacity Planning** - Collect historical data for growth projections

## Platform Support

| Platform  | Status             | Notes                                          |
| --------- | ------------------ | ---------------------------------------------- |
| **Linux** | ✅ Production Ready | Primary platform, fully implemented and tested |
| Windows   | 🔨 Planned          | Framework ready, implementation pending        |
| macOS     | 🔨 Planned          | Framework ready, implementation pending        |

**Current Implementation (Linux):**

- Ubuntu 20.04+ LTS
- Debian 11+
- RHEL/CentOS 8+
- Fedora 35+
- Arch Linux

**CPU Architectures (Linux):**

- x86_64 (AMD/Intel) - Fully tested
- ARM64 (AArch64) - Tested on AWS Graviton

**Future Platforms:** The module is designed with cross-platform support in mind. Each table has an abstract base class with platform-specific implementations. Windows and macOS implementations will be added in future releases while maintaining API compatibility.

## Known Limitations

### Current Release (Linux Implementation)

1. **ARP Table** - IPv4 only (via `/proc/net/arp`). IPv6 neighbor discovery would require parsing `ip -6 neigh` command output.
2. **Route Table** - Requires `ip` command from iproute2 package to be installed.
3. **Link Speed** - May require elevated privileges to read `/sys/class/net/*/speed` for some interface types.
4. **Process Information** - Limited by `/proc` filesystem permissions. Non-root users can only access their own processes.

### Platform Support

- **Windows** - Not yet implemented. Will use WMI and Performance Counters in future release.
- **macOS** - Not yet implemented. Will use sysctl and IOKit in future release.

All platform implementations will maintain API compatibility, ensuring code portability across operating systems.

## License

Copyright 2005-2026 Sly Technologies Inc.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

```
http://www.apache.org/licenses/LICENSE-2.0
```

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

## Contributing

Contributions are welcome! Please:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Support

- **Documentation**: [https://docs.slytechs.com](https://docs.slytechs.com/)
- **Issues**: [GitHub Issues](https://github.com/slytechs-repos/jnetworks/issues)
- **Email**: support@slytechs.com

## Related Projects

- **jNetPcap** - Libpcap wrapper with protocol packs (L2-L4)
- **jNetworks** - High-performance packet capture (800Gbps with DPDK/Napatech)
- **ExaScale OS** - Exascale packet capture and analysis platform

------

**Built by [Sly Technologies Inc.](https://www.slytechs.com/)** - Tampa, Florida