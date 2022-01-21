# SiteMonitor

SiteMonitor is an application to monitor site up status by requesting configured URL paths and verifying that the contents in the response match the required values.

Includes
1. Email Alert Capability
2. Reporting of state changes
3. Response time history reporting
4. Persistent storage and configuration of sites to monitor

### Main Dashboard

<p>
  <img src="https://raw.githubusercontent.com/fuzzbient/sitemonitor/master/doc/screenshot1.jpg" style="width:600px" alt="Screenshot 1"/>
</p>

### Response Graph

<p>
  <img src="https://raw.githubusercontent.com/fuzzbient/sitemonitor/master/doc/screenshot2.jpg" style="width:600px" alt="Screenshot 2"/>
</p>

### Adding a Site

<p>
  <img src="https://raw.githubusercontent.com/fuzzbient/sitemonitor/master/doc/screenshot3.jpg" style="width:600px" alt="Screenshot 3"/>
</p>

## Quickstart

**application.properties** Change the spring.jpa.hibernate.ddl-auto property to create on first run to generate the H2 database needed for persistent storage.

**sitemonitor.security.provider.XTrustProvider** Change the static configuration to limit verify by hostname if desired.

**Directories To Add:** Create a logs directory and a db directory under the application's home directory (where you've placed the jar to run).

Two system properties are expected on startup (mail host and from email address).

```bash
-Dspring.mail.host=localhost 
-Dsitemonitor.mail.from=sitemonitor@foo.bar
```

Example start command.

```bash
nohup java -Xmx1024m -Dspring.mail.host=localhost -Dsitemonitor.mail.from=sitemonitor@foo.bar -jar $JARFile --server.port=8011 > /dev/null 2> ./sitemonitor.err & echo $! > sitemonitor.pid
```
## Try these URLs and REST service endpoints to explore the features.

* **Dashboard** http://localhost:8011/sitemonitor/dashboard
* **Event Listing** http://localhost:8011/sitemonitor/service/events
* **Event Changes** http://localhost:8011/sitemonitor/service/eventchanges
* **Site Listing** http://localhost:8011/sitemonitor/service/sites

## Copyright and license

SiteMonitor is copyright 2022 Paul Horn Enterprises LLC except where noted in included libraries.

Licensed under the **[Apache License, Version 2.0] [license]** (the "License");
you may not use this software except in compliance with the License.

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
