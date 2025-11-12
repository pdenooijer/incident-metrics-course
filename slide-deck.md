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

# **Application monitoring <br/> for team leads**

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
<!-- Check participants knowledge, also introduce the elements of the workshop (logging, metrics, etc).--> 
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

![po-anna.png](img/po-anna.png)
![junior-mark.png](img/junior-mark.png)
![medior-alex.png](img/medior-alex.png)
![senior-arthur.png](img/senior-arthur.png)
![tester-nia.png](img/tester-nia.png)  

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

![po-anna.png](img/po-anna.png)

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

![junior-mark.png](img/junior-mark.png)
![medior-alex.png](img/medior-alex.png)
![senior-arthur.png](img/senior-arthur.png)

---

# Observability from a testers perspective
<!-- Why would you need observability in this role? 
- Identify issues yourself
- Monitor performance
- Automated verification of non-functionals
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

![tester-nia.png](img/tester-nia.png)

---

# Logging - why logging matters

- Essential for **monitoring**, **debugging**, and **auditing**
- Helps identify issues before users notice them
- Enables better **incident response** and **root cause analysis**

> If it’s not logged, it didn’t happen.

---

# Logging - what makes good logging
## Logging ≠ Print Statements

> Logging is part of your **observability strategy**, not just debugging.

- Logs should explain **why** something happened — not just *what* happened.
- Think in terms of **diagnostics**, **accountability**, and **operational insight**.
- Good logs shorten **MTTR** (Mean Time To Recovery).

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

# Let's dive in!

- Install podman desktop
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
- Make changes where necessary
- Can you identify the bug from your logging?   

---

# Logging 
<!-- 09:50 - 10:10 --> 

**Exercise 3: Setup some guidelines for your team**

- Define what makes 'proper' logging 
- Make rules comprehensible for juniors to seniors
- Discuss in pairs 

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

| Level   | Purpose | Example                                   |
|---------|----------|-------------------------------------------|
| `TRACE` | Very detailed, internal state | Method-level entry/exit                   |
| `DEBUG` | Developer-focused info | Query parameters, object states           |
| `INFO`  | Application events | Startup, shutdown, key transactions       |
| `WARN`  | Potential issues | Fallbacks, retries                        |
| `ERROR` | Failures | Exceptions, failed transactions           |
| `FATAL` | Failures | Non-recoverable error that cause downtime |

Experienced developers know: log level inflation kills signal-to-noise ratio.
Keep verbosity low in production — more logs ≠ more visibility.

--- 

# ✅ Summary

**Good logging is about:**

* Context, not volume
* Correct severity levels
* No sensitive data
* Consistent structure
* Traceability across systems

--- 

# Metrics
<!-- 10:45 - 12:00 -->

- Wat meten we?
  - Platform monitoring vs application monitoring
  - RED / USE / 4 golden signals
   
- Welke tools zijn er? 
  - Grafana/Prometheus
  - Elastic APM
  - Azure Application Insights 
  - AWS X-Ray


--- 

# Lunch
<!-- 10:45 - 12:00 -->




--- 

# Alerting
<!-- 13:00 - 14:30 -->



--- 

# Tracing
<!-- 14:30 - 15:00 -->



