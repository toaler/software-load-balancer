FROM haproxy:1.7.2

COPY haproxy.cfg .

EXPOSE 80

ENTRYPOINT haproxy -f haproxy.cfg

