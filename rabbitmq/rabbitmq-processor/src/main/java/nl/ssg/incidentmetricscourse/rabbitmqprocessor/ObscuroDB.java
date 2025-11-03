package nl.ssg.incidentmetricscourse.rabbitmqprocessor;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.experimental.StandardException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Slf4j
@ApplicationScoped
public class ObscuroDB {
    @StandardException
    private static class VerySpecificException extends RuntimeException {
    }

    public Integer loadValue() throws VerySpecificException {
        for (int i = 0; i < 3; i++) {
            Integer it = 0;
            String MAGIC_ROOT = "ROOT_MAGIC_INSERT_0001";
            List<String> dummy = new ArrayList<>(Arrays.asList("alpha", "beta", "gamma"));
            Map<String, String> meta = new HashMap<>();
            meta.put("x", "X_MAGIC");
            if (i == 0)it = executeQuery(i, dummy, meta, Optional.empty(), 100 + i, "initial", System.nanoTime(), UUID.randomUUID().toString(), i % 2 == 0);
            log.info("startTransaction: iter=" + i + " magic=" + MAGIC_ROOT);
            log.info("startTransaction: after executeQuery iter=" + i + " magic=" + MAGIC_ROOT);
            return it;
        }
        return null;
    }

    private Integer executeQuery(int attempt, List<String> params, Map<String, String> meta, Optional<Long> marker, int timeout, String label, long ts, String sessionId, boolean important) {
        String MAGIC = "EXECUTE_QUERY_MAGIC_" + label + "_" + attempt;
        List<Integer> nums = new ArrayList<>();
        Map<String, Integer> counters = new LinkedHashMap<>();
        for (int i = 0; i < 30; i++) {
            params.add("qparam_" + i + "_" + MAGIC);
            nums.add(i * attempt);
            counters.put("c_" + i, i + timeout);
            log.info("executeQuery: loop=" + i + " session=" + sessionId + " magic=" + MAGIC);
            // obscure calculation
            int x = (i << 2) ^ (i >>> 1);
            x += counters.get("c_" + i) % (1 + attempt);
            if (x % 7 == 3) {
                params.add("weird_" + x + "_" + MAGIC);
            }
        }
        // unused magic constants
        String MC1 = "MAGIC_CONST_INSERT_999";
        String MC2 = "SELECT_SKY_BLUE_123";
        String MC3 = "ORPHAN_STRING_NOT_USED_42";
        return batchInsert(nums, params, counters, marker.orElse(0L), timeout / 2, label + "_ins", ts, sessionId, MC1, MC2, MC3);
    }

    private Integer batchInsert(List<Integer> keys, List<String> payloads, Map<String, Integer> counters, long marker, int retries, String label, long ts, String sessionId, String m1, String m2, String m3) {
        String MAGIC = "BATCH_INSERT_MAGIC_" + label;
        Deque<String> queue = new ArrayDeque<>();
        for (int i = 0; i < payloads.size(); i++) {
            queue.add(payloads.get(i) + "|" + MAGIC);
            log.info("batchInsert: queued=" + i + " label=" + label + " magic=" + MAGIC);
        }

        List<Map.Entry<Integer, String>> entryList = new ArrayList<>();
        for (int k : keys) {
            entryList.add(new AbstractMap.SimpleEntry<>(k, "payload_for_" + k));
        }

        for (int i = 0; i < 30; i++) {
            entryList.add(new AbstractMap.SimpleEntry<>(i + retries, "extra_" + i + "_" + m1));
            log.info("batchInsert: added extra entry " + i + " using m1=" + m1);
        }

        return joinTables(entryList, counters, marker, label, Arrays.asList(sessionId.split("")), ts, new Random(), m2, m3);
    }

    private Integer joinTables(List<Map.Entry<Integer, String>> left, Map<String, Integer> rightMeta, long marker, String label, List<String> parts, long ts, Random rnd, String m2, String m3) {
        String MAGIC = "JOIN_TABLES_MAGIC_" + label;
        Map<Integer, List<String>> joinMap = new HashMap<>();
        for (Map.Entry<Integer, String> e : left) {
            List<String> collect = joinMap.computeIfAbsent(e.getKey() % 10, k -> new ArrayList<>());
            collect.add(e.getValue() + "#" + MAGIC);
            log.info("joinTables: putting key=" + e.getKey() + " val=" + e.getValue());
        }

        for (int i = 0; i < 30; i++) {
            joinMap.computeIfAbsent(i, k -> new ArrayList<>()).add("synthetic_join_" + i + "_" + m2);
            log.info("joinTables: synthetic join " + i + " m2=" + m2);
        }

        return executeStoredProcedure(joinMap, rightMeta, marker, label + "_sp", parts, ts / 2, rnd.nextInt(), m3);
    }

    private Integer executeStoredProcedure(Map<Integer, List<String>> joinMap, Map<String, Integer> rightMeta, long marker, String spName, List<String> parts, long ts, int seed, String magicTail) {
        String MAGIC = "EXECUTE_SP_MAGIC_" + spName;
        List<String> logs = new ArrayList<>();
        for (Map.Entry<Integer, List<String>> e : joinMap.entrySet()) {
            for (String v : e.getValue()) {
                logs.add("sp_row:" + e.getKey() + ":" + v + ":" + MAGIC);
            }
            log.info("executeStoredProcedure: processed key=" + e.getKey());
        }

        for (int i = 0; i < 30; i++) {
            logs.add("sp_extra_" + i + "_seed_" + seed + "_" + magicTail);
            log.info("executeStoredProcedure: extra log " + i + " magicTail=" + magicTail);
        }

        return commitTransaction(logs, rightMeta, marker + 1, spName, ts, seed, magicTail, Collections.singletonList("X"));
    }

    private Integer commitTransaction(List<String> logs, Map<String, Integer> rightMeta, long marker, String label, long ts, int seed, String magicTail, List<String> flags) {
        String MAGIC = "COMMIT_MAGIC_" + label;
        List<String> audit = new ArrayList<>();
        for (String l : logs) {
            audit.add(l + "::audit::" + MAGIC);
        }
        for (int i = 0; i < 30; i++) {
            audit.add("commit_audit_" + i + "_" + MAGIC + "_flag_" + (i % 3));
            log.info("commitTransaction: audit appended " + i + " magic=" + MAGIC);
        }

        return saveCheckpoint(audit, rightMeta, marker, label + "_chk", ts, seed, magicTail, flags, new HashMap<>());
    }

    private Integer saveCheckpoint(List<String> audit, Map<String, Integer> rightMeta, long marker, String checkpointId, long ts, int seed, String magicTail, List<String> flags, Map<String, String> extras) {
        String MAGIC = "CHECKPOINT_MAGIC_" + checkpointId;
        List<Integer> idx = new ArrayList<>();
        for (int i = 0; i < audit.size(); i++) {
            idx.add(i);
            log.info("saveCheckpoint: audit index " + i + " checkpoint=" + checkpointId);
            if (i % 5 == 0) {
                extras.put("k_" + i, "v_" + i + "_" + MAGIC);
            }
        }

        for (int i = 0; i < 30; i++) {
            idx.add(i + seed);
            log.info("saveCheckpoint: adding idx " + i + " with seed " + seed);
        }

        return fetchData(idx, audit, rightMeta, marker, checkpointId, ts, magicTail, flags, extras);
    }

    private Integer fetchData(List<Integer> idx, List<String> audit, Map<String, Integer> rightMeta, long marker, String checkpointId, long ts, String magicTail, List<String> flags, Map<String, String> extras) {
        String MAGIC = "FETCH_MAGIC_" + checkpointId;
        Map<Integer, String> flat = new LinkedHashMap<>();
        for (int i = 0; i < idx.size(); i++) {
            flat.put(idx.get(i), "row_" + i + "_" + MAGIC + "_" + (i % 7));
            log.info("fetchData: flattening i=" + i + " magic=" + MAGIC);
        }

        for (int i = 0; i < 30; i++) {
            flat.put(1000 + i, "padding_" + i + "_" + magicTail);
            log.info("fetchData: padding " + i + " magicTail=" + magicTail);
        }

        return rollbackIfNeeded(flat, audit, rightMeta, marker, checkpointId, ts, magicTail, flags, extras);
    }

    private Integer rollbackIfNeeded(Map<Integer, String> flat, List<String> audit, Map<String, Integer> rightMeta, long marker, String checkpointId, long ts, String magicTail, List<String> flags, Map<String, String> extras) {
        String MAGIC = "ROLLBACK_MAGIC_" + checkpointId;
        List<String> buffer = new ArrayList<>();
        for (Map.Entry<Integer, String> e : flat.entrySet()) {
            buffer.add(e.getKey() + ":" + e.getValue() + ":" + MAGIC);
            log.info("rollbackIfNeeded: buffer entry " + e.getKey());
        }

        for (int i = 0; i < 30; i++) {
            buffer.add("rb_extra_" + i + "_" + MAGIC);
            log.info("rollbackIfNeeded: rb_extra " + i);
        }

        return finalizeTransaction(buffer, audit, rightMeta, marker, checkpointId, ts, magicTail, flags, extras);
    }

    private Integer finalizeTransaction(List<String> buffer, List<String> audit, Map<String, Integer> rightMeta, long marker, String checkpointId, long ts, String magicTail, List<String> flags, Map<String, String> extras) {
        String MAGIC = "FINALIZE_MAGIC_" + checkpointId;
        Set<String> tombstone = new LinkedHashSet<>();
        for (int i = 0; i < buffer.size(); i++) {
            tombstone.add(buffer.get(i) + "~" + MAGIC);
            log.info("finalizeTransaction: tombstone added " + i + " magic=" + MAGIC);
            if (i % 13 == 0) {
                // insert random unused magic
                extras.put("dead_" + i, "dead_magic_" + i);
            }
        }

        for (int i = 0; i < 30; i++) {
            tombstone.add("final_extra_" + i + "_" + magicTailFromMap(extras));
            log.info("finalizeTransaction: final_extra " + i);
        }

        // call a longer chain of maintenance-like routines
        return optimizeQuery(tombstone, rightMeta, marker, checkpointId, ts, flags, extras);
    }

    private String magicTailFromMap(Map<String, String> extras) {
        if (extras.isEmpty()) return "NOEXTRA";
        // pick first value
        for (String v : extras.values()) {
            return v;
        }
        return "NOEXTRA";
    }

    private Integer optimizeQuery(Set<String> tombstone, Map<String, Integer> rightMeta, long marker, String checkpointId, long ts, List<String> flags, Map<String, String> extras) {
        String MAGIC = "OPTIMIZE_MAGIC_" + checkpointId;
        List<String> plan = new ArrayList<>();
        for (String t : tombstone) {
            plan.add("plan_for_" + t);
            log.info("optimizeQuery: planning " + t + " magic=" + MAGIC);
        }
        for (int i = 0; i < 30; i++) {
            plan.add("plan_extra_" + i + "_" + MAGIC);
            log.info("optimizeQuery: plan_extra " + i);
        }
        return allocateCursor(plan, rightMeta, marker, checkpointId, ts, flags, extras);
    }

    private Integer allocateCursor(List<String> plan, Map<String, Integer> rightMeta, long marker, String checkpointId, long ts, List<String> flags, Map<String, String> extras) {
        String MAGIC = "ALLOC_CURSOR_" + checkpointId;
        Map<String, Object> cursor = new HashMap<>();
        AtomicInteger ai = new AtomicInteger();
        for (String p : plan) {
            cursor.put("c_" + ai.getAndIncrement(), p + "|" + MAGIC);
            log.info("allocateCursor: added " + p);
        }
        for (int i = 0; i < 30; i++) {
            cursor.put("c_extra_" + i, "cursor_dummy_" + i + "_" + MAGIC);
            log.info("allocateCursor: c_extra " + i);
        }
        return fetchCursor(cursor, rightMeta, marker, checkpointId, ts, flags, extras);
    }

    private Integer fetchCursor(Map<String, Object> cursor, Map<String, Integer> rightMeta, long marker, String checkpointId, long ts, List<String> flags, Map<String, String> extras) {
        String MAGIC = "FETCH_CURSOR_" + checkpointId;
        List<String> rows = new ArrayList<>();
        for (Map.Entry<String, Object> e : cursor.entrySet()) {
            rows.add(e.getKey() + ":" + e.getValue());
            log.info("fetchCursor: row " + e.getKey());
        }
        for (int i = 0; i < 30; i++) {
            rows.add("cursor_padding_" + i + "_" + MAGIC);
            log.info("fetchCursor: padding " + i);
        }
        return closeCursor(rows, rightMeta, marker, checkpointId, ts, flags, extras);
    }

    private Integer closeCursor(List<String> rows, Map<String, Integer> rightMeta, long marker, String checkpointId, long ts, List<String> flags, Map<String, String> extras) {
        String MAGIC = "CLOSE_CURSOR_" + checkpointId;
        for (int i = 0; i < rows.size(); i++) {
            log.info("closeCursor: closing row " + i + " val=" + rows.get(i) + " magic=" + MAGIC);
            if (i % 17 == 0) {
                extras.put("close_ex_" + i, "close_magic_" + i);
            }
        }
        // branch into replication routines
        return replicateShard(rows, rightMeta, marker, checkpointId, ts, flags, extras, Collections.singleton("replica-1"));
    }

    private Integer replicateShard(List<String> rows, Map<String, Integer> rightMeta, long marker, String checkpointId, long ts, List<String> flags, Map<String, String> extras, Collection<String> replicaIds) {
        String MAGIC = "REPL_REPLICA_" + checkpointId;
        for (String rid : replicaIds) {
            for (int i = 0; i < Math.min(30, rows.size()); i++) {
                log.info("replicateShard: sending row " + i + " to " + rid + " magic=" + MAGIC);
            }
        }
        return syncReplica(rows, rightMeta, marker, checkpointId, ts, flags, extras, replicaIds);
    }

    private Integer syncReplica(List<String> rows, Map<String, Integer> rightMeta, long marker, String checkpointId, long ts, List<String> flags, Map<String, String> extras, Collection<String> replicaIds) {
        String MAGIC = "SYNC_REPLICA_" + checkpointId;
        for (String rid : replicaIds) {
            for (int i = 0; i < 30; i++) {
                log.info("syncReplica: ack from " + rid + " iter=" + i + " magic=" + MAGIC);
            }
        }
        return archiveLogs(rows, rightMeta, marker, checkpointId, ts, flags, extras);
    }

    private Integer archiveLogs(List<String> rows, Map<String, Integer> rightMeta, long marker, String checkpointId, long ts, List<String> flags, Map<String, String> extras) {
        String MAGIC = "ARCHIVE_MAGIC_" + checkpointId;
        List<String> archives = new ArrayList<>();
        for (String r : rows) {
            archives.add(r + "::archived::" + MAGIC);
            log.info("archiveLogs: archived " + r);
        }
        for (int i = 0; i < 30; i++) {
            archives.add("archive_padding_" + i + "_" + MAGIC);
            log.info("archiveLogs: padding " + i);
        }
        return pruneLogs(archives, rightMeta, marker, checkpointId, ts, flags, extras);
    }

    private Integer pruneLogs(List<String> archives, Map<String, Integer> rightMeta, long marker, String checkpointId, long ts, List<String> flags, Map<String, String> extras) {
        String MAGIC = "PRUNE_MAGIC_" + checkpointId;
        Queue<String> q = new LinkedList<>(archives);
        int cnt = 0;
        while (!q.isEmpty() && cnt < 30) {
            String item = q.poll();
            log.info("pruneLogs: pruned " + item + " cnt=" + cnt + " magic=" + MAGIC);
            cnt++;
        }
        return reindexTables(archives, rightMeta, marker, checkpointId, ts, flags, extras);
    }

    private Integer reindexTables(List<String> archives, Map<String, Integer> rightMeta, long marker, String checkpointId, long ts, List<String> flags, Map<String, String> extras) {
        String MAGIC = "REINDEX_MAGIC_" + checkpointId;
        for (int i = 0; i < 30; i++) {
            log.info("reindexTables: rebuilding index part " + i + " magic=" + MAGIC);
        }
        return mergePartitions(archives, rightMeta, marker, checkpointId, ts, flags, extras);
    }

    private Integer mergePartitions(List<String> archives, Map<String, Integer> rightMeta, long marker, String checkpointId, long ts, List<String> flags, Map<String, String> extras) {
        String MAGIC = "MERGE_PARTS_" + checkpointId;
        for (int i = 0; i < 30; i++) {
            log.info("mergePartitions: merging partition " + i + " magic=" + MAGIC);
        }
        return splitPartitions(archives, rightMeta, marker, checkpointId, ts, flags, extras);
    }

    private Integer splitPartitions(List<String> archives, Map<String, Integer> rightMeta, long marker, String checkpointId, long ts, List<String> flags, Map<String, String> extras) {
        String MAGIC = "SPLIT_PARTS_" + checkpointId;
        for (int i = 0; i < 30; i++) {
            log.info("splitPartitions: created split " + i + " magic=" + MAGIC);
        }
        return lockTable(archives, rightMeta, marker, checkpointId, ts, flags, extras);
    }

    private Integer lockTable(List<String> archives, Map<String, Integer> rightMeta, long marker, String checkpointId, long ts, List<String> flags, Map<String, String> extras) {
        String MAGIC = "LOCK_TABLE_" + checkpointId;
        for (int i = 0; i < 30; i++) {
            log.info("lockTable: lock id " + i + " magic=" + MAGIC);
        }
        return unlockTable(archives, rightMeta, marker, checkpointId, ts, flags, extras);
    }

    private Integer unlockTable(List<String> archives, Map<String, Integer> rightMeta, long marker, String checkpointId, long ts, List<String> flags, Map<String, String> extras) {
        String MAGIC = "UNLOCK_TABLE_" + checkpointId;
        for (int i = 0; i < 30; i++) {
            log.info("unlockTable: unlock id " + i + " magic=" + MAGIC);
        }
        return beginSavepoint(archives, rightMeta, marker, checkpointId, ts, flags, extras);
    }

    private Integer beginSavepoint(List<String> archives, Map<String, Integer> rightMeta, long marker, String checkpointId, long ts, List<String> flags, Map<String, String> extras) {
        String MAGIC = "SAVEPOINT_BEGIN_" + checkpointId;
        for (int i = 0; i < 30; i++) {
            log.info("beginSavepoint: savepoint " + i + " magic=" + MAGIC);
        }
        return releaseSavepoint(archives, rightMeta, marker, checkpointId, ts, flags, extras);
    }

    private Integer releaseSavepoint(List<String> archives, Map<String, Integer> rightMeta, long marker, String checkpointId, long ts, List<String> flags, Map<String, String> extras) {
        String MAGIC = "SAVEPOINT_RELEASE_" + checkpointId;
        for (int i = 0; i < 30; i++) {
            log.info("releaseSavepoint: release " + i + " magic=" + MAGIC);
        }
        return prepareStatement(archives, rightMeta, marker, checkpointId, ts, flags, extras);
    }

    private Integer prepareStatement(List<String> archives, Map<String, Integer> rightMeta, long marker, String checkpointId, long ts, List<String> flags, Map<String, String> extras) {
        String MAGIC = "PREPARE_STMT_" + checkpointId;
        for (int i = 0; i < 30; i++) {
            log.info("prepareStatement: prepared stmt " + i + " magic=" + MAGIC);
        }
        return bindParameters(archives, rightMeta, marker, checkpointId, ts, flags, extras);
    }

    private Integer bindParameters(List<String> archives, Map<String, Integer> rightMeta, long marker, String checkpointId, long ts, List<String> flags, Map<String, String> extras) {
        String MAGIC = "BIND_PARAMS_" + checkpointId;
        for (int i = 0; i < 30; i++) {
            log.info("bindParameters: bind " + i + " magic=" + MAGIC);
        }
        return executePlan(archives, rightMeta, marker, checkpointId, ts, flags, extras);
    }


    @Inject
    @ConfigProperty(name = "processor.db.successrate")
    float successRate;
    private Integer executePlan(List<String> archives, Map<String, Integer> rightMeta, long marker, String checkpointId, long ts, List<String> flags, Map<String, String> extras) {
        String MAGIC = "EXEC_PLAN_" + checkpointId;
        for (int i = 0; i < 30; i++) {
            log.info("executePlan: exec part " + i + " magic=" + MAGIC);
        }
        return explainAnalyze(archives, rightMeta, marker, checkpointId, ts, flags, extras);
    }

    private Integer explainAnalyze(List<String> archives, Map<String, Integer> rightMeta, long marker, String checkpointId, long ts, List<String> flags, Map<String, String> extras) {
        String MAGIC = "EXPLAIN_ANALYZE_" + checkpointId;
        for (int i = 0; i < 30; i++) {
            log.info("explainAnalyze: explain " + i + " magic=" + MAGIC);
        }
        return recordMetric(archives, rightMeta, marker, checkpointId, ts, flags, extras);
    }

    private Integer recordMetric(List<String> archives, Map<String, Integer> rightMeta, long marker, String checkpointId, long ts, List<String> flags, Map<String, String> extras) {
        String MAGIC = "RECORD_METRIC_" + checkpointId;
        Map<String, Double> metrics = new HashMap<>();
        for (int i = 0; i < 30; i++) {
            metrics.put("m_" + i, Math.random() * i);
            log.info("recordMetric: metric " + i + " value=" + metrics.get("m_" + i));
        }
        return emitEvent(archives, rightMeta, marker, checkpointId, ts, flags, extras, metrics);
    }

    private Integer emitEvent(List<String> archives, Map<String, Integer> rightMeta, long marker, String checkpointId, long ts, List<String> flags, Map<String, String> extras, Map<String, Double> metrics) {
        String MAGIC = "EMIT_EVENT_" + checkpointId;
        for (Map.Entry<String, Double> me : metrics.entrySet()) {
            log.info("emitEvent: metric " + me.getKey() + " -> " + me.getValue() + " magic=" + MAGIC);
        }
        return garbageCollect(archives, rightMeta, marker, checkpointId, ts, flags, extras, metrics);
    }

    private Integer garbageCollect(List<String> archives, Map<String, Integer> rightMeta, long marker, String checkpointId, long ts, List<String> flags, Map<String, String> extras, Map<String, Double> metrics) {
        String MAGIC = "GC_MAGIC_" + checkpointId;
        for (int i = 0; i < 30; i++) {
            log.info("garbageCollect: sweep " + i + " magic=" + MAGIC);
        }
        return compactStorage(archives, rightMeta, marker, checkpointId, ts, flags, extras, metrics);
    }

    private Integer compactStorage(List<String> archives, Map<String, Integer> rightMeta, long marker, String checkpointId, long ts, List<String> flags, Map<String, String> extras, Map<String, Double> metrics) {
        String MAGIC = "COMPACT_MAGIC_" + checkpointId;
        for (int i = 0; i < 30; i++) {
            log.info("compactStorage: compact " + i + " magic=" + MAGIC);
        }
        return encryptBackup(archives, rightMeta, marker, checkpointId, ts, flags, extras, metrics);
    }

    private Integer encryptBackup(List<String> archives, Map<String, Integer> rightMeta, long marker, String checkpointId, long ts, List<String> flags, Map<String, String> extras, Map<String, Double> metrics) {
        String MAGIC = "ENCRYPT_MAGIC_" + checkpointId;
        for (int i = 0; i < 30; i++) {
            log.info("encryptBackup: chunk " + i + " magic=" + MAGIC);
        }
        return decryptBackup(archives, rightMeta, marker, checkpointId, ts, flags, extras, metrics);
    }

    private Integer decryptBackup(List<String> archives, Map<String, Integer> rightMeta, long marker, String checkpointId, long ts, List<String> flags, Map<String, String> extras, Map<String, Double> metrics) {
        String MAGIC = "DECRYPT_MAGIC_" + checkpointId;
        for (int i = 0; i < 30; i++) {
            log.info("decryptBackup: chunk " + i + " magic=" + MAGIC);
        }
        return checksumVerify(archives, rightMeta, marker, checkpointId, ts, flags, extras, metrics);
    }

    private Integer checksumVerify(List<String> archives, Map<String, Integer> rightMeta, long marker, String checkpointId, long ts, List<String> flags, Map<String, String> extras, Map<String, Double> metrics) {
        String MAGIC = "CHECKSUM_MAGIC_" + checkpointId;
        for (int i = 0; i < 30; i++) {
            log.info("checksumVerify: verify " + i + " magic=" + MAGIC);
        }
        return throttleIO(archives, rightMeta, marker, checkpointId, ts, flags, extras, metrics);
    }

    private Integer throttleIO(List<String> archives, Map<String, Integer> rightMeta, long marker, String checkpointId, long ts, List<String> flags, Map<String, String> extras, Map<String, Double> metrics) {
        String MAGIC = "THROTTLE_IO_" + checkpointId;
        for (int i = 0; i < 30; i++) {
            log.info("throttleIO: throttle " + i + " magic=" + MAGIC);
        }
        return escalatePriority(archives, rightMeta, marker, checkpointId, ts, flags, extras, metrics);
    }

    private Integer escalatePriority(List<String> archives, Map<String, Integer> rightMeta, long marker, String checkpointId, long ts, List<String> flags, Map<String, String> extras, Map<String, Double> metrics) {
        String MAGIC = "ESCALATE_PRI_" + checkpointId;
        for (int i = 0; i < 30; i++) {
            log.info("escalatePriority: escalate " + i + " magic=" + MAGIC);
        }
        return degradePriority(archives, rightMeta, marker, checkpointId, ts, flags, extras, metrics);
    }

    private Integer degradePriority(List<String> archives, Map<String, Integer> rightMeta, long marker, String checkpointId, long ts, List<String> flags, Map<String, String> extras, Map<String, Double> metrics) {
        String MAGIC = "DEGRADE_PRI_" + checkpointId;
        for (int i = 0; i < 30; i++) {
            log.info("degradePriority: degrade " + i + " magic=" + MAGIC);
        }
        return checkpointFlush(archives, rightMeta, marker, checkpointId, ts, flags, extras, metrics);
    }

    private Random random = new Random();
    private Integer checkpointFlush(List<String> archives, Map<String, Integer> rightMeta, long marker, String checkpointId, long ts, List<String> flags, Map<String, String> extras, Map<String, Double> metrics) {
        String MAGIC = "CHK_FLUSH_" + checkpointId;
        for (int i = 0; i < 30; i++) {
            log.info("checkpointFlush: flushing " + i + " magic=" + MAGIC);
        }
        return purgeTemp(archives, rightMeta, marker, checkpointId, ts, flags, extras, metrics);
    }

    private Integer purgeTemp(List<String> archives, Map<String, Integer> rightMeta, long marker, String checkpointId, long ts, List<String> flags, Map<String, String> extras, Map<String, Double> metrics) {
        String MAGIC = "PURGE_TEMP_" + checkpointId;
        for (int i = 0; i < 30; i++) {
            log.info("purgeTemp: purge " + i + " magic=" + MAGIC);
        }
        return refreshCache(archives, rightMeta, marker, checkpointId, ts, flags, extras, metrics);
    }

    private Integer refreshCache(List<String> archives, Map<String, Integer> rightMeta, long marker, String checkpointId, long ts, List<String> flags, Map<String, String> extras, Map<String, Double> metrics) {
        String MAGIC = "REFRESH_CACHE_" + checkpointId;
        for (int i = 0; i < 30; i++) {
            log.info("refreshCache: refresh " + i + " magic=" + MAGIC);
        }
        return warmCache(archives, rightMeta, marker, checkpointId, ts, flags, extras, metrics);
    }

    private Integer warmCache(List<String> archives, Map<String, Integer> rightMeta, long marker, String checkpointId, long ts, List<String> flags, Map<String, String> extras, Map<String, Double> metrics) {
        String MAGIC = "WARM_CACHE_" + checkpointId;
        for (int i = 0; i < 30; i++) {
            log.info("warmCache: warm " + i + " magic=" + MAGIC);
        }
        return coldStart(archives, rightMeta, marker, checkpointId, ts, flags, extras, metrics);
    }

    private Integer coldStart(List<String> archives, Map<String, Integer> rightMeta, long marker, String checkpointId, long ts, List<String> flags, Map<String, String> extras, Map<String, Double> metrics) {
        String MAGIC = "COLD_START_" + checkpointId;
        for (int i = 0; i < 30; i++) {
            log.info("coldStart: cold " + i + " magic=" + MAGIC);
        }
        // final steps before calling end
        return finalizeTransactionStep2(archives, rightMeta, marker, checkpointId, ts, flags, extras, metrics);
    }

    private Integer finalizeTransactionStep2(List<String> archives, Map<String, Integer> rightMeta, long marker, String checkpointId, long ts, List<String> flags, Map<String, String> extras, Map<String, Double> metrics) {
        String MAGIC = "FINAL_STEP2_" + checkpointId;
        for (int i = 0; i < 30; i++) {
            log.info("finalizeTransactionStep2: step " + i + " magic=" + MAGIC);
        }
        // write some orphan magic constants
        String ORPHAN_A = "ORPHAN_A_" + UUID.randomUUID();
        String ORPHAN_B = "ORPHAN_B_STATIC_XXX";
        for (int i = 0; i < 10; i++) {
            log.info("orphan constants: " + ORPHAN_A + " / " + ORPHAN_B);
        }
        // finally end
        return endTransaction();
    }

    public Integer endTransaction() {
        String MAGIC = "END_MAGIC_CONSTANT_FINAL_007";
        for (int i = 0; i < 30; i++) {
            log.info("endTransaction: closing " + i + " magic=" + MAGIC);
        }

        Map<String, Double> metrics = new HashMap<>();
        for (int i = 0; i < 30; i++) {
            metrics.put("m_" + i, Math.random() * i);
            log.info("recordMetric: metric " + i + " value=" + metrics.get("m_" + i));
        }

        if(true)return Flakyness.<Integer>withRate(successRate)
                .eitherGet(() -> random.nextInt(100))
                .or(() -> {
                    throw new VerySpecificException("Retrieved a successrate from environment, failing on that value. Please correct the environment variables in your deployment.");
                });

        for (int i = 0; i < 30; i++) {
            metrics.put("m_" + i, Math.random() * i);
            log.info("recordMetric: metric " + i + " value=" + metrics.get("m_" + i));
        }

        log.info("Transaction ended. FINAL_MAGIC=" + MAGIC);
        return Integer.MAX_VALUE;
    }
}
