# district-server-endpoints

[![Build Status](https://travis-ci.org/district0x/district-server-endpoints.svg?branch=master)](https://travis-ci.org/district0x/district-server-endpoints)

Clojurescript-node.js [mount](https://github.com/tolitius/mount) component for a district server, that takes care of HTTP server and its endpoints. This component currently utilises [expressjs](https://expressjs.com/) as an underlying HTTP server.

## Installation
Add `[district0x/district-server-endpoints "1.0.1"]` into your project.clj  
Include `[district.server.endpoints]` in your CLJS file, where you use `mount/start`

**Warning:** district0x components are still in early stages, therefore API can change in a future.

## Real-world example
To see how district server components play together in real-world app, you can take a look at [NameBazaar server folder](https://github.com/district0x/name-bazaar/tree/master/src/name_bazaar/server), 
where this is deployed in production.

## Usage
You can pass following args to endpoints component: 
* `:port` Port at which HTTP server will start
* `:middlewares` Collection of expressjs [middlewares](http://expressjs.com/en/guide/using-middleware.html) you want to install
* `:default-middlewares` This component comes with few default middlewares. Each has an unique keyword key. If you want to use only some of default ones, you can pass collection of their keys here. 


```clojure
(ns my-district
    (:require [mount.core :as mount]
              [district.server.endpoints :refer [reg-get! send query-params]]
              [district.server.endpoints.middleware.logging :refer [logging-middlewares]]))

  (reg-get! "/my-endpoint"
            (fn [req res]
              (if (:hi? (query-params req))
                (send res {:greeting "Hello"})
                (send res 400 "Bad Request"))))

  (-> (mount/with-args
        {:endpoints {:port 6200
                     :middlewares [logging-middlewares]}})
    (mount/start))
```
Note: You need to define your endpoints with `reg-get!`, `reg-post!`, etc. before you start mount. Endpoint definitions can be of course in different file and you just require it in main file.

## Component dependencies
### [district-server-config](https://github.com/district0x/district-server-config)
`district-server-endpoints` gets initial args from config provided by `district-server-config/config` under the key `:endpoints`. These args are then merged together with ones passed to `mount/with-args`.

If you wish to use custom components instead of dependencies above while still using `district-server-endpoints`, you can easily do so by [mount's states swapping](https://github.com/tolitius/mount#swapping-states-with-states).

## district.server.endpoints
This namespace contains following functions for working with expressjs HTTP server:

#### `send [res data]`
#### `send [res status-code data]`
Sends server response. Function chooses format either [transit](https://github.com/cognitect/transit-format) or json automatically, based on request's "Accept" header. 

#### `status [response code]`
Sets status of response.

#### `query-params [req]`
Gets query params of request.

#### `route-params [req]`
Gets route params of request.

#### `body [req]`
Gets body of request.

#### `reg-get! [endpoint handler]`
Registers GET endpoint.

#### `reg-post! [endpoint handler]`
Registers POST endpoint.

#### `reg-put! [endpoint handler]`
Registers PUT endpoint.

#### `reg-delete! [endpoint handler]`
Registers DELETE endpoint.

#### `reg-endpoint! [method endpoint handler]`
Registers endpoint of any method that's supported by expressjs.

## district.server.endpoints.middleware.defaults
Default middlewares that comes with endpoints component are defined here. If you want to use only subset of them, choose from the following and pass it to `:default-middlewares` at mount start.  
* `:middleware/cors` [cors](https://github.com/expressjs/cors) middleware  
* `:middleware/urlencoded` [urlencoded](https://github.com/expressjs/body-parser) body parser  
* `:middleware/text-body-parser-for-transit` passes body for "application/transit+json" as text  
* `:middleware/transit-body-parser` parses transit body  
* `:middleware/query-params-parser` parses query params  

## district.server.endpoints.middleware.logging
Middleware that logs server requests and errors comes with this package, but it's not included in default middleware because it uses [timbre](https://github.com/ptaoussanis/timbre) for logging, which might not be good for everyone. If you do so, best way to set it up is via [district-server-logging](https://github.com/district0x/district-server-logging) mount component.
## Development
```bash
# To start REPL and run tests
lein deps
lein repl
(start-tests!)

# In other terminal
node tests-compiled/run-tests.js

# To run tests without REPL
lein doo node "tests" once
```