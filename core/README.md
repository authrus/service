![Authrus API Gateway](https://github.com/authrus/gateway/raw/master/authrus-gateway/src/main/resources/static/logo.png)

Authrus API Gateway is a high performance event driven proxy for HTTP and WebSockets written in Java.
Configuration of the API Gateway can be done declaratively with a JSON based configuration file. 
The configured API routes are matched in order of declaration using standard regular expressions to match and re-write content. 

A variety of load balancing strategies can be leveraged, from sticky routes to dynamic target selection to equalize loads
across a collection of target servers.

Production grade implementation deployed in high load sites managing API and user traffic where a single 
instance must handle several billion requests each week.

#### Features

* Supports up to 1 million concurrent connections
* Event driven architecture based on the proactor pattern
* Declarative JSON based API configuration
* Supports Two-Way SSL challenges
* Streaming support
* Selective content caching
* Load balancing strategies

#### Example Configuration

```json
{
  "properties": {
    "root": "${gateway.resources}",
    "creds": "${gateway.credentials}"
  },
  "hosts": [
    {
      "name": "example.com",
      "port": 443,
      "directory": "${root}",   
      "access-log": {
        "path": "log/example.com.log",
        "format": "%T [%P] %h [%t] '%r' %s %b %{Host} %{Content-Type} %X @{X-Time} ms %d ms",
        "threshold": 1000000
      },
      "key-store": {
        "type": "PKCS12",
        "path": "${creds}/example.com/example.com.pfx",
        "password": "*********",
        "protocols": [],
        "cipher-suites": [
          "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256",
          "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256",
          "TLS_RSA_WITH_AES_128_CBC_SHA256",
          "TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256",
          "TLS_ECDH_RSA_WITH_AES_128_CBC_SHA256",
          "TLS_DHE_DSS_WITH_AES_128_CBC_SHA256",
          "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA",
          "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA",
          "TLS_RSA_WITH_AES_128_CBC_SHA",
          "TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA",
          "TLS_ECDH_RSA_WITH_AES_128_CBC_SHA",
          "TLS_DHE_DSS_WITH_AES_128_CBC_SHA",
          "TLS_ECDHE_ECDSA_WITH_RC4_128_SHA",
          "TLS_ECDHE_RSA_WITH_RC4_128_SHA",
          "SSL_RSA_WITH_RC4_128_SHA",
          "TLS_ECDH_ECDSA_WITH_RC4_128_SHA",
          "TLS_ECDH_RSA_WITH_RC4_128_SHA",
          "TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA",
          "TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA",
          "SSL_RSA_WITH_3DES_EDE_CBC_SHA",
          "TLS_ECDH_ECDSA_WITH_3DES_EDE_CBC_SHA",
          "TLS_ECDH_RSA_WITH_3DES_EDE_CBC_SHA",
          "SSL_DHE_DSS_WITH_3DES_EDE_CBC_SHA",
          "SSL_RSA_WITH_RC4_128_MD5",
          "TLS_EMPTY_RENEGOTIATION_INFO_SCSV"
        ]
      },
      "redirects": [
        {
          "match-pattern": "http://example.com/(.*)",
          "ignore-pattern": "http://example.com/status",
          "template": "https://example.com/%{1}"
		}
      ],
      "routes": [
        {
          "route-patterns": [
            "http.*://example.com/.*"
          ],
          "route-rules": [
            {
              "pattern": "/",
              "template": "/index.html"
            },          
            {
              "pattern": "/index",
              "template": "/index.html"
            },
            {
              "pattern": "/about",
              "template": "/about.html"
            }                         
          ],
          "cache-duration": 60000,
          "server-group": {
            "buffer": 8192,
            "selector": "master-slave",
            "health-check": {
              "ping-path": "/v1/ping",
              "ping-frequency": 5000
            },
            "servers": [
              "http://target.server1:7072/",
              "http://target.server2:7072/"
            ]
          }
        }
      ]
    } 
  ]
}
 
```
