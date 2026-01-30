#!/bin/bash

# Safe script to display sysfs files - avoids symlink loops

IFACE="${1:-lo}"
NETPATH="/sys/class/net/$IFACE"

# Resolve the symlink once
if [ -L "$NETPATH" ]; then
    REALPATH=$(readlink -f "$NETPATH")
else
    REALPATH="$NETPATH"
fi

if [ ! -d "$REALPATH" ]; then
    echo "Error: Interface $IFACE not found"
    exit 1
fi

echo "====================================="
echo "Interface: $IFACE"
echo "Real Path: $REALPATH"
echo "====================================="
echo

# Read specific known files directly (safe, no recursion)
for file in address mtu operstate speed duplex carrier tx_queue_len type flags; do
    filepath="$REALPATH/$file"
    if [ -f "$filepath" ]; then
        echo "[$file]"
        cat "$filepath" 2>/dev/null || echo "(cannot read)"
        echo
    fi
done

# Statistics subdirectory
echo "=== Statistics ==="
STATS_DIR="$REALPATH/statistics"
if [ -d "$STATS_DIR" ]; then
    for stat in rx_bytes tx_bytes rx_packets tx_packets rx_errors tx_errors rx_dropped tx_dropped; do
        if [ -f "$STATS_DIR/$stat" ]; then
            echo -n "$stat: "
            cat "$STATS_DIR/$stat" 2>/dev/null || echo "(cannot read)"
        fi
    done
fi
echo

# List all available files (no recursion, just this directory)
echo "=== All files in main directory ==="
ls -1 "$REALPATH" | while read name; do
    if [ -f "$REALPATH/$name" ]; then
        echo "  $name"
    fi
done
