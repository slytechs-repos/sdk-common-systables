# SDK Common System Tables

**High-performance Java library for accessing Linux system tables and monitoring system resources.**

Part of the [jNetworks SDK](https://www.slytechs.com/) - Professional network packet capture and analysis toolkit.

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![Java](https://img.shields.io/badge/Java-24%2B-orange.svg)](https://openjdk.java.net/)

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

- **Pure Java** - No JNI overhead, uses Java NIO for filesystem operations
- **Zero-Copy** - Direct access to `/proc` and `/sys` filesystems
- **High Performance** - Optimized for real-time monitoring and dashboards
- **Type-Safe** - Immutable records with proper value types (MemorySize, DataRate, etc.)
- **Production Ready** - Battle-tested on high-performance packet capture systems

## Requirements

- Java 24 or later
- Linux operating system
- Maven 3.8+ (for building)

## Installation

### Maven

```xml
<dependency>
    <groupId>com.slytechs.sdk</groupId>
    <artifactId>sdk-common-systables</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### Gradle

```gradle
implementation 'com.slytechs.sdk:sdk-common-systables:1.0.0-SNAPSHOT'
```

## Quick Start

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

### Data Sources (Linux)

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

### Performance Characteristics

- **Interface enumeration**: ~1ms for 20 interfaces
- **ARP cache read**: <1ms for 100 entries
- **Route table read**: ~5ms for 50 routes (command execution overhead)
- **CPU stats read**: <1ms for 32 cores
- **Process enumeration**: ~50ms for 500 processes
- **Memory overhead**: <1MB for all tables combined

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

## Testing

The module includes comprehensive JUnit tests for all tables:

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=CpuTableTest

# Run with verbose output
mvn test -X
```

Test coverage includes:

- ✅ Network interface detection (physical vs virtual)
- ✅ ARP cache parsing
- ✅ IPv4 and IPv6 routing
- ✅ DNS resolver configuration
- ✅ Per-core CPU usage calculation
- ✅ Memory usage tracking
- ✅ Process enumeration and filtering

## Use Cases

### Network Packet Capture Systems

- **Interface Discovery** - Find physical NICs for packet capture
- **Link Capacity** - Determine maximum capture rates
- **Virtual Interface Filtering** - Exclude docker/kubernetes interfaces

### System Monitoring Dashboards

- **CPU Monitoring** - Per-core usage graphs
- **Memory Tracking** - System and process memory visualization
- **Process Health** - Monitor critical services (exacapture daemon, suricata, zeek)

### Performance Analysis

- **CPU Affinity** - Verify thread pinning for packet processing
- **Memory Profiling** - Track memory usage of capture processes
- **Network Configuration** - Validate routing and DNS setup

## Platform Support

| Platform  | Status            | Notes                                     |
| --------- | ----------------- | ----------------------------------------- |
| **Linux** | ✅ Full Support    | Primary platform, heavily tested          |
| Windows   | ❌ Not Implemented | Framework ready for future implementation |
| macOS     | ❌ Not Implemented | Framework ready for future implementation |

**Tested on:**

- Ubuntu 24.04 LTS
- Debian 12
- CentOS Stream 9
- Arch Linux

**CPU Architectures:**

- x86_64 (AMD/Intel)
- ARM64 (tested on AWS Graviton)

## Known Limitations

1. **ARP Table** - IPv4 only (via `/proc/net/arp`). For IPv6, would need to parse `ip -6 neigh`.
2. **Route Table** - Requires `ip` command from iproute2 package.
3. **Link Speed** - Requires read access to `/sys/class/net/*/speed` (may need elevated privileges for some interfaces).
4. **Process Information** - Limited by `/proc` permissions (can only read processes owned by current user unless running as root).

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

