global
    stats timeout 30s

defaults
    log     global
    mode    http
    option  httplog
    option  dontlognull
    timeout connect 5000
    timeout client  50000
    timeout server  50000

frontend localnodes
    bind *:80
    mode http
    default_backend nodes

backend nodes
    mode http
    balance roundrobin
    option forwardfor
    http-request set-header X-Forwarded-Port %[dst_port]
    option httpchk HEAD / HTTP/1.1\r\nHost:localhost
    <servers:{ server | server <server.name>-<server.id> <server.address>:<server.port> check<\n>}>