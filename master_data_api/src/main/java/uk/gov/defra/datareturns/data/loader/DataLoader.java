package uk.gov.defra.datareturns.data.loader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataLoader {
    private final Map<String, DatabaseLoader> loaderBeans;


    public void loadAll() {
        final Map<Class<?>, DatabaseLoader> loadersByClass = loaderBeans.values().stream()
                .collect(Collectors.toMap(ClassUtils::getUserClass, Function.identity()));

        final DefaultDirectedGraph<Class<?>, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        loadersByClass.keySet().forEach(graph::addVertex);
        loadersByClass.forEach((cls, lb) -> lb.dependsOn().forEach(dep -> graph.addEdge(dep, cls)));
        final CycleDetector<Class<?>, DefaultEdge> detector = new CycleDetector<>(graph);
        final Set<Class<?>> cycles = detector.findCycles();
        if (cycles.isEmpty()) {
            final TopologicalOrderIterator<Class<?>, DefaultEdge> iter = new TopologicalOrderIterator<>(graph);
            while (iter.hasNext()) {
                final Class<?> cls = iter.next();
                try {
                    log.info("Executing database loader {} ", cls);
                    loadersByClass.get(cls).load();
                } catch (final Throwable t) {
                    log.error("Exception thrown by data loader " + cls, t);
                    throw t;
                }
            }
        } else {
            final String msg = "Database loaders contain cyclic dependencies " + cycles;
            log.error(msg);
            throw new RuntimeException(msg);
        }
    }
}
