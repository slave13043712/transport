# Direct Route Solution
[![build status](https://travis-ci.org/slave13043712/transport.svg?branch=master)](https://travis-ci.org/slave13043712/transport)
[![codecov](https://codecov.io/gh/slave13043712/transport/branch/master/graph/badge.svg)](https://codecov.io/gh/slave13043712/transport)

## Overview

This solution provides two possible ways to check if direct route exists between two given stations. They are implemented in:
- *org.aakimov.transport.api.FileRouteManager*
- *org.aakimov.transport.api.MemoryRouteManager*

**The first one is provided only for demonstration purposes and is not used at runtime.** It is slow and is using a naive approach of data lookup using *Files.lines* routine which is followed by inefficient string parsing.

## Application Workflow

Target route data file is analysed during application start up using super fast MappedByteBuffer approach. Integer values are also parsed without *Scanner/String.split* overhead (see *org.aakimov.transport.api.MappedRouteDataReader* for more details).

All the data is then loaded into memory and all subsequent requests are handled without filesystem I/O.

From the requirements of the task it is clear that it is completely inefficient (in terms of memory usage) to use standard Java collections (object-related overhead will be too high).

Obvious solutions like

- *create a **Map** that matches route to corresponding stops*
- *create a **Map** that matches stop to all related routes and then perform search using this map*
- *create a **Map** that matches stop to all other stops that are connected with the target by a direct route (I bet there are people that try to follow this one ;))*
- *create a **Map*** that...
- etc

**will not** work for large data sets without **significant** increase of the heap size.

Proposed solution has the lowest possible memory footprint and still keeps all the data in memory. Also stop information is sorted to optimize future search requests.

Spring Boot application start time for route data file that contains 100000 routes with 1000 stops per route and 1000000 possible stops (~700Mb) is about 15-20s on my local machine (2xCore 1.7GHz, 4Gb RAM). This amount of data requires about 380Mb of the heap size.

**P.S.** Sample jMeter scenario can be found in *transport-rest/src/test/resources/RouteApiTestPlan.jmx*

## Application Requirements
Application expects route data file to be encoded in UTF-8.

## P.S.
Well... it was a long night... so please forgive me typos if any ;) Also I squashed all commits into one to make it easier to review.

