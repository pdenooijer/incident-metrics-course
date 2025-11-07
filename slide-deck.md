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

# Schedule

T.b.d. something like:
- Introduction
- Logging
- Metrics theorie
- Lunch
- Metric exercises

---

# Logging - why logging matters

- Essential for **monitoring**, **debugging**, and **auditing**
- Helps identify issues before users notice them
- Enables better **incident response** and **root cause analysis**

> “If it’s not logged, it didn’t happen.”

---

# Logging - what makes good logging

- Essential for **monitoring**, **debugging**, and **auditing**
- Helps identify issues before users notice them
- Enables better **incident response** and **root cause analysis**

> “If it’s not logged, it didn’t happen.”

---

# Setup

- Install podman desktop
- Clone repo: github.com:pdenooijer/incident-metrics-course.git
- Run scripts 01 t/m 04 in project root
- Open http://localhost:30000/quotes.html and start getting quotes
 
---

# Logging 
**Exercise 1: Review the logging in Elastic Search**

- What do you notice? 
- Can you determine why you get no responses? 

---

# Logging 
**Exercise 2: Review  the code in the repository**

- What do you notice? 
- Make changes where necessary
- Can you identify the bug from your logging?   

---

# Logging 
**Exercise 3: Setup some guidelines for your team**

- Define what makes 'proper' logging 
- Make rules comprehensible for juniors to seniors
- Discuss in pairs 

---

# Logging 
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

--- 

# ✅ Summary

**Good logging is about:**

* Context, not volume
* Correct severity levels
* No sensitive data
* Consistent structure
* Traceability across systems
