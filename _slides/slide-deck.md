---
theme: gaia
_class: lead
paginate: true
backgroundColor: #fff
backgroundImage: url('https://marp.app/assets/hero-background.svg')
---
<style>
section {
  font-size: x-large;
}
</style>

# **Observability <br/> for team leads**

How to use logging and metrics to guide a team
and inform the customer.

&nbsp;

[github.com:pdenooijer/incident-metrics-course.git]()

---

# Introductions

- Piet de Nooijer
- Maarten Veldink

---

# Observability
<!-- Check participants' knowledge, also introduce the elements of the workshop (logging, metrics, etc).-->
What is observability?
What elements can we identify?
What do we use it for?

---

# But first... meet the team!
<!-- Setting the stage, creating a team-lead narrative to refer to during the rest of the workshop. -->
<style scoped>
p {
display: flex;
justify-content: space-around;
align-items: center;
height: 80%; /* Adjust height as needed */
}
p img {
flex-grow: 1;
max-width: 18%;
max-height: 100%;
object-fit: contain;
margin: 0 10px; /* Add some horizontal space between images */
}
</style>

![po-anna.png](img/team/po-anna.png)
![junior-mark.png](img/team/junior-mark.png)
![medior-alex.png](img/team/medior-alex.png)
![senior-arthur.png](img/team/senior-arthur.png)
![tester-nia.png](img/team/tester-nia.png)

---

# Observability from a developers perspective
<!--
Why would you need observability in this role?
- Verify solutions
- Identify problems earlier
- Analyse problems more efficiently
-->
<style scoped>
p {
display: flex;
justify-content: space-around;
align-items: center;
height: 80%; /* Adjust height as needed */
}
p img {
flex-grow: 1;
max-width: 30%;
max-height: 100%;
object-fit: contain;
margin: 0 10px; /* Add some horizontal space between images */
}
</style>

![junior-mark.png](img/team/junior-mark.png)
![medior-alex.png](img/team/medior-alex.png)
![senior-arthur.png](img/team/senior-arthur.png)

---

# Observability from a PO's perspective
<!--
Why would you need observability in this role?
Report to stakeholders, verify SLAs,
make substantiated decisions on technical improvements
-->
<style scoped>
p {
display: flex;
justify-content: space-around;
align-items: center;
height: 80%; /* Adjust height as needed */
}
p img {
flex-grow: 1;
max-width: 30%;
max-height: 100%;
object-fit: contain;
margin: 0 10px; /* Add some horizontal space between images */
}
</style>

![po-anna.png](img/team/po-anna.png)

---

# Observability from a testers perspective
<!-- Why would you need observability in this role?
- Identify issues yourself
- Monitor performance
- Automated verification of non-functionals
- Monitoring of pipelines
- Identify risk/focus-area's
-->
<style scoped>
p {
display: flex;
justify-content: space-around;
align-items: center;
height: 80%; /* Adjust height as needed */
}
p img {
flex-grow: 1;
max-width: 30%;
max-height: 100%;
object-fit: contain;
margin: 0 10px; /* Add some horizontal space between images */
}
</style>

![tester-nia.png](img/team/tester-nia.png)

---

# Observability from a team-lead perspective
<!-- Why would you need observability in this role?
- Monitor team performance
- Identify problems
- Keep an overview of systems
- Verify architectural constraints
-->
<style scoped>
p {
display: flex;
justify-content: space-around;
align-items: center;
height: 40%; /* Adjust height as needed */
}
p img {
flex-grow: 1;
max-width: 18%;
max-height: 100%;
object-fit: contain;
margin: 0 10px; /* Add some horizontal space between images */
}
</style>

![po-anna.png](img/team/po-anna.png)
![junior-mark.png](img/team/junior-mark.png)
![medior-alex.png](img/team/medior-alex.png)
![senior-arthur.png](img/team/senior-arthur.png)
![tester-nia.png](img/team/tester-nia.png)


![teamlead-architect.png](img/team/teamlead-architect.png)

---

# 🔍 SLA, SLO & SLI
<!--
- **SLA** → Agreement → What we **promise externally** (with consequences)
- **SLO** → Objective → What we **aim for** (target)
- **SLI** → Indicator → What we **measure** (data)
-->

- Stand for?
- Meaning?

---

## 📊 SLA, SLO & SLI Recap

| Concept | Stands for              | Meaning                                      | Audience          |
|---------|-------------------------|----------------------------------------------|-------------------|
| **SLA** | Service Level Agreement | Contractual uptime commitment with penalties | Customers & Legal |
| **SLO** | Service Level Objective | Target reliability threshold for SLIs        | DevOps            |
| **SLI** | Service Level Indicator | A metric that represents system performance  | DevOps            |

---

## 📈 Example SLA, SLO, SLI

**Service:** Order API
**SLI:** Successful request rate per minute
**Measured:** `99.2%`

| Layer | Example                         |
|-------|---------------------------------|
| SLI   | % of successful requests        |
| SLO   | 99.9% success rate over a month |
| SLA   | 99.5% monthly uptime            |

---

## 📉 Burn Rate & Error Budget
<!-- Alerting will be further discussed later in the course -->

- If SLO is **99.9%**, then failure allowance = `0.1% errors`
- That **0.1%** is the **error budget**

> If the budget is spent too fast → alert action required.

---

## ⭐ Summary

| Item    | Purpose             | Used for             |
|---------|---------------------|----------------------|
| **SLA** | Contract guarantee  | Business alignment   |
| **SLO** | Target reliability  | Alerting & decisions |
| **SLI** | Measure reliability | Observability        |

---

# Aspects of observability

- Logging
- Metrics
- Lunch
- Alerting
- Tracing
- Reporting

---

# Logging

> If it’s not logged, it didn’t happen.

Why is logging important?

---

# Logging - why logging matters

- Essential for **monitoring**, **debugging**, and **auditing**
- Helps identify issues before users notice them
- Good logs shorten **MTTR** (Mean Time To Recovery).
- Enables better **incident response** and **root cause analysis**

---

# Logging - what makes good logging
## Logging ≠ Print Statements

> Logging is part of your **observability strategy**, not just debugging.

- Logs should explain **why** something happened — not just *what* happened.
- Think in terms of **diagnostics**, **accountability**, and **operational insight**.

🧠 *Design logs as you design APIs: intentional and structured.*

---

# Logging - what makes good logging
## Structure and Context

> Flat text is dead — context-rich, structured logs rule.

- Prefer **structured (JSON)** logging.
- Include context, etc. in every log.
- Use **MDC (Mapped Diagnostic Context)** to propagate metadata.

```java
MDC.put("requestId", requestId);
log.info("Order processed: {}", orderId);
MDC.clear();
```

---

# Tooling

<style scoped>
p {
display: flex;
justify-content: space-around;
align-items: center;
height: 80%; /* Adjust height as needed */
}
p img {
flex-grow: 1;
max-width: 18%;
max-height: 100%;
object-fit: contain;
margin: 0 10px; /* Add some horizontal space between images */
}
</style>

![aws-cloudwatch](img/logging/aws-cloudwatch.webp)
![elasticsearch](img/logging/elasticsearch.png)
![azure-monitor](img/logging/azure-monitor.png)
![logo-loki](img/logging/loki.svg)
![splunk](img/logging/splunk.svg)

---

# Let's dive in!

- Clone repo: github.com:pdenooijer/incident-metrics-course.git
- Follow the instructions in the README

---

# Logging
<!-- 09:30 - 09:40 -->

**Exercise 1: Review the logging in Elastic Search**

- What do you notice?
- Can you determine why you get no responses?

---

# Logging
<!-- 09:30 - 09:50 -->

**Exercise 2: Review  the code in the repository**

- What do you notice?
- Make changes where necessary,
  - change only the logging, not the 'business logic'
- Can you identify the bug from your logging?

---

# Logging
<!-- 09:50 - 10:10 -->

**Exercise 3: Setup some guidelines for your team**

- Define what makes 'proper' logging
- Make rules comprehensible for juniors to seniors
- Discuss in pairs

---
<!-- 10:10 - 10:25 -->

# ☕ Break

---

# Logging
<!-- 10:25 - 10:45 -->

**Lessons learned**

Plenary discussion of guidelines

---

## 1️⃣ Overly Verbose Logging ("Log Spam")

**Symptom:** Thousands of log lines per minute, even under normal load.
**Problem:** Important info is buried, logs grow fast, and analysis becomes painful.

```java
log.debug("Loop iteration {}", i); // In a loop of 10,000 iterations
````

---

## 2️⃣ Insufficient Logging

**Symptom:** Errors happen, but no useful log messages.
**Problem:** Debugging becomes guesswork.

```java
catch (Exception e) {
    // Nothing logged
}
```

✅ Always log meaningful context when catching exceptions.

---

## 3️⃣ Wrong Log Levels

**Symptom:** Everything is logged as ERROR, or real errors at INFO.
**Problem:** Misleading alerts or missed issues.

```java
log.error("Cache miss for key {}", key); // Should be WARN or INFO
```

✅ Use `ERROR` only for failures that affect functionality.

---

## 4️⃣ Logging Sensitive Data

**Symptom:** Passwords, tokens, or PII in logs.
**Problem:** Security and GDPR violations.

```java
log.info("User login with password={}", password);
```

🚫 Never log secrets, credentials, or personal data.

---

## 5️⃣ Vague or Meaningless Messages

**Symptom:** Logs without context.
**Problem:** Impossible to diagnose what actually failed.

```java
log.error("Something went wrong");
```

✅ Include component and context:
`[OrderService] Failed to process order 1234`

---

## 6️⃣ Missing Stacktrace or Exception Details

**Symptom:** Error is logged, but no stacktrace.
**Problem:** Root cause is hidden.

```java
catch (Exception e) {
    log.error("Error processing request: " + e.getMessage());
}
```

✅ Always pass the exception object:
`log.error("Failed to process request", e);`

---

## 7️⃣ Duplicate Logging of the Same Error

**Symptom:** Same exception logged multiple times.
**Problem:** Noise and confusion in logs.

```java
try {
    service.doSomething();
} catch (Exception e) {
    log.error("Error in service", e);
    throw e; // Logged again higher up
}
```

✅ Log once, at the level where it’s actually handled.

---

## 8️⃣ String Concatenation in Log Messages

**Symptom:** Using `+` instead of placeholders.
**Problem:** Strings built even when logging is disabled.

```java
log.debug("Result: " + expensiveComputation());
```

✅ Use placeholders:
`log.debug("Result: {}", result);`

---

## 9️⃣ Inconsistent or Unstructured Logs

**Symptom:** Different formats and missing context.
**Problem:** Hard to parse or search in ELK / Loki.

```java
log.info("Order complete: {}", 12345);
log.info("Processed order id={}", orderId);
```

✅ Define a consistent logging format across the team.

---

## 🔟 No Correlation or Traceability

**Symptom:** Impossible to link logs across services.
**Problem:** Distributed debugging nightmare.

```java
log.info("[traceId={}] Request completed", traceId);
```

✅ Always include a trace or request ID

---

## 🎛️ Log Levels
<style scoped>
table {
   font-size: x-large;
}
</style>

| Level   | Purpose                       | Example                                    |
|---------|-------------------------------|--------------------------------------------|
| `TRACE` | Very detailed, internal state | Method-level entry/exit                    |
| `DEBUG` | Developer-focused info        | Query parameters, object states            |
| `INFO`  | Application events            | Startup, shutdown, key transactions/events |
| `WARN`  | Potential issues              | Fallbacks, retries                         |
| `ERROR` | Failures                      | Exceptions, failed transactions            |
| `FATAL` | Failures                      | Non-recoverable error that cause downtime  |

Experienced developers know: log level inflation kills signal-to-noise ratio.
Keep verbosity low in production — more logs ≠ more visibility.

---

# ✅ Summary

**Good logging is about:**

* Context, not volume
* Correct severity levels
* No sensitive data, use (generated) identifier instead
* Consistent structure
* Traceability across systems

---

# Metrics
<!-- 10:45 - 12:00 -->

- What is the difference between metrics and logs?
- Why are metrics important?

---

# Metrics - storage
<style scoped>
p {
display: flex;
justify-content: space-around;
align-items: center;
height: 80%; /* Adjust height as needed */
}
p img {
flex-grow: 1;
max-width: 18%;
max-height: 100%;
object-fit: contain;
margin: 0 10px; /* Add some horizontal space between images */
}
</style>

![amazon-timestream.png](img/metrics/tsdb/amazon-timestream.png)
![influxdb.svg](img/metrics/tsdb/influxdb.svg)
![mimir.png](img/metrics/tsdb/mimir.png)
![prometheus.png](img/metrics/tsdb/prometheus.png)
![victoria-metrics.png](img/metrics/tsdb/victoria-metrics.png)

---

# Metrics - visualisation
<style scoped>
p {
display: flex;
justify-content: space-around;
align-items: center;
height: 80%; /* Adjust height as needed */
}
p img {
flex-grow: 1;
max-width: 18%;
max-height: 100%;
object-fit: contain;
margin: 0 10px; /* Add some horizontal space between images */
}
</style>

![aws-cloudwatch.webp](img/metrics/visualisation/aws-cloudwatch.webp)
![azure-monitor.png](img/metrics/visualisation/azure-monitor.png)
![grafana.png](img/metrics/visualisation/grafana.png)
![kibana.png](img/metrics/visualisation/kibana.png)

---

<!---
Ask the audience what they know about the key frameworks.
-->
# Metrics - frameworks

Understand the key frameworks for measuring service health and performance.

- RED
- USE
- 4 Golden Signals

---

## 🟥 RED Method

**R**ate – how many requests per second your service handles
**E**rrors – how many requests fail
**D**uration – how long each request takes

### 💡 Example
- **Rate:** 500 checkouts per minute
- **Errors:** 10 payment failures
- **Duration:** 2 seconds per checkout

If duration spikes or errors rise → performance problem.

➡️ Best for **APIs** and **web services**.

---

## 🧮 USE Method

**U**tilization – how busy a resource is (CPU, memory)
**S**aturation – how full or overloaded it is
**E**rrors – hardware or system-level issues

### 💡 Example
- **Utilization:** CPU at 85%
- **Saturation:** Disk write queue growing
- **Errors:** Disk read/write failures

➡️ Best for **infrastructure monitoring** (servers, containers, networks).

---

## 🟢 Four Golden Signals

1. **Latency** – time to handle a request
2. **Traffic** – total demand on the system
3. **Errors** – rate of failed requests
4. **Saturation** – how close the system is to limits

### 💡 Example
Video streaming service:
- **Latency:** Video start time
- **Traffic:** Concurrent viewers
- **Errors:** Failed video loads
- **Saturation:** Bandwidth usage

➡️ Works well for **user-facing systems**.

---

## 🧭 Summary

| Framework            | Focus                                                  | Best For                     |
|----------------------|--------------------------------------------------------|------------------------------|
| **RED**              | Requests (Rate, Errors, Duration)                      | Application-level monitoring |
| **USE**              | Resources (Utilization, Saturation, Errors)            | Infrastructure monitoring    |
| **4 Golden Signals** | User experience (Latency, Traffic, Errors, Saturation) | End-to-end service health    |

---

## 📊 Combined View

A **Grafana dashboard** could combine all three:
- RED for app requests
- USE for server metrics
- Golden Signals for user experience

👉 Together they give a complete picture of system health.

---

## 🧩 Exercise: Monitor the RabbitMQ Application

### 🎯 Goal
Implement the RED, USE, and 4 Golden Signals metrics in the RabbitMQ setup.

### 🪶 Steps
1. **Design a metrics solution**
2. **Implement given solution**
3. **Visualize:**
   - Export metrics with **Prometheus**
   - Create a **Grafana dashboard** showing all three frameworks

💡 *Tip: Use RabbitMQ’s built-in Prometheus exporter for queue metrics!*

---

# Lunch
<!-- 12:00 - 13:00 -->

![lunch.png](img/lunch.png)

---

# 🚨 Alerting
<!-- 13:00 - 14:30 -->

Why does alerting exist?

---

## 🎯 Why Alerting Exists

*"Alerts should notify humans **only** when action is required."*

- Detect issues **before users experience impact**
- Avoid noise and alert fatigue
- Prioritize actionable signals

> 📌 A dashboard is for **observing**.
> 📌 An alert is for **acting**.

---

## ❌ Common Bad Examples

- CPU > 90%
- Disk space < 20%
- Requests > threshold
- "Something turned red"

➡️ You don’t want to wake someone because infrastructure is busy — only if **users are affected**.

---

## 🧭 Types of Alerts

| Type                       | Purpose                   | Example              |
|----------------------------|---------------------------|----------------------|
| 📟 **Paging Alert**        | Immediate action required | Error rate > SLO     |
| 📝 **Ticket Alert**        | Action needed, but later  | Certificate expiring |
| ℹ️ **Informational Alert** | Context only, no action   | Deployment event     |

➡ **Paging alerts should be rare and meaningful.**

---

## 📏 SLO-Based Alerting

Alert based on **user-impact**, not arbitrary numeric thresholds.

- **SLI** → measurable indicator
- **SLO** → target reliability level
- **Alert** → when you are about to violate the SLO

🔧 Alert only when deviations **affect users or reliability objectives.**

---

## 🧪 Bad vs Good Alerting Example

❌ Bad alert:

ALERT: CPU > 90%

✅ Good alert:

⚠️ Checkout API latency 1.2s (SLO: 300ms)
Impact: 18% of customer checkouts failing
Likely cause: queue saturation
Runbook: https://runbook/checkout-latency

---

## 🧠 Alert Design Rules

| Rule         | Meaning                           |
|--------------|-----------------------------------|
| Actionable   | A human needs to do something     |
| Urgent       | It cannot wait                    |
| Owned        | A responsible team exists         |
| Context rich | Includes cause, hint, and runbook |

---

## 📦 Runbooks

Every alert must have:

- What the alert means
- Immediate troubleshooting steps
- Escalation path

🔗 A runbook makes an alert **fixable**, not just noisy.

---

## 📊 Alerting Maturity Model

| Level      | Description                            |
|------------|----------------------------------------|
| 🔴 Level 1 | Everything alerts — chaos mode         |
| 🟠 Level 2 | Resource-threshold monitoring          |
| 🟡 Level 3 | SLO-driven alerting                    |
| 🟢 Level 4 | Predictive alerting + auto-remediation |

---

## 🧹 Alert Hygiene

Alerting requires continuous improvement:

- Quarterly alert review
- Remove unused alerts
- Track alert metrics:
    - False positives
    - Time to acknowledge
    - Action rate

🎯 **Goal:** Improve signal-to-noise ratio.

---

## 🚦 Alert Review Checklist

For each alert:

✔ Does it represent user impact?
✔ Is human action required?
✔ Is there a clear ownership team?
✔ Is the message self-contained and clear?
✔ Is there a runbook?
✔ Can it be tested?

---

## 🧩 Alerting Exercise

📥 Activity in pairs:

1. Make a list of (fictional) alerts from
   - The metrics created in the previous exercise or
   - Your current or previous assignment
2. Categorize them as:
    - Paging
    - Ticket
    - Informational
3. Try to have at least 2 alerts for each category
4. Discuss, improve or remove at least **two** alerts

➡ Goal: **Fewer alerts, better alerts.**

---

## 🔥 Alerting Tools

- Choose tools that match your **scale and maturity**
- Route alerts to the **right teams/persons**
- Automate where possible
- Use machine learning and anomaly detection only after getting the basics right

---

## ⭐ Key Takeaways

**Good alerting is:**

- Calm — few paging alerts
- Relevant — focused on user impact
- Maintained — reviewed and documented
- Measurable — tied to SLOs and incident metrics

> 👌 "**Only alert when humans must act.**"

---

# Tracing

<!-- 14:30 - 15:00 -->
<style scoped>
p {
display: flex;
justify-content: space-around;
align-items: center;
height: 80%; /* Adjust height as needed */
}
p img {
flex-grow: 1;
max-width: 12%;
max-height: 100%;
object-fit: contain;
margin: 0 10px; /* Add some horizontal space between images */
}
</style>

![grafana-tempo.png](img/tracing/grafana-tempo.png)
![jaeger.png](img/tracing/jaeger.png)
![zipkin.svg](img/tracing/zipkin.svg)
![aws-x-ray.png](img/tracing/aws-x-ray.png)
![azure-monitor.png](img/tracing/azure-monitor.png)
![azure-application-insights.png](img/tracing/azure-application-insights.png)

---

# Reporting
## Incident Management Metrics
<!--
-->
<style scoped>
p {
display: flex;
justify-content: space-around;
align-items: center;
height: 80%; /* Adjust height as needed */
}
p img {
flex-grow: 1;
max-width: 30%;
max-height: 100%;
object-fit: contain;
margin: 0 10px; /* Add some horizontal space between images */
}
</style>

![po-anna.png](img/team/po-anna.png)

---
# Incident Management Metrics
## MTTD — Mean Time To Detect

**Definition**
&emsp; Average time between the moment an incident occurs and the moment it is detected.

**Why it matters**
&emsp; The faster you detect an incident, the smaller the impact.

**Source**
&emsp; Monitoring, logging, and alerting systems (e.g., Prometheus, APM).

**Target**
&emsp; < 15 minutes for major incidents (depending on SLA).

---

# Incident Management Metrics
## MTTA — Mean Time To Acknowledge


**Definition**
&emsp; Average time between the reporting of an incident and the first response from an engineer.

**Why it matters**
&emsp; Shows how responsive the support or on-call team is.

**Source**
&emsp; Ticket system, e.g. Jira,

**Target**
&emsp; < 15 minutes

---

# Incident Management Metrics
## MTTR — Mean Time To Resolve

**Definition**
&emsp; Average time from detection to full resolution of an incident.

**Why it matters**
&emsp; Measures the efficiency of recovery processes and team collaboration.

**Source**
&emsp; Ticket system, e.g. Jira,

**Target**
&emsp; < 1 hour for major incidents, < 2 hour for minor incidents

> Reliability is a function of mean time to failure (MTTF) and mean time to repair (MTTR)

---

# Incident Management Metrics
## MTTF — Mean Time To Failure

**Definition**
&emsp; Average time a system or component operates before experiencing a failure.

**Why it matters**
&emsp; Indicates the inherent reliability and stability of systems over time.
&emsp; Higher MTTF = fewer failures and greater operational resilience.

**Source**
&emsp; Monitoring and logging platforms (e.g. CloudWatch, Azure Monitor, Prometheus).
&emsp; Hardware/system lifecycle data.

**Target**
&emsp; Increase over time through reliability engineering and preventive maintenance.

> Reliability is a function of mean time to failure (MTTF) and mean time to repair (MTTR)

---

# Incident Management Metrics
## Incident Recurrence Rate

**Definition**
&emsp; Percentage of incidents that reoccur within a given period.

**Why it matters**
&emsp; Indicator of structural problem-solving effectiveness (or lack thereof).

**Source**
&emsp; Post-mortem, SLA reporting

**Target**
&emsp; < 5% recurring incidents.

---

# Incident Management Metrics
## RCA Completion Rate (Root Cause Analysis)

**Definition**
&emsp; Percentage of incidents for which a root cause is identified and documented within X days.

**Why it matters**
&emsp; Measures discipline in problem management and learning from incidents.

**Target**
&emsp; 100% for major incidents within 5 working days.

---

# Incident Management Metrics
## SLA Compliance Rate

**Definition**
&emsp; Percentage of incidents resolved within the agreed SLA timeframe.

**Why it matters**
&emsp; Shows adherence to commitments made to customers or the business.

**Target**
&emsp; ≥ 95%.

---

# Incident Management Metrics
## Post-Incident Customer Satisfaction (CSAT)

**Definition**
&emsp; Average satisfaction score from users after incident resolution.

**Why it matters**
&emsp; Measures not only speed but also communication quality and customer focus.

**Source**
&emsp; Short survey after the incident.

---

# Incident Management Metrics
## How They Relate

A mental model:

MTTD + MTTA + MTTR
&emsp; measure speed and efficiency of your incident process.

MTTF + Recurrence + RCA Completion
&emsp; measure quality and structural improvement capability.

SLA Compliance + CSAT
&emsp; measure impact and perception from the customer/business perspective.

---

# ❓ Questions?

---
