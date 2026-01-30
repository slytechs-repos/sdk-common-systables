#!/bin/bash

IFACE="${1:-lo}"
NETPATH="/sys/class/net/$IFACE"

echo "Checking: $NETPATH"
echo

# First, just list everything
echo "=== ls -la ==="
ls -la "$NETPATH"
echo

# Now try to read some common files directly
echo "=== Direct reads ==="
for file in address mtu operstate speed tx_queue_len type flags; do
    filepath="$NETPATH/$file"
    if [ -f "$filepath" ]; then
        echo -n "$file: "
        cat "$filepath" 2>/dev/null || echo "(cannot read)"
    else
        echo "$file: (not found)"
    fi
done
echo

# Show directory contents recursively
echo "=== Tree view ==="
find "$NETPATH" -maxdepth 2 2>/dev/null | head -50
